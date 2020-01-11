package vadiole.birthdays.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vadiole.birthdays.R
import vadiole.birthdays.models.Month

class MonthViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    private var mMonth: TextView? = null

    init {
        mMonth = itemView.findViewById(R.id.header_item)
    }

    companion object {
        fun from(parent: ViewGroup): MonthViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.recycler_header, parent, false)
            return MonthViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(month: Month) {
        mMonth?.text = month.name
    }
}
