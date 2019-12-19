package vadiole.birthdays

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

class MyDateValidator() : CalendarConstraints.DateValidator {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun isValid(date: Long): Boolean {
        return (date <= LocalDate.now(ZoneOffset.UTC).toEpochDay()*(1000 * 60 * 60 * 24))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyDateValidator> {
        override fun createFromParcel(parcel: Parcel): MyDateValidator {
            return MyDateValidator()
        }

        override fun newArray(size: Int): Array<MyDateValidator?> {
            return arrayOfNulls(size)
        }
    }

}
