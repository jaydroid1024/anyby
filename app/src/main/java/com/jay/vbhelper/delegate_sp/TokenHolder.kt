package com.jay.vbhelper.delegate_sp

import android.content.SharedPreferences
import com.jay.anyby.sp.int
import com.jay.anyby.sp.string

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/18
 */
class TokenHolder(prefs: SharedPreferences) {

    var token: String by prefs.string()
        private set

    var count by prefs.int()
        private set

    fun saveToken(newToken: String) {
        token = newToken
        count++
    }

    override fun toString(): String {
        return "TokenHolder(token='$token', count=$count)"
    }

}
