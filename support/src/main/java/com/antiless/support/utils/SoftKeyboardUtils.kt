package com.antiless.support.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by lixindong on 2018/8/19.
 */

object SoftKeyboardUtils {
    fun hideSoftKeyboard(editText: EditText?, context: Context?) {
        if (editText != null && context != null) {
            val imm = context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    fun showSoftKeyboard(editText: EditText?, context: Context?) {
        if (editText != null && context != null) {
            val imm = context
                    .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, 0)
        }
    }

    private var isFirst = true

    interface OnGetSoftHeightListener {
        fun onShowed(height: Int)
    }

    interface OnSoftKeyWordShowListener {
        fun hasShow(isShow: Boolean)
    }

    /**
     * 获取软键盘的高度 * *
     *
     * @param rootView *
     * @param listener
     */
    fun getSoftKeyboardHeight(rootView: View, listener: OnGetSoftHeightListener?) {
        val layoutListener: OnGlobalLayoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isFirst) {
                    val rect = Rect()
                    rootView.getWindowVisibleDisplayFrame(rect)
                    val screenHeight = rootView.rootView.height
                    val heightDifference = screenHeight - rect.bottom
                    //设置一个阀值来判断软键盘是否弹出


                    val visible = heightDifference > screenHeight / 3
                    if (visible) {
                        isFirst = false
                        listener?.onShowed(heightDifference)
                        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }


    /**
     * 判断软键盘是否弹出
     * * @param rootView
     *
     * @param listener 备注：在不用的时候记得移除OnGlobalLayoutListener
     */
    fun doMonitorSoftKeyWord(rootView: View, listener: OnSoftKeyWordShowListener?): OnGlobalLayoutListener {
        val layoutListener = OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            Log.e("TAG", rect.bottom.toString() + "#" + screenHeight)
            val heightDifference = screenHeight - rect.bottom
            val visible = heightDifference > screenHeight / 3
            listener?.hasShow(visible)
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        return layoutListener
    }

}