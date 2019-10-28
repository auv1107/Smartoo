package com.sctdroid.autosigner.views.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.sctdroid.autosigner.views.StatusItem
import com.sctdroid.autosigner.views.StatusItem.OnImageClickListener
import com.sctdroid.autosigner.views.StatusItem_
import com.sina.weibo.sdk.openapi.models.Status
import com.sina.weibo.sdk.openapi.models.StatusList
import java.util.*

/**
 * Created by lixindong on 1/26/16.
 */
class StatusAdapter(val context: Context) : BaseAdapter() {
    private var mList: StatusList? = null
    override fun getCount(): Int {
        return if (mList == null) 0 else mList!!.statusList.size
    }

    override fun getItem(i: Int): Status? {
        return if (mList == null || mList!!.statusList.size <= i) null else mList!!.statusList[i]
    }

    var mListener: OnImageClickListener? = null
    var mOnAvatarClickListener: StatusItem.OnAvatarClickListener? = null
    fun setOnImageClickListener(listener: OnImageClickListener?) {
        mListener = listener
    }
    fun setOnAvatarClickListener(listener: StatusItem.OnAvatarClickListener?) {
        mOnAvatarClickListener = listener
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val item: StatusItem = if (view == null) {
            StatusItem_.build(context)
        } else {
            view as StatusItem_
        }
        item.bind(getItem(i))
        item.setOnImageClickListener(mListener)
        item.setOnAvatarClickListener(mOnAvatarClickListener)
        return item
    }

    fun update(list: StatusList?) {
        mList = list
        mList!!.statusList.sortWith(Comparator { o, t1 -> if (o.id.toLong() > t1.id.toLong()) 1 else 0 })
        notifyDataSetChanged()
    }

    fun append(list: StatusList) {
        mList!!.statusList.addAll(list.statusList)
        mList!!.next_cursor = list.next_cursor
        mList!!.statusList.sortWith(Comparator { o, t1 -> if (o.id.toLong() > t1.id.toLong()) 1 else 0 })
        notifyDataSetChanged()
    }
}