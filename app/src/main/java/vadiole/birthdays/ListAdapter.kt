package vadiole.birthdays

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1

    var itemList: List<MainListDataItem> by Delegates.observable(emptyList()) { _, oldList, newList ->
        notifyChanges(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            else -> {
                val birthdayVH = BirthdayViewHolder.from(parent)
                birthdayVH
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


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MainListDataItem.BirthdayItem -> ITEM_VIEW_TYPE_ITEM
            is MainListDataItem.HeaderItem -> ITEM_VIEW_TYPE_HEADER
        }
    }

    override fun getItemCount(): Int = itemList.size


    private fun notifyChanges(oldList: List<MainListDataItem>, newList: List<MainListDataItem>) {

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
                return oldList[oldPosition].id == newList[newPosition].id
            }

            override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
                return oldList[oldPosition] == newList[newPosition]
            }

            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size
        })

        diff.dispatchUpdatesTo(this)
    }

    private fun getItem(position: Int): MainListDataItem {
        return itemList[position]
    }

}
