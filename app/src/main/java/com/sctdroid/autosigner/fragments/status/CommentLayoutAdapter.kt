package com.sctdroid.autosigner.fragments.status

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antiless.support.fragment.list.RecyclerViewLayoutAdapter
import com.bumptech.glide.Glide
import com.sctdroid.autosigner.R
import com.sctdroid.autosigner.utils.GlideCircleTransform
import com.sina.weibo.sdk.openapi.models.Comment
import java.sql.Date
import kotlinx.android.synthetic.main.layout_comment.view.*

class CommentLayoutAdapter : RecyclerViewLayoutAdapter<Comment>() {
    override fun getLayoutManager(context: Context?): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Comment?) {
        if (holder == null || item == null) return
        (holder as ViewHolder).bind(item)
    }

    class ViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_comment, parent, false)
    ) {
        fun bind(item: Comment) {
            Glide.with(itemView.context)
                    .load(item.user.avatar_large)
                    .transform(GlideCircleTransform(itemView.context))
                    .into(itemView.itemAvatar)
            itemView.itemName.text = item.user.name
            itemView.itemInfo.text = DateUtils.getRelativeTimeSpanString(Date.parse(item.created_at))
            itemView.itemContent.text = item.text
        }
    }
}