package vadiole.birthdays.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import vadiole.birthdays.*
import vadiole.birthdays.utils.Event


class MainActivity : AppCompatActivity(),
    MainListFragment.OnFragmentInteractionListener,
    BirthdayFragment.OnFragmentInteractionListener {

    private lateinit var navController: NavController
    private val intentHome by lazy {
        Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_HOME)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private val birthdayViewModel by lazy {
        ViewModelProviders.of(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        //set saved night mode
        if (App.currentNightMode != AppCompatDelegate.getDefaultNightMode()) setNightMode(
            App.currentNightMode
        )


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mainListFragment) {
            //emulate home button instead back
            startActivity(intentHome)
        } else {
            if (!birthdayViewModel.transactionLocked) super.onBackPressed()
        }
    }

    private fun setNightMode(currentNightMode: Int) {
        when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    override fun onFragmentInteraction(event: Event) {
        when (event) {
            Event.OpenBirthday -> {
                if (navController.currentDestination?.id == R.id.mainListFragment)
                    navController.navigate(R.id.action_mainListFragment_to_birthdayFragment)
                else Log.e(
                    localClassName,
                    "Navigate main_list -> birthday, but main_list isn't current fragment"
                )
            }
            Event.DelPressed -> {
                onBackPressed()

            }
            Event.BackPressed -> {
                onBackPressed()
            }
        }
    }
}
