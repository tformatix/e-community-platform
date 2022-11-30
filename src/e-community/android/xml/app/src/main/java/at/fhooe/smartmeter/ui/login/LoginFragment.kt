package at.fhooe.smartmeter.ui.login

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.FragmentLoginBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.LoginMemberModel


class LoginFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentLoginBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        mContext = requireContext()

        mEncryptedPreferences = EncryptedPreferences(requireContext())
        mBinding.fragmentLoginButtonLogin.setOnClickListener(this)
        mBinding.fragmentLoginButtonForgotPassword.setOnClickListener(this)

        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentMessageService = context as IFragmentMessageService
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        mBinding.fragmentLoginTextviewInfoBelowUsername.setText("")
        mBinding.fragmentLoginTextviewInfoBelowPassword.setText("")

        when (view?.id) {
            mBinding.fragmentLoginButtonLogin.id -> {
                val email = mBinding.fragmentLoginTextfieldUsernameEmail.text.toString()
                val password = mBinding.fragmentLoginTextfieldPassword.text.toString()

                if (email == "") {
                    mBinding.fragmentLoginTextviewInfoBelowUsername.setText("This Field cannot be empty")
                    if (password == "") {
                        mBinding.fragmentLoginTextviewInfoBelowPassword.setText("This Field cannot be empty")
                    }
                    return
                }
                if (password == "") {
                    mBinding.fragmentLoginTextviewInfoBelowPassword.setText("This Field cannot be empty")
                    return
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mBinding.fragmentLoginTextviewInfoBelowUsername.setText("Email not valid")
                    if (password == "") {
                        mBinding.fragmentLoginTextviewInfoBelowPassword.setText("This Field cannot be empty")
                    }
                    return
                }

                var memberId = "";
                var accessToken = "";
                var refreshToken = "";

                var answer = null

                val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)

                // start networkresquest on new thread and store answer in sharedPreferences
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var answer = authApi.authLoginPost(LoginMemberModel(email, password))
                        memberId = answer.memberId.toString()
                        accessToken = answer.accessToken.toString()
                        refreshToken = answer.refreshToken.toString()

                        val editor = mEncryptedPreferences.sharedPreferences?.edit()
                        editor?.apply {
                            putString(mContext.getString(R.string.shared_prefs_member_id), memberId)
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
                        Log.e("MIMIS DEBUGTAG ERROR", e.toString())
                        requireActivity().runOnUiThread {
                            mBinding.fragmentRegisterconfirmedProgressbar.visibility = View.GONE
                            mBinding.fragmentLoginTextfieldPassword.setText("")
                            mBinding.fragmentLoginTextfieldUsernameEmail.setText("")
                            mBinding.fragmentLoginTextviewInfoBelowUsername.setText("Email or Password incorrect")
                            mBinding.fragmentLoginTextviewInfoBelowPassword.setText("Email or Password incorrect")
                            return@runOnUiThread
                        }
                    }
                }
                mBinding.fragmentRegisterconfirmedProgressbar.visibility = View.VISIBLE
            }

            mBinding.fragmentLoginButtonForgotPassword.id -> {
                mIFragmentMessageService.onCommunicate(Constants.SHOW_PASSWORD_FORGOTTEN, null)
            }
        }
    }
}
