package com.antiless.support.utils

import android.app.Activity
import android.content.Context
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
}