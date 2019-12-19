package vadiole.birthdays

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1

    private var list: MutableList<MainListDataItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> BirthdayViewHolder.from(parent)
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            else -> BirthdayViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val headerItem = getItem(position) as MainListDataItem.HeaderItem
                holder.bind(headerItem.monthHeader)
            }
            is BirthdayViewHolder -> {
                val birthdayItem = getItem(position) as MainListDataItem.BirthdayItem
                holder.bind(birthdayItem.birthday)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun refreshBirthdays(newList: List<MainListDataItem>) {
        Log.d("LIST SIZE ", newList.size.toString())

        if (newList.size > this.list.size) {
            val diffList: List<MainListDataItem> = getDiff(newList, this.list)
            if (diffList.size == 1) {
                val position: Int = newList.indexOf(diffList.single())
                this.list.add(position, diffList.single())

                notifyItemInserted(position)
            } else {
//            diffList.forEach { item: MainListDataItem ->
//                this.list.add(
//                    newList.indexOf(item),
//                    item
//                )
//                notifyItemInserted(newList.indexOf(item))
//            }
                this.list = newList.toMutableList()
                notifyDataSetChanged()
            }
        } else {
            this.list = newList.toMutableList()
            notifyDataSetChanged()
        }

    }

    private fun getDiff(
        list1: List<MainListDataItem>,
        list2: List<MainListDataItem>
    ): List<MainListDataItem> {
        val sum = list1 + list2
        return sum.groupBy { it.id }
            .filter { it.value.size == 1 }
            .flatMap { it.value }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MainListDataItem.BirthdayItem -> ITEM_VIEW_TYPE_ITEM
            is MainListDataItem.HeaderItem -> ITEM_VIEW_TYPE_HEADER
        }
    }

    private fun getItem(position: Int): MainListDataItem {
        return list[position]
    }
}
