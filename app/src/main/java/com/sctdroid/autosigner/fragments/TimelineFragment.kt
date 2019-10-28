package com.sctdroid.autosigner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.sctdroid.autosigner.R
import com.sctdroid.autosigner.activities.GalleryActivity
import com.sctdroid.autosigner.fragments.status.StatusFragment
import com.sctdroid.autosigner.utils.AccessTokenKeeper
import com.sctdroid.autosigner.utils.Constants
import com.sctdroid.autosigner.views.StatusItem
import com.sctdroid.autosigner.views.adapter.StatusAdapter
import com.sctdroid.pullToRefresh.PullToRefreshBaseView.RefreshListener
import com.sctdroid.pullToRefresh.PullToRefreshListView
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.openapi.StatusesAPI
import com.sina.weibo.sdk.openapi.models.Status
import com.sina.weibo.sdk.openapi.models.StatusList
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.parceler.Parcels
import kotlin.concurrent.thread

/**
 * Created by lixindong on 1/20/16.
 */
open class TimelineFragment : BaseNavigationFragment() {
    lateinit var adapter: StatusAdapter
    lateinit var statusList: StatusList
    lateinit var mAccessToken: Oauth2AccessToken
    lateinit var mStatusesAPI: StatusesAPI
    val SINCE_ID = 0
    val MAX_ID = 0
    val REQUEST_COUNT = 10
    val FIRST_PAGE = 1
    private val currentPage = 1
    private val sinceId = ""
    private var maxId = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = StatusAdapter(context)
    }

    override fun onViewCreated(view: View) {
        mAccessToken = AccessTokenKeeper.readAccessToken(activity)
        if (mAccessToken.isSessionValid) {
            initListView()
            mStatusesAPI = StatusesAPI(activity, Constants.APP_KEY, mAccessToken)
            thread { fetchTimeline(REQUEST_COUNT, SINCE_ID.toLong()) }
        } else {
            Toast.makeText(activity, "Please login first.", Toast.LENGTH_LONG).show()
            activity!!.finish()
        }
    }

    private fun initListView() {
        adapter.setOnImageClickListener(StatusItem.OnImageClickListener { position, urls -> GalleryActivity.viewPictures(activity, position, urls) })
        adapter.setOnAvatarClickListener(StatusItem.OnAvatarClickListener { NavHostFragment.findNavController(this).navigate(R.id.action_timelineFragment_to_userFragment) })
        listView!!.setAdapter(adapter)
        listView!!.showLoadMore()
        listView!!.setRefreshListener(object : RefreshListener {
            override fun onPullDownToRefresh() {
                thread {
                    if (!isDetached) fetchTimeline(REQUEST_COUNT, MAX_ID.toLong())
                }
            }

            override fun onPullUpTpRefresh() {}
            override fun onPullUpToLoad() {
                thread {
                    if (!isDetached) fetchTimeline(REQUEST_COUNT, maxId.toLong())
                }
            }
        })
        listView!!.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (position > 0) {
                val status: Status? = adapter.getItem(position - 1)
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_timelineFragment_to_statusFragment,
                                Bundle().apply { putParcelable(StatusFragment.KEY_STATUS, Parcels.wrap(status)) })
            }
        }
    }

    fun fetchTimeline(count: Int, max_id: Long) {
        val statuses: String? = mStatusesAPI.friendsTimelineSync(SINCE_ID.toLong(), max_id, count, FIRST_PAGE, false, StatusesAPI.FEATURE_ORIGINAL, false)
        Log.d(TAG, statuses ?: "status null")
        statusList = StatusList.parse(statuses)
        maxId = statusList.next_cursor
        activity?.runOnUiThread {
            if (max_id == 0L) {
                adapter.update(statusList)
            } else {
                adapter.append(statusList)
            }
            hideRefreshView(max_id)
        }
    }

    fun hideRefreshView(max_id: Long) {
        if (max_id == MAX_ID.toLong()) {
            listView!!.finishRefreshing(PullToRefreshListView.PULL_DOWN)
        }
    }

    companion object {
        private val TAG = TimelineFragment::class.java.simpleName
    }
}