package at.fhooe.smartmeter.ui.profile.local

import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.FragmentLocalDeviceSettingsBinding
import at.fhooe.smartmeter.models.LocalDeviceConfig

class LocalDeviceSettingsFragment : Fragment(), CompoundButton.OnCheckedChangeListener{

    private var _binding: FragmentLocalDeviceSettingsBinding? = null
    private val mBinding get()= _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalDeviceSettingsBinding.inflate(layoutInflater, container, false)

        val smartMeterModel = LocalDeviceConfig.getSmartMeterModel()

        with(mBinding) {
            fragmentLocalDeviceSettingsIsDirectFeedIn.setOnCheckedChangeListener(this@LocalDeviceSettingsFragment)
            fragmentLocalDeviceSettingsIsMain.setOnCheckedChangeListener(this@LocalDeviceSettingsFragment)
            fragmentLocalDeviceSettingsIsOverflowFeedIn.setOnCheckedChangeListener(this@LocalDeviceSettingsFragment)
            fragmentLocalDeviceSettingsMeasureConsumption.setOnCheckedChangeListener(this@LocalDeviceSettingsFragment)
            fragmentLocalDeviceSettingsMeasureFeedIn.setOnCheckedChangeListener(this@LocalDeviceSettingsFragment)

            fragmentLocalDeviceSettingsIsMain.isChecked = smartMeterModel.isMain!!
            fragmentLocalDeviceSettingsIsOverflowFeedIn.isChecked = smartMeterModel.isOverflowFeedIn!!
            fragmentLocalDeviceSettingsIsDirectFeedIn.isChecked = smartMeterModel.isDirectFeedIn!!
            fragmentLocalDeviceSettingsMeasureConsumption.isChecked = smartMeterModel.measuresConsumption!!
            fragmentLocalDeviceSettingsMeasureFeedIn.isChecked = smartMeterModel.measuresFeedIn!!
            fragmentLocalDeviceSettingsAesKey.setText(smartMeterModel.aesKey!!)
        }

        return mBinding.root
    }

    /**
     * this fragment has a menu (just check)
     * when config is finished (onCLick will be handled in MainActivity)
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_check, menu)
    }

    /**
     * update device settings in LocalDeviceConfig
     * @see CompoundButton
     */
    override fun onCheckedChanged(view: CompoundButton?, checked: Boolean) {

        val smartMeterModel = LocalDeviceConfig.getSmartMeterModel()

        when(view?.id) {
            mBinding.fragmentLocalDeviceSettingsIsMain.id -> {
                smartMeterModel.isMain = checked
            }

            mBinding.fragmentLocalDeviceSettingsIsDirectFeedIn.id -> {
                smartMeterModel.isDirectFeedIn = checked
            }

            mBinding.fragmentLocalDeviceSettingsIsOverflowFeedIn.id -> {
                smartMeterModel.isOverflowFeedIn = checked
            }

            mBinding.fragmentLocalDeviceSettingsMeasureConsumption.id -> {
                smartMeterModel.measuresConsumption = checked
            }

            mBinding.fragmentLocalDeviceSettingsMeasureFeedIn.id -> {
                smartMeterModel.measuresFeedIn = checked
            }
        }

        LocalDeviceConfig.setSmartMeterModel(smartMeterModel)
    }
}