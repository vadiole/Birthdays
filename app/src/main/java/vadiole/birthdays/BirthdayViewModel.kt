package vadiole.birthdays

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class BirthdayViewModel(application: Application) : AndroidViewModel(application) {

    private val APP_PREFERENCES = "mysettings"

    private val repository: BirthdayRepository
    var allBirthdaysAndHeaders: LiveData<List<MainListDataItem>>

    init {
        val birthdayDao =
            BirthdayRoomDatabase.getDatabase(application, viewModelScope).birthdayDao()
        repository = BirthdayRepository(birthdayDao)

        val allBirthdays = repository.allBirthdays

        allBirthdaysAndHeaders =
            Transformations.map(allBirthdays) { list -> getBirthdayItemsList(list) }


    }

    fun insert(birthday: Birthday) = viewModelScope.launch {
        repository.insert(birthday)
    }

    fun update(birthday: Birthday) = viewModelScope.launch {
        repository.update(birthday)
    }

    fun delete(birthday: Birthday) = viewModelScope.launch {
        repository.delete(birthday)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()

    }

    private fun getBirthdayItemsList(list: List<Birthday>): List<MainListDataItem> {
        if (list.isNotEmpty()) {
            list.forEach {
                it.yearsOld = Birthday.getYearsOld(it)
            }

            val newList: MutableList<MainListDataItem> =
                list.map { bd -> MainListDataItem.BirthdayItem(bd) }
                    .toMutableList()
            val newListHeaders: MutableList<MainListDataItem> = mutableListOf()

            val positionNow = getNowBirthdayPosition(list)
            Collections.rotate(newList, -positionNow)

            return newList
        }
        return emptyList()


/*

        if (list.isNotEmpty()) {


            val newList: MutableList<MainListDataItem> =
                list.map { birthday: Birthday -> MainListDataItem.BirthdayItem(birthday) }
                    .toMutableList()

            val newListWithHeaders: MutableList<MainListDataItem> = mutableListOf()
            newListWithHeaders.addAll(newList)

            var counterPastBirthdays = 0

            newList.forEach {
                if (it is MainListDataItem.BirthdayItem) {
                    it.birthday.yearsOld = getYearsOld(it.birthday.birthdayDate, LocalDate.now())
                    if (isBirthdayPast(it.birthday)) {
                        counterPastBirthdays++
                    }
                    if (!newListWithHeaders.contains(MainListDataItem.HeaderItem(MonthHeader(it.birthday.birthdayDate.month.toString())))) {
                        newListWithHeaders.add(
                            newListWithHeaders.indexOf(it),
                            MainListDataItem.HeaderItem(MonthHeader(it.birthday.birthdayDate.month.toString()))
                        )
                    }
                }
            }
            val positionToRotate = newListWithHeaders.indexOf(newList[counterPastBirthdays])
            Collections.rotate(newListWithHeaders, positionToRotate)


//        if (newList.size > 0) {
//            newList.add(
//                newList.size - counterPastBirthdays,
//                MainListDataItem.HeaderItem(MonthHeader("My First Header"))
//            )
//        }
            return newListWithHeaders
        } else {
            return emptyList()
        }

 */
    }

    private fun getNowBirthdayPosition(list: List<Birthday>): Int {
        var position = 0
        list.forEach {
            if (isBirthdayPast(it)) {
                position++
            }
        }
        return position
    }

    private fun isBirthdayPast(birthday: Birthday): Boolean {
        return (birthday.birthdayDate.dayOfYear < LocalDate.now().dayOfYear)
    }

//    private fun getYearsOld(birthday: Birthday): Int {
//        val years = ChronoUnit.YEARS.between(birthday.birthdayDate, LocalDate.now()).toInt()
//        return if ((birthday.birthdayDate.dayOfMonth == LocalDate.now().dayOfMonth) and (birthday.birthdayDate.monthValue == LocalDate.now().monthValue)) years else years + 1
//    }

}
