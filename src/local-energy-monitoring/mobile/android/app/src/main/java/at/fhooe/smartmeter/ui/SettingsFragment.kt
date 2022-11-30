package at.fhooe.smartmeter.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.FragmentHistoryBinding
import at.fhooe.smartmeter.databinding.FragmentSettingsBinding
import com.github.mikephil.charting.data.BarEntry
import java.lang.StringBuilder
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val toolbar = binding.fragmentSettingsToolbar
        toolbar.inflateMenu(R.menu.menu_settings_toolbar)

        sharedPrefs = activity?.getSharedPreferences(getString(R.string.shared_prefs_settings), Context.MODE_PRIVATE)!!

        binding.fragmentSettingsIp.setText(sharedPrefs.getString(getString(R.string.shared_prefs_settings_ip), ""))
        binding.fragmentSettingsAesKey.setText(sharedPrefs.getString(getString(R.string.shared_prefs_settings_aeskey), ""))

        binding.let {
            binding.fragmentSettingsIp.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s?.matches(Regex("^(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\$"))!!) {
                        binding.fragmentSettingsIp.error = getString(R.string.fragment_settings_invalid_ip)
                    }
                    else {
                        binding.fragmentSettingsIp.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            binding.fragmentSettingsAesKey.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s?.matches(Regex("^([0-9A-F]{2} ){15}[0-9A-F]{2}\$"))!!) {
                        binding.fragmentSettingsAesKey.error = getString(R.string.fragment_settings_aes_key_invalid)
                    }
                    else {
                        binding.fragmentSettingsAesKey.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            toolbar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_settings_toolbar_save -> saveSettings()
                }
                true
            }
        }
        return binding.root
    }

    /**
     * format AES Key and save values in sharedPreferences
     * */
    private fun saveSettings() {
        if (binding.fragmentSettingsAesKey.text?.isEmpty()!!) {
            binding.fragmentSettingsAesKey.error = getString(R.string.fragment_settings_aes_empty)
            return
        }

        sharedPrefs = requireActivity().getSharedPreferences(getString(R.string.shared_prefs_settings), Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(getString(R.string.shared_prefs_settings_aeskey), binding.fragmentSettingsAesKey.text.toString()).apply()
        sharedPrefs.edit().putString(getString(R.string.shared_prefs_settings_ip), binding.fragmentSettingsIp.text.toString()).apply()

        Toast.makeText(requireActivity(), R.string.fragment_settings_saved, Toast.LENGTH_SHORT).show()
    }
}