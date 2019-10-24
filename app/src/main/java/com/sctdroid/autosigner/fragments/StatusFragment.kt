package com.sctdroid.autosigner.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sctdroid.autosigner.R
import com.sina.weibo.sdk.openapi.models.Status
import kotlinx.android.synthetic.main.fragment_status.*
import org.parceler.Parcels

class StatusFragment : BaseNavigationFragment() {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View) {
        statusItem.bind(status)
    }
}