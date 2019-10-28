package com.sctdroid.autosigner.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.antiless.support.BaseFragment
import com.sctdroid.autosigner.R

class UserFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View) {
    }

}