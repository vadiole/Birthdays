package vadiole.birthdays

sealed class MainListDataItem {
    abstract val id: Long?

    data class BirthdayItem(val birthday: Birthday) : MainListDataItem() {
        override val id = birthday.id
    }

    data class HeaderItem(val monthHeader: MonthHeader) : MainListDataItem() {
        override val id = Long.MIN_VALUE
    }

}

