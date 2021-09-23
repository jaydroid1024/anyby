package com.jay.vbhelper.simple.delegate_vm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jay.anyby.vm.vm

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class ViewModelTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //在系统第一次调用 activity 的 onCreate() 方法时创建一个 ViewModel。
        // 重新创建的 activity 接收由第一个 activity 创建的相同 MyViewModel 实例。
        // 使用来自 activity-ktx 工件的“by viewModels()”Kotlin 属性委托
        val model: MyViewModel by vm()

    }

}