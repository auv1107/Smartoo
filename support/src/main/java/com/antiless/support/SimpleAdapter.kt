package com.antiless.support

import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.HashMap

abstract class SimpleAdapter<D> : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
    private val data = ArrayList<D>()
    private var onClickListener: OnItemClickListener? = null
    private var onLongClickListener: OnItemLongClickListener? = null

    override fun getItemCount(): Int {
        return data.size
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(getItemView(i, viewGroup), this)
    }

    open fun getItemView(position: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(getLayoutId(position), parent, false)
    }

    abstract fun getLayoutId(position: Int): Int
    abstract fun onBindData(d: D, vh: ViewHolder)
    protected abstract fun onViewHolderCreated(vh: ViewHolder)

    fun getData(): List<D> {
        return data
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onClickListener = listener
    }

    fun setOnLongClickListener(listener: OnItemLongClickListener) {
        onLongClickListener = listener
    }

    override fun onBindViewHolder(@NonNull vh: ViewHolder, i: Int) {
        vh.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onItemClicked(vh)
            }
        }
        vh.itemView.setOnLongClickListener {
            if (onLongClickListener != null) {
                return@setOnLongClickListener onLongClickListener!!.onItemLongClicked(vh)
            }
            false
        }
        onBindData(data[i], vh)
    }

    fun getItem(position: Int): D {
        return data[position]
    }

    fun append(data: List<D>) {
        val count = this.data.size
        val newCount = data.size
        this.data.addAll(data)
        onItemRangeInserted(count, newCount)
    }

    protected fun onItemRangeInserted(count: Int, newCount: Int) {
        notifyItemRangeInserted(count, newCount)
    }

    fun update(data: List<D>?) {
        val diffCallback = DiffCallback(this.data, data)
        val result = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        if (data != null) {
            this.data.addAll(data)
        }
        dispatchUpdates(result)
    }

    protected fun dispatchUpdates(result: DiffUtil.DiffResult) {
        result.dispatchUpdatesTo(this)
    }

    private inner class DiffCallback constructor(private val oldData: List<D>?, private val newData: List<D>?) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newData?.size ?: 0
        }

        override fun areItemsTheSame(i: Int, i1: Int): Boolean {
            val oldItem = oldData!![i]
            val newItem = newData!![i1]
            return this@SimpleAdapter.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(i: Int, i1: Int): Boolean {
            val oldItem = oldData!![i]
            val newItem = newData!![i1]

            return this@SimpleAdapter.areContentsTheSame(oldItem, newItem)
        }
    }

    protected fun areItemsTheSame(oldItem: D, newItem: D): Boolean {
        return false
    }

    protected fun areContentsTheSame(oldItem: D, newItem: D): Boolean {
        return false
    }

    class ViewHolder(@NonNull itemView: View, simpleAdapter: SimpleAdapter<*>) : RecyclerView.ViewHolder(itemView) {
        private val adapterWeakReference: WeakReference<SimpleAdapter<*>> = WeakReference(simpleAdapter)
        var viewMap: MutableMap<String, View> = HashMap()

        init {
            onViewHolderCreated()
        }

        fun onViewHolderCreated() {
            adapterWeakReference.get()?.onViewHolderCreated(this)
        }

        fun hold(key: String, @IdRes viewId: Int) {
            viewMap[key] = itemView.findViewById(viewId)
        }

        operator fun <T : View> get(key: String): T {
            return viewMap[key] as T
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(vh: ViewHolder)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(vh: ViewHolder): Boolean
    }
}
