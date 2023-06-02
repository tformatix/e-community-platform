package at.fhooe.smartmeter.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.adapters.ProfileAdapter
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.databinding.FragmentProfileBinding
import at.fhooe.smartmeter.models.Settings
import at.fhooe.smartmeter.models.SmartMeter
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.Api
import at.fhooe.smartmeter.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.MemberApi
import org.openapitools.client.apis.SmartMeterApi

class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null

    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mEncryptedPreferences: EncryptedPreferences

    private val mBinding get() = _binding!!
    private var mContext: Context? = null

    private var mSettings: MutableList<Settings> = emptyList<Settings>().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        mEncryptedPreferences = EncryptedPreferences(requireContext())

        mBinding.fragmentProfileAddSmartMeter.setOnClickListener(this)
        mBinding.fragmentProfileIcvLogout.setOnClickListener(this)

        mContext?.let { context ->
            Api.authorizedBackendCall(context, null) {
                val memberApi = MemberApi(Constants.HTTP_BASE_URL_CLOUD)
                val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                val member = memberApi.memberGetMinimalMemberGet()
                val smartMeters = smartMeterApi.smartMeterGetMinimalSmartMetersGet()

                mSettings.add(Settings(smartMeters))
                CoroutineScope(Dispatchers.Main).launch {
                    mBinding.fragmentProfileUsername.setText(member.userName)

                    val profileAdapter = ProfileAdapter()
                    profileAdapter.setSettings(mSettings)

                    with(mBinding.fragmentProfileRecyclerviewSettings) {
                        adapter = profileAdapter
                        setHasFixedSize(true)
                    }
                }
            }
        }

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
     * */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mIFragmentMessageService = context as IFragmentMessageService
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            mBinding.fragmentProfileAddSmartMeter.id -> {
                mIFragmentMessageService.onCommunicate(Constants.SHOW_LOCAL_DISCOVERY, null)
            }
            mBinding.fragmentProfileIcvLogout.id -> {
                logout()
            }
        }
    }

    private fun logout() {
        mEncryptedPreferences.sharedPreferences?.edit()?.apply {
            putString(
                mContext?.getString(R.string.shared_prefs_refresh_token),
                null
            )
        }?.apply()

        mIFragmentMessageService.onCommunicate(Constants.SHOW_STARTUP_ACTIVITY, null)
    }
}