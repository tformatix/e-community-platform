package at.fhooe.smartmeter.ui.startup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.databinding.FragmentRegisterConfirmationBinding
import at.fhooe.smartmeter.databinding.LoadingScreenBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.util.EncryptedPreferences

class LoadingScreen: Fragment() {
    private var _binding: LoadingScreenBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mContext: Context
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mIFragmentMessageService: IFragmentMessageService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoadingScreenBinding.inflate(inflater, container, false)
        mContext = requireContext()
        mEncryptedPreferences = EncryptedPreferences(requireContext())

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
}