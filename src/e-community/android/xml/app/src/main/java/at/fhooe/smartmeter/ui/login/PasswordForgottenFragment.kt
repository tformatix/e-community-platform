package at.fhooe.smartmeter.ui.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.FragmentPasswordForgottenBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.services.AuthService
import java.util.*

class PasswordForgottenFragment : Fragment(), View.OnClickListener, Observer {
    private lateinit var mBinding: FragmentPasswordForgottenBinding
    private lateinit var mAuthService: AuthService
    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private var mTag = "PasswordForgottenFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPasswordForgottenBinding.inflate(layoutInflater)

        mAuthService = AuthService(requireContext())

        with(mBinding) {
            fragmentPasswordForgottenSendEmail.setOnClickListener(this@PasswordForgottenFragment)
        }

        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentMessageService = context as IFragmentMessageService
    }

    override fun onResume() {
        super.onResume()
        mAuthService.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAuthService.deleteObserver(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.fragmentPasswordForgottenSendEmail.id -> {

                // check if email is valid
                with(mBinding.fragmentPasswordForgottenEmail) {
                    if (length() == 0) {
                        error = getString(R.string.email_is_empty)
                        return
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                        error = getString(R.string.email_not_valid)
                        return
                    }
                }

                mAuthService.passwordForgotten(mBinding.fragmentPasswordForgottenEmail.toString())
            }
        }
    }

    /**
     * observer for AuthService after password forgotten
     */
    @Deprecated("do not use volley")
    override fun update(o: Observable?, arg: Any?) {
        /*if (arg is VolleyError) {
            val contextView = mBinding.fragmentPasswordForgottenSendEmail

            // email not registered.. user can register
            Snackbar.make(contextView, R.string.email_not_registered, Snackbar.LENGTH_SHORT)
                .setAction(R.string.button_register) {
                    mIFragmentMessageService.onCommunicate(Constants.SHOW_REGISTER, null)
                }
                .show()
        }
        else if (arg is JSONObject) {
            Toast.makeText(context, getString(R.string.email_sent), Toast.LENGTH_SHORT).show()
        }*/
    }
}