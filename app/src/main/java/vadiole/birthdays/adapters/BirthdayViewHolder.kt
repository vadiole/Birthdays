package vadiole.birthdays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vadiole.birthdays.R
import vadiole.birthdays.models.Birthday

class BirthdayViewHolder(view: View, onItemClickListener: OnItemClickListener) :
    RecyclerView.ViewHolder(view), View.OnClickListener {
    private var mName: TextView? = null
    private var mData: TextView? = null
    private var mYearsOld: TextView? = null
    private val onItemClickListener: OnItemClickListener

    init {
        mName = itemView.findViewById(R.id.nameInItem)
        mData = itemView.findViewById(R.id.dataInItem)
        mYearsOld = itemView.findViewById(R.id.textYearsOldInItem)
        this.onItemClickListener = onItemClickListener
    }

    companion object {

        fun from(parent: ViewGroup, onItemClickListener: OnItemClickListener): BirthdayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.recycler_item, parent, false)
            return BirthdayViewHolder(
                view,
                onItemClickListener
            )
        }
    }

    fun bind(birthday: Birthday) {
        mName?.text = birthday.name
        mData?.text =
            birthday.birthdayDate.toString()
        mYearsOld?.text = birthday.yearsOld.toString()
        itemView.setOnClickListener {
            onItemClickListener.onItemClick(adapterPosition)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onClick(v: View?) {

    }
}
