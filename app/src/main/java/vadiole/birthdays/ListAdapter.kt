package vadiole.birthdays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListAdapter : RecyclerView.Adapter<BirthdayViewHolder>() {

    private var list: List<Birthday> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BirthdayViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun refreshBirthdays(birthdays: List<Birthday>) {
        this.list = birthdays
        notifyDataSetChanged()
    }
}
