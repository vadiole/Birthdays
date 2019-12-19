package vadiole.birthdays

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BirthdayViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    private var mName: TextView? = null
    private var mData: TextView? = null
    private var mYearsOld: TextView? = null

    init {
        mName = itemView.findViewById(R.id.nameInItem)
        mData = itemView.findViewById(R.id.dataInItem)
        mYearsOld = itemView.findViewById(R.id.textYearsOldInItem)
    }

    companion object {
        fun from(parent: ViewGroup): BirthdayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.birthday_item, parent, false)

            return BirthdayViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(birthday: Birthday) {
        mName?.text = birthday.name
        mData?.text =
            birthday.birthdayDate.toString()
        mYearsOld?.text = birthday.yearsOld.toString()
    }
}
