package com.sctdroid.autosigner.fragments.status

import com.antiless.support.fragment.list.RecyclerViewContract
import com.sctdroid.autosigner.utils.WeiboApi
import com.sina.weibo.sdk.exception.WeiboException
import com.sina.weibo.sdk.net.RequestListener
import com.sina.weibo.sdk.openapi.models.Comment
import com.sina.weibo.sdk.openapi.models.CommentList
import com.sina.weibo.sdk.openapi.models.Status
import timber.log.Timber

class CommentPresenter(val view: RecyclerViewContract.View<Comment>, val status: Status) : RecyclerViewContract.Presenter {
    override fun start() {
        refreshComment()
    }
    fun refreshComment() {
        val commentsAPI = WeiboApi.commentsAPI()
        commentsAPI?.show(status.id.toLong(), 0, 0, 50, 1, 0, object : RequestListener {
            override fun onWeiboException(p0: WeiboException?) {
//                Toast.makeText(ContextUtils.applicationContext, "comment error ${p0?.localizedMessage}", Toast.LENGTH_LONG).show()
            }

            override fun onComplete(p0: String?) {
                Timber.d("comments $p0")
                val comments = CommentList.parse(p0)
                view.updateData(comments.commentList)
            }
        })
    }

    override fun refresh() {
        refreshComment()
    }

    override fun loadMore() {
    }

    override fun hasMore(): Boolean {
        return false
    }

    override fun create() {
    }
}