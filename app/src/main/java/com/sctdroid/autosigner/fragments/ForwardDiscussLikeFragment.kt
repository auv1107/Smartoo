package com.sctdroid.autosigner.fragments

import androidx.fragment.app.Fragment
import com.antiless.support.fragment.TabFragment
import com.sctdroid.autosigner.R
import java.lang.RuntimeException

class ForwardDiscussLikeFragment : TabFragment() {

    override fun getFragmentList(position: Int): Fragment {
        if (position > fragmentCount) throw object : RuntimeException("wrong fragment position"){}
        return when(position) {
            0 -> Fragment()
            1 -> Fragment()
            2 -> Fragment()
            else -> Fragment()
        }
    }

    override fun getFragmentTitle(position: Int): String {
        return when(position) {
            0 -> resources.getString(R.string.reposts)
            1 -> resources.getString(R.string.comments)
            2 -> resources.getString(R.string.attitudes)
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