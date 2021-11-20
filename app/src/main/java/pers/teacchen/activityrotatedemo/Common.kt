package pers.teacchen.activityrotatedemo

import android.util.Log

fun debugLog(msg: String) {
    Log.d("chenhj", msg)
}

val Any.identifyStr: String
    get() = "${javaClass.simpleName}@${hashCode()}"