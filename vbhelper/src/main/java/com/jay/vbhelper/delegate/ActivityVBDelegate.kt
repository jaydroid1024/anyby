package com.jay.vbhelper.delegate

import android.app.Activity
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*
 * 在 Activity 中创建 ViewBinding 绑定类
 * 使用方式如下：
 * 1. 通过自定义属性代理 + 反射绑定类的 inflate 方法
 * private val binding: ActivityMainBinding by vb()
 * 2. 通过自定义属性代理 + 传递 inflate 方法引用
 * private val binding: ActivityMainBinding by vb(ActivityMainBinding::inflate)
 */

/**
 * 通过自定义属性代理 + 反射绑定类的 inflate 方法
 *
 * @param T ViewBinding 的子类
 */
inline fun <reified T : ViewBinding> Activity.vb() =
    object : ReadOnlyProperty<Activity, T> {
        private var binding: T? = null
        override fun getValue(thisRef: Activity, property: KProperty<*>): T {
            binding?.let { return it }
            val inflateMethod = T::class.java.getMethod("inflate", LayoutInflater::class.java)
            @Suppress("UNCHECKED_CAST")
            binding = inflateMethod.invoke(null, thisRef.layoutInflater) as T
            thisRef.setContentView(binding!!.root)
            return binding!!
        }
    }

/**
 * 通过自定义属性代理 + 传递 inflate 方法引用
 *
 * @param T ViewBinding 的子类
 * @param inflate LayoutInflater
 */
fun <T : ViewBinding> Activity.vb(inflate: (LayoutInflater) -> T) =
    object : ReadOnlyProperty<Activity, T> {
        private var binding: T? = null
        override fun getValue(thisRef: Activity, property: KProperty<*>): T {
            binding?.let { return it }
            binding = inflate(layoutInflater).also { setContentView(it.root) }
            return binding!!
        }
    }