package vadiole.birthdays

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HeaderViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    private var mMonth: TextView? = null

    init {
        mMonth = itemView.findViewById(R.id.header_item)
    }

    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.month_header, parent, false)
            return HeaderViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(month: MonthHeader) {
        mMonth?.text = month.name
    }
}
