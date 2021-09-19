package com.jay.vbhelper.simple.delegate_sp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class SPTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("sp_app_jay", Context.MODE_PRIVATE)

        //缓存Token的场景
//        val tokenHolder = TokenHolder(prefs)
//        Log.d("Jay", "tokenHolder:$tokenHolder")
//        tokenHolder.saveToken("token_one")
//        tokenHolder.saveToken("token_second")
//
//        //缓存登录信息的场景
//        val userHolder = UserHolder(prefs)
//        Log.d("Jay", "userHolder:$userHolder")
//        userHolder.saveUserAccount("jay", "123456")


    }


}