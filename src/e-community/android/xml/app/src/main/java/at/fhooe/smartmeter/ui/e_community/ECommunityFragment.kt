package at.fhooe.smartmeter.ui.e_community

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.fhooe.smartmeter.R

class ECommunityFragment : Fragment() {

    companion object {
        fun newInstance() = ECommunityFragment()
    }

    private lateinit var viewModel: ECommunityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_e_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ECommunityViewModel::class.java)
    }

}