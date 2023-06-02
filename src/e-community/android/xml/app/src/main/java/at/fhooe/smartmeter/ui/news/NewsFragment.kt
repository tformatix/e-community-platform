package at.fhooe.smartmeter.ui.news

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.SmartMeterApplication
import at.fhooe.smartmeter.adapters.NotificationAdapter
import at.fhooe.smartmeter.database.NotificationViewModel
import at.fhooe.smartmeter.database.NotificationViewModelFactory
import at.fhooe.smartmeter.databinding.FragmentHomeBinding
import at.fhooe.smartmeter.databinding.FragmentNewsBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.TutorialManager
import at.fhooe.smartmeter.util.EncryptedPreferences
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null

    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mTutorialManager: TutorialManager

    private val mNotificationViewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory((requireActivity().application as SmartMeterApplication).notificationRepository)
    }

    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        // setup helpers
        mEncryptedPreferences = EncryptedPreferences(requireContext())
        mTutorialManager = TutorialManager(requireContext())

        val notificationAdapter = NotificationAdapter()
        with(mBinding.fragmentHomeRecyclerViewNotifications) {
            setHasFixedSize(true)
            adapter = notificationAdapter
        }

        // check if tutorial mode is active
        if (mEncryptedPreferences.sharedPreferences?.getBoolean(getString(R.string.shared_prefs_tutorial_mode), false) == true) {

            // do not reload notifications
            if (mEncryptedPreferences.sharedPreferences?.getBoolean(getString(R.string.shared_prefs_tutorial_loaded), false) == false) {
                mTutorialManager.prepareTutorialNotifications(mNotificationViewModel)
            }

            // check if notifications has been inserted in room with Flow<>
            lifecycle.coroutineScope.launch {
                mNotificationViewModel.getTutorialNotifications().collect {
                    notificationAdapter.setNotifications(it)
                }
            }
        }
        else {

            lifecycle.coroutineScope.launch {
                mNotificationViewModel.getNotifications().collect {
                    notificationAdapter.setNotifications(it)
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
        mIFragmentMessageService = context as IFragmentMessageService
    }
}