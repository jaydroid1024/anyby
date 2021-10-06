package com.jay.vbhelper.simple

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jay.vbhelper.delegate.vb
import com.jay.vbhelper.simple.databinding.ActivitySimpleBinding
import com.jay.vbhelper.simple.delagate_vb.MainActivityWithDelegate
import com.jay.vbhelper.simple.delegate_api.ApiTestActivity
import com.jay.vbhelper.simple.delegate_sp.SPTestActivity
import com.jay.vbhelper.simple.delegate_vm.ViewModelTestActivity
import com.jay.vbhelper.simple.inflate_test.InflateTestActivity
import com.jay.vbhelper.simple.normal_use_vb.MainActivity

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class SimpleActivity : AppCompatActivity() {

    private val binding: ActivitySimpleBinding by vb()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
    }


    fun normalUseVb(view: android.view.View) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun delegateVB(view: android.view.View) {
        startActivity(Intent(this, MainActivityWithDelegate::class.java))
    }

    fun inflateTest(view: android.view.View) {
        startActivity(Intent(this, InflateTestActivity::class.java))
    }

    fun delegateSP(view: android.view.View) {
        startActivity(Intent(this, SPTestActivity::class.java))
    }

    fun delegateVM(view: android.view.View) {
        startActivity(Intent(this, ViewModelTestActivity::class.java))
    }

    fun delegateApi(view: android.view.View) {
        startActivity(Intent(this, ApiTestActivity::class.java))
    }

}