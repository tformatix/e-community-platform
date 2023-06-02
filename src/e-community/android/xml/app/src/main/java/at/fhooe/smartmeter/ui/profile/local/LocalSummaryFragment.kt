package at.fhooe.smartmeter.ui.profile.local

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.databinding.FragmentLocalSummaryBinding
import at.fhooe.smartmeter.models.LocalDeviceConfig
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.Api
import at.fhooe.smartmeter.util.EncryptedPreferences
import at.fhooe.smartmeter.util.Util
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import local.org.openapitools.client.models.CloudConnectModel
import org.openapitools.client.apis.PairingApi
import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.models.CreateSmartMeterDto
import org.openapitools.client.models.CreateSmartMeterModel
import java.lang.Exception

class LocalSummaryFragment : Fragment(), View.OnClickListener, View.OnTouchListener {

    private var _binding : FragmentLocalSummaryBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mEncryptedPreferences: EncryptedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalSummaryBinding.inflate(layoutInflater, container, false)

        mEncryptedPreferences = EncryptedPreferences(requireContext())

        val status = LocalDeviceConfig.getStatus()
        val smartMeterModel = LocalDeviceConfig.getSmartMeterModel()

        with(mBinding) {
            fragmentLocalSummaryNetworkInternet.isChecked = status.isConnectedToInternet!!

            fragmentLocalSummaryNetworkConnection.setText(Util.getConnectionString(status))

            fragmentLocalSummaryBtnConnectCloud.setOnClickListener(this@LocalSummaryFragment)

            fragmentLocalSummaryNetworkEdit.setOnTouchListener(this@LocalSummaryFragment)
            fragmentLocalSummaryDeviceSettingsEdit.setOnTouchListener(this@LocalSummaryFragment)

            fragmentLocalSummaryIsMain.isChecked = smartMeterModel.isMain!!
            fragmentLocalSummaryIsOverflowFeedIn.isChecked = smartMeterModel.isOverflowFeedIn!!
            fragmentLocalSummaryIsDirectFeedIn.isChecked = smartMeterModel.isDirectFeedIn!!
            fragmentLocalSummaryMeasureConsumption.isChecked = smartMeterModel.measuresConsumption!!
            fragmentLocalSummaryMeasureFeedIn.isChecked = smartMeterModel.measuresFeedIn!!

            // quickfix for the demo
            if (smartMeterModel.name == "string") {
                fragmentLocalSummaryDeviceName.setText("")
                fragmentLocalSummaryDeviceDescription.setText("")
            }
            else {
                fragmentLocalSummaryDeviceName.setText(smartMeterModel.name)
                fragmentLocalSummaryDeviceDescription.setText(smartMeterModel.description)
            }

            fragmentLocalSummaryAesKey.text = smartMeterModel.aesKey
        }

        return mBinding.root
    }

    /**
     * save device name to LocalDeviceConfig
     */
    override fun onPause() {
        super.onPause()

        val smartMeterModel = LocalDeviceConfig.getSmartMeterModel()
        smartMeterModel.name = mBinding.fragmentLocalSummaryDeviceName.text.toString()
        smartMeterModel.description = mBinding.fragmentLocalSummaryDeviceDescription.text.toString()
        LocalDeviceConfig.setSmartMeterModel(smartMeterModel)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mIFragmentMessageService = context as IFragmentMessageService
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            mBinding.fragmentLocalSummaryBtnConnectCloud.id -> {

                with(mBinding) {
                    if (fragmentLocalSummaryDeviceName.text.isNullOrEmpty()) {
                        fragmentLocalSummaryDeviceName.error = getString(R.string.not_empty)
                        return
                    }
                    if (fragmentLocalSummaryDeviceDescription.text.isNullOrEmpty()) {
                        fragmentLocalSummaryDeviceName.error = getString(R.string.not_empty)
                        return
                    }
                }

                connectToCloud()
            }

            R.id.loading_screen_connection_ok -> {
                if (LocalDeviceConfig.getFullyConfigured()) {
                    Log.d(TAG, "SmartMeter fully configured")
                    //IFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_ADD_NETWORK, null)
                }
            }
        }
    }


    /**
     * try to connect to cloud
     * create a Smart Meter Model
     */
    private fun connectToCloud() {

        // load refresh token from sharedPreferences
        val refreshToken = mEncryptedPreferences.sharedPreferences?.getString(getString(R.string.shared_prefs_refresh_token), "not authorized")

        val createSmartMeterModel = CreateSmartMeterModel(refreshToken,
            mBinding.fragmentLocalSummaryDeviceName.text.toString(),
            mBinding.fragmentLocalSummaryDeviceDescription.text.toString(),
        false, false, false, false, false)

        val progressDialogBuilder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.check_connection))
        }

        val progressDialogView = layoutInflater.inflate(R.layout.loading_screen_connection, null)
        val progressBar = progressDialogView.findViewById<ProgressBar>(R.id.loading_screen_connection_progress)

        val textCreateSmartMeter = progressDialogView.findViewById<TextView>(R.id.loading_screen_connection_text_1)
        val textInitCloud = progressDialogView.findViewById<TextView>(R.id.loading_screen_connection_text_2)
        val statusCreateSmartMeterCloud = progressDialogView.findViewById<ImageView>(R.id.loading_screen_connection_text_status_1)
        val statusCloudConnection = progressDialogView.findViewById<ImageView>(R.id.loading_screen_connection_text_status_2)
        val btnOK = progressDialogView.findViewById<Button>(R.id.loading_screen_connection_ok)
        btnOK.setOnClickListener(this)

        // set strings dynamically in the progress dialog
        textCreateSmartMeter.text = getString(R.string.create_update_smart_meter)
        textInitCloud.text = getString(R.string.init_cloud_connection)

        progressDialogBuilder.setView(progressDialogView)
        progressDialogBuilder.show()

        // create/update SmartMeter
        Api.authorizedBackendCall(requireContext(), null) {
            val pairingCloudApi = PairingApi(Constants.HTTP_BASE_URL_CLOUD)

            // create/update smart meter in cloud
            val createSmartMeterResult =
                pairingCloudApi.pairingCreateSmartMeterPost(createSmartMeterModel)

            requireActivity().runOnUiThread {
                statusCloudConnection.visibility = View.VISIBLE
                statusCreateSmartMeterCloud.visibility = View.VISIBLE
            }

            if (createSmartMeterResult.smartMeter != null) {
                requireActivity().runOnUiThread {
                    Glide.with(requireContext())
                        .load(R.drawable.ic_connection_sucess)
                        .into(statusCreateSmartMeterCloud)
                }
            } else {
                requireActivity().runOnUiThread {
                    Glide.with(requireContext())
                        .load(R.drawable.ic_connection_not_sucess)
                        .into(statusCreateSmartMeterCloud)
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val pairingLocalApi = local.org.openapitools.client.apis.PairingApi("http://${LocalDeviceConfig.getLocal().ipAddress}:5001")

            val cloud = CloudConnectModel(refreshToken, LocalDeviceConfig.getSmartMeterModel())

            try {
                val resultCloudConnect = pairingLocalApi.pairingCloudConnectPost(cloud)
                Log.d(TAG, "result: $resultCloudConnect")

                requireActivity().runOnUiThread {
                    if (resultCloudConnect.status == getString(R.string.OK)) {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_connection_sucess)
                            .into(statusCloudConnection)

                        LocalDeviceConfig.setFullyConfigured(true)
                    }
                }
            }
            catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())

                requireActivity().runOnUiThread {
                    Glide.with(requireContext())
                        .load(R.drawable.ic_connection_not_sucess)
                        .into(statusCloudConnection)
                }
                
                LocalDeviceConfig.setFullyConfigured(false)
            }

            requireActivity().runOnUiThread {
                progressBar.visibility = View.GONE
                btnOK.visibility = View.VISIBLE
            }
        }
    }

    /**
     * user can edit network / device settings
     */
    override fun onTouch(view: View?, p1: MotionEvent?): Boolean {
        when(view?.id) {
            mBinding.fragmentLocalSummaryNetworkEdit.id -> {
                mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_ADD_NETWORK, null)
            }

            mBinding.fragmentLocalSummaryDeviceSettingsEdit.id -> {
                mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_DEVICE_SETTINGS, null)
            }
        }

        view?.performClick()
        return true
    }
}