package vadiole.birthdays

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BirthdayViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
RecyclerView.ViewHolder(inflater.inflate(R.layout.birthday_item, parent, false)) {
    private var mName : TextView? = null
    private var mData : TextView? = null

    init {
        mName = itemView.findViewById(R.id.nameInItem)
        mData = itemView.findViewById(R.id.dataInItem)
    }

    fun bind(birthdayData: Birthday) {
        mName?.text = birthdayData.name
        mData?.text = birthdayData.dateOfBirthday.toString()
    }
}