package vadiole.birthdays

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BirthdayViewModel : ViewModel() {
    var birthdayList : MutableLiveData<List<Birthday>> = MutableLiveData()

    init {
        birthdayList.value = BirthdayData.getBirthdays()

    }

}
