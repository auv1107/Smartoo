package com.antiless.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    private var isNavigationViewInit = false
    private var lastView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (lastView == null) {
            lastView = onCreateView(inflater, container)
        }
        return lastView
    }

    abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View?;
    abstract fun onViewCreated(view: View)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isNavigationViewInit) {
            onViewCreated(view)
            isNavigationViewInit = true
        }
    }
}