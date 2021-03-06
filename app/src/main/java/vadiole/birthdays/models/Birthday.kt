package vadiole.birthdays.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.lang.Exception

@Entity
data class Birthday(
    var name: String,
    var birthdayDate: LocalDate,
    var yearsOld: Int? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    var isArchived: Boolean = false

    var avatarPath: String? = null
    var userTag: String? = null

    var phoneNumber: String? = null
    var telegramLink: String? = null
    var instagramLink: String? = null
    var email: String? = null
    var notes: String? = null
    var gifts: String? = null

    companion object {
        fun getYearsOld(birthday: Birthday): Int {
            return getYearsOld(
                birthday.birthdayDate
            )
        }

        fun getYearsOld(dataOdBirthday: LocalDate): Int {
            val dateNow = LocalDate.now()
            val years = ChronoUnit.YEARS.between(dataOdBirthday, dateNow).toInt()
            return if ((dataOdBirthday.dayOfMonth == dateNow.dayOfMonth) and (dataOdBirthday.monthValue == dateNow.monthValue)) years else years + 1
        }

        fun isNull(birthday: Birthday?): Boolean {
            return try {
                birthday?.name.equals(getNull().name)
                birthday?.birthdayDate == getNull().birthdayDate
            } catch (e: Exception) {
                true
            }
        }

        fun getNull() : Birthday = Birthday(" ", LocalDate.MIN)
    }
}
