package com.jay.vbhelper.delegate_sp

import android.content.SharedPreferences
import com.jay.anyby.sp.string

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/18
 */
class UserHolder(prefs: SharedPreferences) {

    var name: String by prefs.string()
        private set

    var pwd: String by prefs.string()
        private set

    fun saveUserAccount(name: String, pwd: String) {
        this.name = name
        this.pwd = pwd
    }

    override fun toString(): String {
        return "UserHolder(name='$name', pwd='$pwd')"
    }

}
