package com.jay.vbhelper.delagate_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewbinding.ViewBinding
import com.jay.vbhelper.R
import com.jay.vbhelper.databinding.ActivityMainBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class MainActivityWithDelegateTest : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var binding: ActivityMainBinding? = null

    //通过 lazy 属性代理 + inflate方法引用
    private val binding1 by binding1(ActivityMainBinding::inflate)

    //通过自定义属性代理 + inflate方法引用
    private val binding2 by binding2(ActivityMainBinding::inflate)

    //通过 lazy 属性代理 + 反射
    private val binding3: ActivityMainBinding by binding3()

    //通过自定义属性代理+ 反射
    private val binding4: ActivityMainBinding by binding4()

    //通过 lazy 属性代理 + inflate方法引用
    fun <VB : ViewBinding> ComponentActivity.binding1(inflate: (LayoutInflater) -> VB) =
        lazy {
            inflate(layoutInflater).also {
                setContentView(it.root)
            }
        }

    //通过自定义属性代理 + inflate方法引用
    fun <VB : ViewBinding> ComponentActivity.binding2(inflate: (LayoutInflater) -> VB) =
        object : ReadOnlyProperty<ComponentActivity, VB> {
            private var binding: VB? = null
            override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): VB {
                if (binding == null) {
                    binding = inflate(layoutInflater).also { setContentView(it.root) }
                }
                return binding!!
            }
        }


    //通过 lazy 属性代理 + 反射
    //reified 实化类型参数，作用是将泛型替换为真实的类型用于反射等
    inline fun <reified VB : ViewBinding> ComponentActivity.binding3() =
        lazy {
            //经过内联后VB是可以确切知道具体类型的，所以这里可以反射获取具体的 ViewBinding
            val viewBinding: VB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, layoutInflater) as VB
            viewBinding.also {
                setContentView(it.root)
            }
        }

    //通过自定义属性代理+ 反射
    //reified 实化类型参数，作用是将泛型替换为真实的类型用于反射等
    inline fun <reified VB : ViewBinding> ComponentActivity.binding4() =
        ReadOnlyProperty<ComponentActivity, VB> { thisRef, property ->
            //经过内联后VB是可以确切知道具体类型的，所以这里可以反射获取具体的 ViewBinding
            val viewBinding: VB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, layoutInflater) as VB
            viewBinding.also {
                setContentView(it.root)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Jay", "onCreate")
        val t1 = System.currentTimeMillis()
        binding = binding3
        val t3 = System.currentTimeMillis()
        Log.d("Jay", "onCreate, t3-t1=" + (t3 - t1))//onCreate, t3-t1=64

        setSupportActionBar(binding?.toolbar) //todo 第一次调用 binding 属性的时候回调lazy代码块

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}