package com.sctdroid.autosigner.fragments.status

import android.text.TextUtils
import com.antiless.support.fragment.list.RecyclerViewContract
import com.sctdroid.autosigner.utils.WeiboApi
import com.sina.weibo.sdk.exception.WeiboException
import com.sina.weibo.sdk.net.RequestListener
import com.sina.weibo.sdk.openapi.models.Comment
import com.sina.weibo.sdk.openapi.models.CommentList
import com.sina.weibo.sdk.openapi.models.Status
import timber.log.Timber

class CommentPresenter(val view: RecyclerViewContract.View<Comment>, val status: Status) : RecyclerViewContract.Presenter {
    private var nextCursor: String = ""
    override fun start() {
        refreshComment()
    }
    fun refreshComment() {
        val commentsAPI = WeiboApi.commentsAPI()
        commentsAPI?.show(status.id.toLong(), 0, 0, 10, 1, 0, object : RequestListener {
            override fun onWeiboException(p0: WeiboException?) {
//                Toast.makeText(ContextUtils.applicationContext, "comment error ${p0?.localizedMessage}", Toast.LENGTH_LONG).show()
            }

            override fun onComplete(p0: String?) {
                Timber.d("comments $p0")
                val comments = CommentList.parse(p0)
                view.updateData(comments.commentList)
                nextCursor = comments.next_cursor
            }
        })
    }

    override fun refresh() {
        refreshComment()
    }

    override fun loadMore() {
        val commentsAPI = WeiboApi.commentsAPI()
        commentsAPI?.show(status.id.toLong(), 0, 0, 10, 1, 0, object : RequestListener {
            override fun onWeiboException(p0: WeiboException?) {
//                Toast.makeText(ContextUtils.applicationContext, "comment error ${p0?.localizedMessage}", Toast.LENGTH_LONG).show()
            }

            override fun onComplete(p0: String?) {
                Timber.d("comments $p0")
                val comments = CommentList.parse(p0)
                view.updateData(comments.commentList)
                nextCursor = comments.next_cursor
            }
        })
    }

    override fun hasMore(): Boolean {
        return nextCursor.isNotEmpty()
    }

    override fun create() {
    }
}