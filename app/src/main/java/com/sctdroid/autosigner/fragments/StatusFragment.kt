package com.sctdroid.autosigner.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.sctdroid.autosigner.R
import com.sina.weibo.sdk.openapi.models.Status
import kotlinx.android.synthetic.main.fragment_status.*
import org.parceler.Parcels

class StatusFragment : Fragment() {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statusItem.bind(status)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val v = TextView(context)
                v.text = "$position\n2131\n\n\n32423\n"
                return v
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return "$position"
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                if (obj is View && container.indexOfChild(obj) >= 0) {
                    container.removeView(obj)
                }
            }

            override fun getCount(): Int {
                return 3
            }
        }
    }
}