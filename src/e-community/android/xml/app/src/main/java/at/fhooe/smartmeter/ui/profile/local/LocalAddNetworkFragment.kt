package at.fhooe.smartmeter.ui.profile.local

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.adapters.AvailableNetworksAdapter
import at.fhooe.smartmeter.data.PairingRepository
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.WifiNetwork
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.services.LocalPairingManager
import at.fhooe.smartmeter.viewmodel.WifiNetworkViewModel
import at.fhooe.smartmeter.viewmodel.WifiNetworkViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.databinding.FragmentLocalAddNetworkBinding
import at.fhooe.smartmeter.models.LocalDeviceConfig
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import local.org.openapitools.client.apis.PairingApi
import local.org.openapitools.client.models.StatusDto

class LocalAddNetworkFragment : Fragment(), AvailableNetworksAdapter.WifiConnectListener,
    CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private var _binding: FragmentLocalAddNetworkBinding? = null

    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mLocalPairingManager: LocalPairingManager
    private lateinit var mAvailableNetworksAdapter: AvailableNetworksAdapter
    private lateinit var mWifiNetworkViewModel: WifiNetworkViewModel
    private lateinit var mStatus: StatusDto

    private var mLocalDevice: Local? = null
    private var mPairingRepository = PairingRepository()
    private val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalAddNetworkBinding.inflate(inflater, container, false)

        mLocalDevice = LocalDeviceConfig.getLocal()

        mAvailableNetworksAdapter = AvailableNetworksAdapter()
        mAvailableNetworksAdapter.setWifiConnectListener(this)

        with(mBinding.fragmentLocalAddNetworkNetworks) {
            setHasFixedSize(true)
            adapter = mAvailableNetworksAdapter
        }

        mLocalPairingManager = LocalPairingManager(requireContext())

        val wifiNetworkViewModelFactory = WifiNetworkViewModelFactory(mPairingRepository)
        mWifiNetworkViewModel = ViewModelProvider(this, wifiNetworkViewModelFactory)[WifiNetworkViewModel::class.java]

        // set refreshListener -> loadWifiNetworks()
        mBinding.fragmentLocalAddNetworkSwipeRefresh.setOnRefreshListener { loadWifiNetworks() }

        mBinding.fragmentLocalAddNetworkWiredConnection.setOnCheckedChangeListener(this)

        // load wifi networks when fragment gets created
        loadWifiNetworks()

        return mBinding.root
    }

    /**
     * destructor
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * for communication between fragment/activity
     * @see Context
     * */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentMessageService = context as IFragmentMessageService
    }

    /**
     * user tries to connect to this wifiNetwork
     * start LocalConnectWifiFragment to enter a password
     * @see WifiNetwork
     */
    override fun onConnect(wifiNetwork: WifiNetwork) {
        val bundle = bundleOf(
            Constants.BUNDLE_WIFI_SSID to wifiNetwork.SSID,
            Constants.BUNDLE_LOCAL_DEVICE_IP to mLocalDevice?.ipAddress
        )

        mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_CONNECT_WIFI, bundle)
    }

    /**
     * when user select wired-connection disable recyclerview (and reversed)
     */
    override fun onCheckedChanged(view: CompoundButton?, checked: Boolean) {

        when(checked) {
            true -> {
                with(mBinding) {
                    fragmentLocalAddNetworkAvailableNetworksText.visibility = View.INVISIBLE
                    fragmentLocalAddNetworkNetworks.visibility = View.INVISIBLE
                }

                // call backend and ask with ethernet is active and has internet connection
                checkEthConnection()
            }

            false -> {
                with(mBinding) {
                    fragmentLocalAddNetworkAvailableNetworksText.visibility = View.VISIBLE
                    fragmentLocalAddNetworkNetworks.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * save networking settings and go to summary fragment
     */
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.loading_screen_connection_ok -> {
                LocalDeviceConfig.setStatus(mStatus)

                mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_DEVICE_SETTINGS, null)
            }
        }
    }

    /**
     * load wifi networks from local backend
     */
    private fun loadWifiNetworks() {
        mBinding.fragmentLocalAddNetworkSwipeRefresh.isRefreshing = true

        mWifiNetworkViewModel.getWifiNetworks(mLocalDevice)

        // only collect wifi networks when the fragment is in STARTED (visible) state
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mWifiNetworkViewModel.mUiState.collect {
                    mAvailableNetworksAdapter.setAvailableNetworks(it.wifiNetworks)
                    mBinding.fragmentLocalAddNetworkSwipeRefresh.isRefreshing = false
                }
            }
        }
    }

    /**
     * check if local device has an active ethernet connection,
     * if success check if internet connection is successful
     */
    private fun checkEthConnection() {

        val progressDialogBuilder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.check_connection))
        }

        val progressDialogView = layoutInflater.inflate(R.layout.loading_screen_connection, null)
        val progressBar = progressDialogView.findViewById<ProgressBar>(R.id.loading_screen_connection_progress)

        val textCheckWired = progressDialogView.findViewById<TextView>(R.id.loading_screen_connection_text_1)
        val textCheckInternet = progressDialogView.findViewById<TextView>(R.id.loading_screen_connection_text_2)
        val statusImageNetwork = progressDialogView.findViewById<ImageView>(R.id.loading_screen_connection_text_status_1)
        val statusImageInternet = progressDialogView.findViewById<ImageView>(R.id.loading_screen_connection_text_status_2)
        val btnOK = progressDialogView.findViewById<Button>(R.id.loading_screen_connection_ok)

        btnOK.setOnClickListener(this)
        btnOK.visibility = View.GONE

        // set strings dynamically in the progress dialog
        textCheckWired.text = getString(R.string.check_wired_connection)
        textCheckInternet.text = getString(R.string.check_internet)

        progressDialogBuilder.setView(progressDialogView)

        // close dialog
        progressDialogBuilder.setNegativeButton(R.string.cancel,
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }
        )

        progressDialogBuilder.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val pairingApi = PairingApi("http://${mLocalDevice?.ipAddress}:${Constants.HTTP_LOCAL_PORT}")
            var connected = true

            mStatus = pairingApi.pairingStatusGet()

            // check internet connection
            mStatus.isConnectedToInternet.let {
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    statusImageInternet.visibility = View.VISIBLE

                    if (it!!) {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_connection_sucess)
                            .into(statusImageInternet)
                    }
                    else {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_connection_not_sucess)
                            .into(statusImageInternet)

                        connected = false
                    }
                }
            }

            // check wired network connection
            mStatus.isWiredConnected.let {
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    statusImageNetwork.visibility = View.VISIBLE

                    if (it!!) {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_connection_sucess)
                            .into(statusImageNetwork)
                    }
                    else {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_connection_not_sucess)
                            .into(statusImageNetwork)

                        connected = false
                    }
                }
            }

            // now use can press OK (when the local device is connected wired + internet)
            requireActivity().runOnUiThread {
                if (connected) {
                    btnOK.visibility = View.VISIBLE
                }
                else {
                    Log.e(TAG, "local device is not correctly connected to wired/internet...")
                }
            }
        }
    }

    /**
     * show user an information, that a ethernet cable is necessary for the pairing process
     */
    private fun showWiredConnectionInfo() {
        val infoDialogBuilder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.pairing_info))
        }

        infoDialogBuilder.setMessage(R.string.wired_connect_info)
        infoDialogBuilder.setPositiveButton(R.string.OK) { _, _ -> }
        infoDialogBuilder.show()
    }
}