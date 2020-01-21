package vadiole.birthdays

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

import vadiole.birthdays.models.Birthday
import vadiole.birthdays.models.MainListDataItem
import vadiole.birthdays.models.Month
import vadiole.birthdays.repository.BirthdayRepository
import vadiole.birthdays.repository.BirthdayRoomDatabase
import vadiole.birthdays.utils.LogUtil
import java.util.*

class MyViewModel(application: Application) : AndroidViewModel(application) {

    var transactionLocked = false

    private val repository: BirthdayRepository
    var allBirthdaysAndHeaders: LiveData<List<MainListDataItem>>
    var selectedItem = MutableLiveData<Birthday>(
        Birthday.getNull()
    )

    private var privateRecDelItem = MutableLiveData<Birthday>(Birthday.getNull())

    var deletedItem: LiveData<Birthday> = privateRecDelItem

    private val MonthList: List<Month> =
        application.resources.getStringArray(R.array.months).map {
            Month(
                it
            )
        }

    init {
        val birthdayDao =
            BirthdayRoomDatabase.getDatabase(application, viewModelScope).birthdayDao()
        repository = BirthdayRepository(birthdayDao)

        val allBirthdays = repository.allBirthdays
        allBirthdaysAndHeaders =
            Transformations.map(allBirthdays) { list -> getMainMappedList(list) }


    }

    private fun getMainMappedList(list: List<Birthday>): List<MainListDataItem> {
        if (list.isNotEmpty()) {
            list.forEach {
                it.yearsOld = Birthday.getYearsOld(it)
            }

            val newList: MutableList<MainListDataItem> =
                list.map { bd -> MainListDataItem.BirthdayItem(bd) }
                    .toMutableList()

            val positionToRotate = getNextBirthdayPosition(list)
            Collections.rotate(newList, -positionToRotate)


            val newListHeaders: MutableList<MainListDataItem> = mutableListOf()

            val arrayHeadersFlags = BooleanArray(12) { false }


            var repeatedMonth = if (newList.first() is MainListDataItem.BirthdayItem) {
                (newList.first() as MainListDataItem.BirthdayItem).birthday.birthdayDate.monthValue
            } else -1


            for (item in newList) {
                if (item is MainListDataItem.BirthdayItem) {
                    val monthId = item.birthday.birthdayDate.monthValue


                    if (!arrayHeadersFlags[monthId - 1]) {
                        arrayHeadersFlags[monthId - 1] = true
                        newListHeaders.add(MainListDataItem.HeaderItem(MonthList[monthId - 1]))
                    } else if (monthId == repeatedMonth) {
                        if (isBirthdayWasIsThisMonth(item.birthday)) {
                            repeatedMonth = -1
                            newListHeaders.add(
                                MainListDataItem.HeaderItem(
                                    Month(
                                        (MonthList[monthId - 1].name) + " ${LocalDate.now().year + 1}"
                                    )
                                )
                            )
                        }
                    }
                    newListHeaders.add(item)

                }
            }

            return newListHeaders
        }
        return emptyList()
    }

    private fun isBirthdayWasIsThisMonth(birthday: Birthday): Boolean {
        if (LocalDate.now().monthValue == birthday.birthdayDate.monthValue) {
            if (LocalDate.now().dayOfMonth > birthday.birthdayDate.dayOfMonth) {
                return true
            }
        }
        return false
    }

    //position to rotate
    private fun getNextBirthdayPosition(list: List<Birthday>): Int {
        var position = 0
        list.forEach {
            if (isBirthdayPast(it)) {
                position++
            }
        }
        return position
    }

    private fun isBirthdayPast(birthday: Birthday): Boolean {
        return (LocalDate.now().dayOfYear > birthday.birthdayDate.dayOfYear)
    }

    fun setSelectedItem(birthday: Birthday) {
        selectedItem.value = birthday
    }

    fun setRecentlyDeletedItem(birthday: Birthday) {
        privateRecDelItem.value = birthday
    }

    //repository methods
    fun insert(birthday: Birthday) = viewModelScope.launch {
        repository.insert(birthday)
    }

    fun update(birthday: Birthday) = viewModelScope.launch {
        repository.update(birthday)
    }

    fun delete(birthday: Birthday) = viewModelScope.launch {
        repository.delete(birthday)
        privateRecDelItem.value = birthday

    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun undoDelete() {
        if ((privateRecDelItem.value != null) && (!Birthday.isNull(privateRecDelItem.value))) {
            insert(privateRecDelItem.value!!)
            privateRecDelItem.value = Birthday.getNull()
        } else {
            Log.e(LogUtil.UNDO_SNACKBAR, ": birthday == null")
        }
    }

}
