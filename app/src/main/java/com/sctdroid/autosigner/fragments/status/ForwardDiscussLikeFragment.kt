package com.sctdroid.autosigner.fragments.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.antiless.support.fragment.TabFragment
import com.antiless.support.fragment.list.RecyclerViewFragment
import com.sctdroid.autosigner.R
import com.sina.weibo.sdk.openapi.models.Comment
import com.sina.weibo.sdk.openapi.models.Status
import org.parceler.Parcels
import java.lang.RuntimeException

open class ForwardDiscussLikeFragment : TabFragment() {

    companion object {
        private const val KEY_STATUS = "status"
        fun newInstance(status: Status): ForwardDiscussLikeFragment {
            val args = Bundle().apply{
                putParcelable(KEY_STATUS, Parcels.wrap(status))
            }
            return ForwardDiscussLikeFragment().apply { arguments = args }
        }
    }

    private lateinit var status: Status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = Parcels.unwrap(arguments?.getParcelable(KEY_STATUS))
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_forward_discuss_like
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
        return 3
    }
}