package com.sctdroid.autosigner.fragments.status

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import com.antiless.support.fragment.TabFragment
import com.antiless.support.fragment.list.RecyclerViewFragment
import com.sctdroid.autosigner.R
import com.sina.weibo.sdk.openapi.models.Comment
import com.sina.weibo.sdk.openapi.models.Status
import kotlinx.android.synthetic.main.fragment_status.*
import org.parceler.Parcels

class StatusFragment : TabFragment() {
    companion object {
        const val KEY_STATUS = "key_status"
        fun newInstance(status: Status): StatusFragment {
            val pStatus = Parcels.wrap(status)
            return StatusFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_STATUS, pStatus)
                }
            }
        }
    }

    private lateinit var status: Status
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pStatus = arguments?.getParcelable<Parcelable>(KEY_STATUS)
        status = Parcels.unwrap<Status>(pStatus)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_status
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        statusItem.bind(status)
        tabLayout.visibility = View.GONE
    }

    override fun getFragmentList(position: Int): Fragment {
        if (position > fragmentCount) throw object : RuntimeException("wrong fragment position"){}
        return when(position) {
            0 -> {
                val fragment = RecyclerViewFragment<Comment>()
                fragment.setRecyclerViewLayoutAdapter(CommentLayoutAdapter())
                fragment.setPresenter(CommentPresenter(fragment, status))
                fragment
            }
            1 -> Fragment()
            2 -> Fragment()
            else -> Fragment()
        }
    }

    override fun getFragmentTitle(position: Int): String {
        return when(position) {
            0 -> resources.getString(R.string.reposts) + "(${status.reposts_count})"
            1 -> resources.getString(R.string.comments) + "(${status.comments_count})"
            2 -> resources.getString(R.string.attitudes) + "(${status.attitudes_count})"
            else -> ""
        }
    }

    override fun getUniqueId(position: Int): Int {
        return position
    }

    override fun getFragmentCount(): Int {
        return 1
    }
}