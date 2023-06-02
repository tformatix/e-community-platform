package at.fhooe.smartmeter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.fhooe.smartmeter.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** setUp bottomNavigation **/
        val navView: BottomNavigationView = binding.activityMainNavView
        val navController = findNavController(R.id.activity_main_nav_host_fragment)

        // ActionBar needed?
        //val appBarConfiguration = AppBarConfiguration(setOf(
        //    R.id.navigation_history, R.id.navigation_realtime, R.id.navigation_settings))
        //setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }
}