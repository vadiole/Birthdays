package vadiole.birthdays.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import vadiole.birthdays.App
import vadiole.birthdays.MyViewModel
import vadiole.birthdays.R
import vadiole.birthdays.utils.Event


class MainActivity : AppCompatActivity(),
    MainListFragment.OnFragmentInteractionListener,
    BirthdayFragment.OnFragmentInteractionListener,
    EditBirthdayFragment.OnFragmentInteractionListener,
    HelpFragment.OnFragmentInteractionListener {

    private lateinit var navController: NavController
    private val intentHome by lazy {
        Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_HOME)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private val birthdayViewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
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
        navController.setGraph(R.navigation.nav_graph)
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
            Event.OpenBirthdayFragment -> {
                if (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment is MainListFragment)
                    navController.navigate(R.id.action_mainListFragment_to_birthdayFragment)
                else Log.e(
                    supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment.toString(),
                    "Navigate main_list -> birthday, but main_list isn't current fragment"
                )
            }
            Event.OpenEditBirthdayFragment -> {
                if (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment is BirthdayFragment)
                    navController.navigate(R.id.action_birthdayFragment_to_edit_birthday_fragment)
                else Log.e(
                    supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment.toString(),
                    "Navigate birthday -> edit_birthday, but birthday isn't current fragment"
                )
            }
            Event.DelBirthdayPressed -> {
                onBackPressed()

            }
            Event.BackPressed -> {
                onBackPressed()
            }
            Event.OpenHelpFragment -> {
                if (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment is MainListFragment)
                    navController.navigate(R.id.action_mainListFragment_to_helpFragment)
                else Log.e(
                    supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment.toString(),
                    "Navigate MainFragment -> HelpFragment, but MainFragment isn't current fragment"
                )
            }
        }
    }


}
