package vadiole.birthdays

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import org.threeten.bp.LocalDate

class BirthdayRepository(private val birthdayDao: BirthdayDao) {

    val allBirthdays: LiveData<List<Birthday>> = birthdayDao.getAll()

    suspend fun insert(birthday: Birthday) {
        birthdayDao.insert(birthday)
    }

    suspend fun update(birthday: Birthday) {
        birthdayDao.update(birthday)
    }

    suspend fun delete(birthday: Birthday) {
        birthdayDao.delete(birthday)
    }

    suspend fun deleteAll() {
        birthdayDao.deleteAll()
    }

    var list = listOf(
        Birthday("Dmitriy @vadiole", LocalDate.of(2000, 11, 8)),
        Birthday("Sasha", LocalDate.of(2001, 1, 18)),
        Birthday("Alexandr", LocalDate.of(2000, 11, 21)),
        Birthday("Nikolay Levtik", LocalDate.of(1960, 3, 23)),
        Birthday("Platon Rimskiy", LocalDate.of(1988, 4, 8)),
        Birthday("Leonardo Davinchi", LocalDate.of(1500, 12, 12)),
        Birthday("Nikola Tesla", LocalDate.of(1913, 9, 13)),
        Birthday("User User", LocalDate.of(1980, 5, 15)),
        Birthday("Name Soname", LocalDate.of(1990, 7, 8)),
        Birthday("Test Test", LocalDate.of(1950, 3, 3)),
        Birthday("Sherlock Holmes", LocalDate.of(1999, 2, 18)),
        Birthday("Hello World", LocalDate.of(2008, 10, 19)),
        Birthday("Birthday", LocalDate.of(2005, 7, 5)),
        Birthday("Last Element", LocalDate.of(2006, 6, 17))
    )

    fun getBirthdays() = list

}

