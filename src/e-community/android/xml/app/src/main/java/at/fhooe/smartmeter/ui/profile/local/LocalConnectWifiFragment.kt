package at.fhooe.smartmeter.ui.profile.local

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.databinding.FragmentLocalConnectWifiBinding
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.LocalDeviceConfig
import at.fhooe.smartmeter.models.WifiNetwork
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import local.org.openapitools.client.apis.PairingApi
import local.org.openapitools.client.models.NetworkConnectModel
import org.w3c.dom.Text
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [LocalConnectWifiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocalConnectWifiFragment : Fragment(), View.OnClickListener, TextWatcher {

    private var _binding: FragmentLocalConnectWifiBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mWifiNetwork: WifiNetwork
    private lateinit var mLocalDevice: Local
    private lateinit var mIFragmentMessageService: IFragmentMessageService

    private var mCheckAlertBuilder: AlertDialog.Builder? = null
    private var mCheckAlert: AlertDialog? = null
    private var mCheckAlertView: View? = null
    private var mCheckWifiImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalConnectWifiBinding.inflate(inflater, container, false)

        arguments?.let {
            mWifiNetwork = WifiNetwork(it.getString(Constants.BUNDLE_WIFI_SSID), 100)
            mLocalDevice = Local("", it.getString(Constants.BUNDLE_LOCAL_DEVICE_IP)!!)
        }

        mBinding.fragmentLocalConnectWifiPassword.addTextChangedListener(this)
        mBinding.fragmentLocalConnectWifiSsid.text = mWifiNetwork.SSID

        mBinding.fragmentLocalConnectWifiConnect.isEnabled = false
        mBinding.fragmentLocalConnectWifiConnect.setOnClickListener(this)

        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mIFragmentMessageService = context as IFragmentMessageService
    }

    /**
     * send post request to local backend and try
     * to connect to this wifi network
     * @see View
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            mBinding.fragmentLocalConnectWifiConnect.id -> {

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val pairingApi = PairingApi("http://${mLocalDevice.ipAddress}:${Constants.HTTP_LOCAL_PORT}")
                        val result = pairingApi.pairingNetworkAddPost(
                            NetworkConnectModel(
                                mWifiNetwork.SSID,
                                mBinding.fragmentLocalConnectWifiPassword.text.toString()
                            )
                        )

                        CoroutineScope(Dispatchers.Main).launch {
                            mCheckAlertBuilder = this.let {
                                AlertDialog.Builder(requireContext())
                                    .setTitle(getString(R.string.check_connection))
                            }

                            mCheckAlertView =
                                layoutInflater.inflate(R.layout.loading_screen_connection, null)
                            val progressBar =
                                mCheckAlertView?.findViewById<ProgressBar>(R.id.loading_screen_connection_progress)

                            val textCheckWifi =
                                mCheckAlertView?.findViewById<TextView>(R.id.loading_screen_connection_text_1)
                            val textCheckInternet =
                                mCheckAlertView?.findViewById<TextView>(R.id.loading_screen_connection_text_2)

                            mCheckWifiImageView = mCheckAlertView?.findViewById(R.id.loading_screen_connection_text_status_1)
                            val btnOK =
                                mCheckAlertView?.findViewById<Button>(R.id.loading_screen_connection_ok)

                            btnOK?.setOnClickListener(this@LocalConnectWifiFragment)

                            // set strings dynamically in the progress dialog
                            textCheckWifi?.text = getString(R.string.check_wifi_settings)
                            textCheckInternet?.text = getString(R.string.check_connection)

                            mCheckAlertBuilder?.setView(mCheckAlertView)
                            mCheckAlertBuilder?.show()
                        }
                    }
                    catch (e: Exception) {
                        Log.e(
                            TAG,
                            "error when sending network details: ${e.localizedMessage}"
                        )
                    }
                }

                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {

                        CoroutineScope(Dispatchers.IO).launch {
                            val pairingApi = PairingApi("http://${mLocalDevice.ipAddress}:${Constants.HTTP_LOCAL_PORT}")

                            try {
                                val status = pairingApi.pairingStatusGet()
                                LocalDeviceConfig.setStatus(status)

                                Log.d(TAG, "status: $status")

                                requireActivity().runOnUiThread {
                                    mCheckAlertView?.findViewById<ProgressBar>(R.id.loading_screen_connection_progress)?.visibility = View.GONE
                                    mCheckAlertView?.findViewById<Button>(R.id.loading_screen_connection_ok)?.visibility = View.VISIBLE
                                    var configCorrect = true

                                    var view = mCheckAlertView?.findViewById<ImageView>(R.id.loading_screen_connection_text_status_1)
                                    view?.let {
                                        view!!.visibility = View.VISIBLE

                                        status.wifiSSID?.let {
                                            if (it == mWifiNetwork.SSID) {
                                                Glide.with(requireContext())
                                                    .load(R.drawable.ic_connection_sucess)
                                                    .into(view!!)
                                            }
                                            else {
                                                Glide.with(requireContext())
                                                    .load(R.drawable.ic_connection_not_sucess)
                                                    .into(view!!)

                                                configCorrect = false
                                            }
                                        }
                                    }

                                    view = mCheckAlertView?.findViewById<ImageView>(R.id.loading_screen_connection_text_status_2)
                                    view?.let {
                                        view.visibility = View.VISIBLE

                                        status.isConnectedToInternet?.let {
                                            if (it) {
                                                Glide.with(requireContext())
                                                    .load(R.drawable.ic_connection_sucess)
                                                    .into(view)
                                            }
                                            else {
                                                Glide.with(requireContext())
                                                    .load(R.drawable.ic_connection_not_sucess)
                                                    .into(view)

                                                configCorrect = false
                                            }
                                        }
                                    }

                                    LocalDeviceConfig.setFullyConfigured(configCorrect)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "error when requesting status of local device: ${e.localizedMessage}"
                                )
                            }
                        }

                    }
                }, Constants.CHECK_SIGNALR_TIMER.toLong())
            }

            R.id.loading_screen_connection_ok -> {
                requireActivity().runOnUiThread {
                    mCheckAlertView?.visibility = View.GONE
                }

                if (LocalDeviceConfig.getFullyConfigured()) {
                    mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_SUMMARY, null)
                }
            }
        }
    }

    /**
     * user can only connect when length of enter password
     * is greater than Constants.WIFI_PW_MIN_LENGTH (8)
     * @param text password from editText
     * @see Editable
     */
    override fun afterTextChanged(text: Editable?) {
        text?.chars().let {
            if (it != null) {
                if (it.count() >= Constants.WIFI_PW_MIN_LENGTH) {
                    mBinding.fragmentLocalConnectWifiConnect.isEnabled = true
                    mBinding.fragmentLocalConnectWifiConnect.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryDarkColor))
                }

            }
        }
    }

    /* not important */
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    /* not important */
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}