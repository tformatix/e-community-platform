package at.fhooe.smartmeter.ui.startup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.SmartMeterApplication
import at.fhooe.smartmeter.database.*
import at.fhooe.smartmeter.databinding.FragmentStartUpBinding
import at.fhooe.smartmeter.models.Member
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.services.AuthService
import at.fhooe.smartmeter.util.TutorialManager
import at.fhooe.smartmeter.util.EncryptedPreferences
import at.fhooe.smartmeter.util.TextFormatter
import java.util.*


class StartUpFragment : Fragment(), View.OnClickListener, Observer, ViewPager.OnPageChangeListener {
    private var mTag = "LoginRegisterFragment"
    private lateinit var mBinding: FragmentStartUpBinding
    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mAuthService: AuthService
    private lateinit var mMember: Member
    private lateinit var mMemberDao: MemberDao
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mTutorialManager: TutorialManager
    private lateinit var mTextFormatter: TextFormatter

    // welcome slider (images & texts)
    private var welcomeImages = arrayOf(R.drawable.happy_earth, R.drawable.save_money, R.drawable.social)
    private var welcomeTexts = arrayOf(R.string.welcome_earth, R.string.welcome_save_money, R.string.welcome_social)

    private val mMemberViewModel: MemberViewModel by viewModels {
        UserViewModelFactory((requireActivity().application as SmartMeterApplication).memberRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStartUpBinding.inflate(inflater, container, false)
       // mContext = requireContext()
        mAuthService = AuthService(requireContext())
        mEncryptedPreferences = EncryptedPreferences(requireContext())
        mTutorialManager = TutorialManager(requireContext())
        mTextFormatter = TextFormatter(requireContext())

        val viewPagerAdapter = WelcomeViewPagerAdapter(requireContext(), welcomeImages)

        with(mBinding) {
            fragmentLoginRegisterLogin.setOnClickListener(this@StartUpFragment)
            fragmentLoginRegisterRegister.setOnClickListener(this@StartUpFragment)
            fragmentLoginRegisterTutorial.setOnClickListener(this@StartUpFragment)

            fragmentLoginRegisterViewpager.apply {
                adapter = viewPagerAdapter
                addOnPageChangeListener(this@StartUpFragment)
            }

            fragmentLoginRegisterImageText.text = mTextFormatter.convertHtmlToString(welcomeTexts[0])
        }

        return mBinding.root
    }

    /**
     * onClick() for Login or Registration
     */
    override fun onClick(v: View?) {
        when(v?.id) {

            // validate fields and send Request to backend
            mBinding.fragmentLoginRegisterLogin.id -> {

                mIFragmentMessageService.onCommunicate(Constants.SHOW_LOGIN, null)
            }

            // open register screen
            mBinding.fragmentLoginRegisterRegister.id -> {

                mIFragmentMessageService.onCommunicate(Constants.SHOW_REGISTER, null)
            }

            // start main activity with tutorial mode
            mBinding.fragmentLoginRegisterTutorial.id -> {

                // setup tutorial
                mTutorialManager.setup()

                mIFragmentMessageService.onCommunicate(Constants.SHOW_MAIN_ACTIVITY, null)
            }
        }
    }

    /**
    * for communication between fragment/activity
    * */
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

    /**
     * observer for AuthService after login attempt
     */
    @Deprecated("do not use volley")
    override fun update(o: Observable?, arg: Any?) {
        /*if (arg is VolleyError) {
            Log.e("wos", "ha")
        }
        else if (arg is JSONObject) {
            try {
                mMember.memberId = arg.getString(getString(R.string.json_householdId))
                mMember.accessToken = arg.getString(getString(R.string.json_access_token))
                mMember.refreshToken = arg.getString(getString(R.string.json_refresh_token))

                mMemberViewModel.insert(member = mMember)

                mIFragmentMessageService.onCommunicate(Constants.SHOW_MAIN_ACTIVITY, null)
            }
            catch (exc: JSONException) {
                Log.e(TAG, "$mTag::update() invalid json, cannot process")
            }
        }*/
    }

    /**
     * set according text of selected viewPager image
     */
    override fun onPageSelected(position: Int) {
        mBinding.fragmentLoginRegisterImageText.text = mTextFormatter.convertHtmlToString(welcomeTexts[position])
    }

    override fun onPageScrollStateChanged(state: Int) { }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

}