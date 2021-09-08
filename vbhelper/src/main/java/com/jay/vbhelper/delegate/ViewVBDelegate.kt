package com.jay.vbhelper.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/*
 * 在 View 中创建 ViewBinding 绑定类
 * 使用方式如下：
 * 1. 通过自定义属性代理 + 反射绑定类的 inflate 三参数方法
 * private val binding: MyViewBinding by vb()
 * 2. 通过自定义属性代理 + 传递 inflate 三参数方法引用
 * private val binding: MyViewBinding by vb(MyViewBinding::inflate)
 */

/**
 * 通过自定义属性代理 + 反射绑定类的 inflate 三参数方法
 *
 * @param T ViewBinding 的子类
 */
inline fun <reified T : ViewBinding> ViewGroup.vb() = object : ReadOnlyProperty<ViewGroup, T> {

    private var binding: T? = null

    override fun getValue(thisRef: ViewGroup, property: KProperty<*>): T {
        binding?.let { return it }
        val bindingClass = T::class.java
        //反射绑定类中三参数的 inflate 方法
        @Suppress("UNCHECKED_CAST")
        val inflateMethod = bindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        binding =
            inflateMethod.invoke(null, LayoutInflater.from(thisRef.context), thisRef, true) as T
        return binding!!
    }
}


/**
 * 通过自定义属性代理 + 传递绑定类 inflate 三参数方法引用
 *
 * @param T ViewBinding 的子类
 * @param inflate LayoutInflater
 */
fun <T : ViewBinding> ViewGroup.vb(inflate: (LayoutInflater, ViewGroup, Boolean) -> T) =
    object : ReadOnlyProperty<ViewGroup, T> {
        private var binding: T? = null
        override fun getValue(thisRef: ViewGroup, property: KProperty<*>): T {
            binding?.let { return it }
            val layoutInflater = LayoutInflater.from(thisRef.context)
            binding = inflate(layoutInflater, thisRef, true)
            return binding!!
        }
    }
