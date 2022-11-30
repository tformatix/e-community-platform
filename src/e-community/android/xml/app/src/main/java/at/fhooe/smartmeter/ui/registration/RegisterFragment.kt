package at.fhooe.smartmeter.ui.registration

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.FragmentRegisterBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.models.RegisterMemberModel
import java.util.*
import java.util.regex.Matcher

class RegisterFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRegisterBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mContext: Context
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mIFragmentMessageService: IFragmentMessageService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        mContext = requireContext()

        mEncryptedPreferences = EncryptedPreferences(requireContext())
        mBinding.fragmentRegisterRegister.setOnClickListener(this)

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
         mBinding.fragmentRegisterErrormessage.setText("")

         when (view?.id) {
             mBinding.fragmentRegisterRegister.id -> {
                 val username = mBinding.fragmentRegisterUsername.text.toString()
                 val password = mBinding.fragmentRegisterPassword.text.toString()
                 val repeatPassword = mBinding.fragmentRegisterRepeatpassword.text.toString()
                 val email = mBinding.fragmentRegisterEmail.text.toString()
                 val language = Locale.getDefault().getLanguage()

                 if (email == "" || password == "" || username == "" || repeatPassword == ""){
                     mBinding.fragmentRegisterErrormessage.setText(R.string.emptyField)
                     return
                 }

                 if(!password.equals(repeatPassword)){
                     mBinding.fragmentRegisterErrormessage.setText(R.string.unequalPasswords)
                     return
                 }

                 val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#\$^+=!*()@%&]).{6,}\$"
                 val passwordMatcher = Regex(passwordPattern)

                 if( passwordMatcher.find(password) == null) {
                     mBinding.fragmentRegisterPassword.setText("")
                     mBinding.fragmentRegisterRepeatpassword.setText("")
                     mBinding.fragmentRegisterErrormessage.setText(R.string.passwordInvalid)
                     mBinding.fragmentRegisterPasswordErrorInfo.setText(R.string.passwordInfo)
                     return
                 }

                 if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                     mBinding.fragmentRegisterErrormessage.setText(R.string.invalidEmail)
                     mBinding.fragmentRegisterUsername.setText("")
                     mBinding.fragmentRegisterPassword.setText("")
                     mBinding.fragmentRegisterEmail.setText("")
                     mBinding.fragmentRegisterRepeatpassword.setText("")
                     mBinding.fragmentRegisterPasswordErrorInfo.setText("")
                     return
                 }

                 val authApi = AuthApi("https://e-community.azurewebsites.net")
                 mBinding.fragmentRegisterconfirmedProgressbar.visibility = View.VISIBLE
                 CoroutineScope(Dispatchers.IO).launch {
                     try {
                         val answer = authApi.authRegisterPost(
                             RegisterMemberModel(
                                 email,
                                 username,
                                 password,
                                 language
                             )
                         )

                         val email = answer.email.toString()
                         val id = answer.id.toString()
                         val username = answer.userName.toString()

                         val editor = mEncryptedPreferences.sharedPreferences?.edit()
                         editor?.apply {
                             putString(mContext.getString(R.string.shared_prefs_member_id), id)
                             putString(mContext.getString(R.string.shared_prefs_email), email)
                             putString(mContext.getString(R.string.shared_prefs_username), username)
                         }?.apply()

                         mIFragmentMessageService.onCommunicate(Constants.SHOW_REGISTER_CONFIRMATION, null)
                     } catch (e: Exception) {
                         requireActivity().runOnUiThread {
                             Log.d("RegisterFragmentTag", e.toString())
                             mBinding.fragmentRegisterErrormessage.setText(R.string.invalidAttempt)
                             mBinding.fragmentRegisterUsername.setText("")
                             mBinding.fragmentRegisterPassword.setText("")
                             mBinding.fragmentRegisterEmail.setText("")
                             mBinding.fragmentRegisterRepeatpassword.setText("")
                             mBinding.fragmentRegisterPasswordErrorInfo.setText("")
                             mBinding.fragmentRegisterconfirmedProgressbar.visibility = View.GONE
                             return@runOnUiThread
                         }
                     }
                 }

             }
         }
    }
}