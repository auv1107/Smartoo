package com.antiless.support.utils

/**
 * Created by lixindong on 2018/8/5.
 */


fun <T> checkNotNull(reference: T?): T {
    if (reference == null) {
        throw NullPointerException()
    }
    return reference
}

fun <T> checkNotNull(reference: T?, errorMessage: Any?): T {
    if (reference == null) {
        throw NullPointerException(errorMessage.toString())
    }
    return reference
}