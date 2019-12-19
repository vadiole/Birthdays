package vadiole.birthdays

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity(), MainListFragment.OnFragmentInteractionListener {

    private val mainListFragment by lazy { MainListFragment() }
    private val intentHome = Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_HOME)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        //      Set saved theme
        if (App.currentNightMode != AppCompatDelegate.getDefaultNightMode()) {
            when (App.currentNightMode) {
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

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction {
                add(R.id.root_container, mainListFragment)
            }
        }

    }

    //      Emulate home button instead back (it closes the app)
    override fun onBackPressed() {
        startActivity(intentHome)
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

}
