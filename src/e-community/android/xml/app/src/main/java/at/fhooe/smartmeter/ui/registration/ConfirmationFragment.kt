package at.fhooe.smartmeter.ui.registration

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.FragmentRegisterBinding
import at.fhooe.smartmeter.databinding.FragmentRegisterConfirmationBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.models.LoginMemberModel

class ConfirmationFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRegisterConfirmationBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mContext: Context
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mIFragmentMessageService: IFragmentMessageService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterConfirmationBinding.inflate(inflater, container, false)

        mContext = requireContext()

        mEncryptedPreferences = EncryptedPreferences(requireContext())
        mBinding.fragmentRegisterconfirmedButtonRegisterconfirmed.setOnClickListener(this)

        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentMessageService = context as IFragmentMessageService
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            mBinding.fragmentRegisterconfirmedButtonRegisterconfirmed.id -> {
                mBinding.fragmentRegisterconfirmedTextviewErrormessage.setText("")

                val password = mBinding.fragmentRegisterconfirmedTextfieldPassword.text.toString()
                if(password.equals("")){
                    mBinding.fragmentRegisterconfirmedTextviewErrormessage.setText(R.string.emptyField)
                    return
                }

                var memberId = ""
                var accessToken = ""
                var refreshToken = ""

                val email = mEncryptedPreferences.sharedPreferences?.getString(
                    mContext.getString(R.string.shared_prefs_email), null).toString()

                if (email == "") {
                    mBinding.fragmentRegisterconfirmedTextviewErrormessage.setText(R.string.error)
                    Log.d("ConfirmationFragmentTag", "ERROR - Email could not be saved in encrypted shared preferences")
                    return
                }
                mBinding.fragmentRegisterconfirmedProgressbar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val authApi = AuthApi("https://e-community.azurewebsites.net")
                        val loginAnswer = authApi.authLoginPost(LoginMemberModel(email, password))

                        memberId = loginAnswer.memberId.toString()
                        accessToken = loginAnswer.accessToken.toString()
                        refreshToken = loginAnswer.refreshToken.toString()

                        val editor = mEncryptedPreferences.sharedPreferences?.edit()
                        editor?.apply {
                            putString(
                                mContext.getString(R.string.shared_prefs_member_id),
                                memberId
                            )
                            putString(
                                mContext.getString(R.string.shared_prefs_access_token),
                                accessToken
                            )
                            putString(
                                mContext.getString(R.string.shared_prefs_refresh_token),
                                refreshToken
                            )
                        }?.apply()

                        mIFragmentMessageService.onCommunicate(Constants.SHOW_MAIN_ACTIVITY, null)

                    } catch (e: Exception) {
                        requireActivity().runOnUiThread {
                            Log.d("ConfirmationFragmentTag", e.toString())
                            val toast = Toast.makeText(
                                mContext,
                                R.string.error,
                                Toast.LENGTH_LONG
                            )
                            toast.show()
                            mBinding.fragmentRegisterconfirmedTextfieldPassword.setText("")
                            mBinding.fragmentRegisterconfirmedProgressbar.visibility = View.GONE
                            return@runOnUiThread
                        }
                    }
                }
            }
        }
    }
}