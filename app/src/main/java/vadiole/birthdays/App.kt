package vadiole.birthdays

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.jakewharton.threetenabp.AndroidThreeTen


class App : Application() {


    val keyNightMode = "NIGHT_MODE"

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        currentNightMode = mPrefs.getInt(
            keyNightMode,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

//    fun getCurrentNightMode(): Int {
//        return currentNightMode
//    }
//
//    fun setCurrentNightMode(nightMode: Int) {
//        currentNightMode = nightMode
//    }


    companion object {
        var currentNightMode: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        lateinit var mPrefs: SharedPreferences

        fun saveNightMode(nightMode: Int) {
            mPrefs.edit { putInt("NIGHT_MODE", nightMode) }
        }
    }
}
