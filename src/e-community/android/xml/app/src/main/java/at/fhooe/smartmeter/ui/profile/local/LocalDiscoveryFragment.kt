package at.fhooe.smartmeter.ui.profile.local

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.adapters.LocalAdapter
import at.fhooe.smartmeter.databinding.FragmentLocalDiscoveryBinding
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.LocalDeviceConfig
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.services.LocalPairingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import local.org.openapitools.client.apis.PairingApi
import java.util.*

class LocalDiscoveryFragment : Fragment(),
                               Observer,
                               LocalAdapter.LocalConnectionListener {

    private var _binding: FragmentLocalDiscoveryBinding? = null
    private lateinit var mLocalPairingManager: LocalPairingManager
    private lateinit var mLocalAdapter: LocalAdapter

    private lateinit var mIFragmentMessageService: IFragmentMessageService

    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalDiscoveryBinding.inflate(inflater, container, false)

        // set refreshing icon to true
        mBinding.fragmentLocalDiscoverySwipeRefresh.isRefreshing = true

        // a localPairingManager as Observer and start discovery service
        mLocalPairingManager = LocalPairingManager(requireContext())
        mLocalPairingManager.addObserver(this)
        mLocalPairingManager.discover()

        mLocalAdapter = LocalAdapter()
        mLocalAdapter.setLocalConnectionListener(this)

        with(mBinding.fragmentLocalDiscoveryDevices) {
            adapter = mLocalAdapter
            setHasFixedSize(true)
        }

        showWiredConnectionInfo()

        return mBinding.root
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    /**
     * destructor
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroy")

        _binding = null
        mLocalPairingManager.deleteObserver(this)
    }

    /**
     * for communication between fragment/activity
     * */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentMessageService = context as IFragmentMessageService
    }

    /**
     * observer for nsd manager, gets called when a new local device is discovered
     * @see Observable
     * @see Any
     */
    override fun update(o: Observable?, arg: Any?) {
        if (arg is MutableList<*>) {
            activity?.runOnUiThread {
                val list = arg.filterIsInstance<Local>()

                if (list.isNotEmpty()) {
                    mLocalAdapter.setLocals(arg.filterIsInstance<Local>())
                }

                mBinding.fragmentLocalDiscoverySwipeRefresh.isRefreshing = false
            }
        }
    }

    /**
     * connect to selected local device
     * @see Local
     */
    override fun onConnect(local: Local) {
        Log.d(TAG, "connect to $local")

        LocalDeviceConfig.setLocal(local)

        lifecycleScope.launch(Dispatchers.IO) {
            val pairingApi = PairingApi("http://${local.ipAddress}:${Constants.HTTP_LOCAL_PORT}")

            val status = pairingApi.pairingStatusGet()
            Log.e(TAG, "status: $status")
            LocalDeviceConfig.setStatus(status)
            status.smartMeter?.let { LocalDeviceConfig.setSmartMeterModel(it) }

            activity?.runOnUiThread {
                mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_SUMMARY, null)
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