package com.example.wordnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections


open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder>() {

    protected val mItemList = ArrayList<T>()
    val itemList get() = mItemList
    protected var itemTouchHelper: ItemTouchHelper? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    open fun setItemList(arrayList: ArrayList<T>) {
        mItemList.clear()
        mItemList.addAll(arrayList)
        notifyDataSetChanged()
    }

    open fun setItemList(items: List<T>) {
        mItemList.clear()
        mItemList.addAll(items)
        notifyDataSetChanged()
    }

    open fun addItem(item: T) {
        mItemList.add(item)
        notifyItemInserted(mItemList.size - 1)
    }

    open fun addItemNoNotify(item: T) {
        mItemList.add(item)
    }

    fun clear() {
        mItemList.clear()
        notifyDataSetChanged()
    }

    fun removeItem(item: T) {
        val index = mItemList.indexOf(item)
        mItemList.remove(item)
        notifyItemRemoved(index)
    }

    override fun getItemViewType(position: Int): Int = doGetViewType(position)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = mItemList[position]
        val view = holder.itemView
        doBindViewHolder(view, item, position, holder)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = mItemList[position]
        val view = holder.itemView
        if (payloads.isNotEmpty()) {
            doBindViewHolder(view, item, position, holder, payloads)
        } else {
            doBindViewHolder(view, item, position, holder)
        }
    }

    private fun registerItemTouch(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    interface ItemTouchListener {
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }

    abstract fun doGetViewType(position: Int): Int
    abstract fun doBindViewHolder(view: View, item: T, position: Int, holder: BaseViewHolder)
    open fun doBindViewHolder(
        view: View,
        item: T,
        position: Int,
        holder: BaseViewHolder,
        payloads: MutableList<Any>
    ) {
        doBindViewHolder(view, item, position, holder)
    }

    private fun moveItem(fromPosition: Int, toPosition: Int) {

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mItemList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mItemList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun addMoveCallback(
        recyclerView: RecyclerView,
        onMove: ((fromPosition: Int, toPosition: Int) -> Unit)
    ) {
        val callback = ItemTouchHelperCallback(object : ItemTouchListener {
            override fun onItemMove(fromPosition: Int, toPosition: Int) {
                moveItem(fromPosition, toPosition)
                onMove(fromPosition, toPosition)
            }
        })
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        registerItemTouch(itemTouchHelper)
    }

    class ItemTouchHelperCallback(private var itemTouchListener: ItemTouchListener) :
        ItemTouchHelper.Callback() {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
            )
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            itemTouchListener.onItemMove(
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            return true
        }
    }

    fun shuffle() {
        mItemList.shuffle()
        notifyDataSetChanged()
    }

}