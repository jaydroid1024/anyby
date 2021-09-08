package com.jay.vbhelper.delegate

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Field
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*
 * 在 Fragment 中创建 ViewBinding 绑定类
 * 使用方式如下：
 * 1. 通过自定义属性代理 + 反射绑定类的 inflate 方法
 * private val binding: FragmentMainBinding by vb()
 * 2. 通过自定义属性代理 + 传递 inflate 方法引用
 * private val binding: FragmentMainBinding by vb(FragmentMainBinding::inflate)
 */

/**
 * 通过自定义属性代理 + 反射绑定类的 inflate 方法
 *
 * @param T ViewBinding 的子类
 */
inline fun <reified T : ViewBinding> Fragment.vb() = fragmentVBDelegate<T>(this, -1)


/**
 * todo Fragment 布局 id
 * 通过自定义属性代理 + 反射绑定类的 inflate 方法+Fragment 布局 id
 *
 * @param T ViewBinding 的子类
 * @param layoutIdRes Fragment 布局 id 用于解脱 OnCreateView 方法
 */
inline fun <reified T : ViewBinding> Fragment.vb(layoutIdRes: Int) =
    fragmentVBDelegate<T>(this, layoutIdRes)

/**
 * 通过自定义属性代理 + 传递 inflate 方法引用
 *
 * @param T ViewBinding 的子类
 * @param layoutIdRes Fragment 布局id 用于解脱 OnCreateView 方法
 * @param fragment fragment
 * @return ReadOnlyProperty
 */
inline fun <reified T : ViewBinding> fragmentVBDelegate(
    fragment: Fragment,
    layoutIdRes: Int
): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T> {
        private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }
        private var binding: T? = null


        val inflateMethod = T::class.java.getMethod("inflate", LayoutInflater::class.java)

        init {
            Log.d("Jay", "layoutIdRes:$layoutIdRes")

//            fragment.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
//                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//                fun onDestroyView() {
//                    Log.i("FragmentVBDelegate", "onDestroy,binding:$binding")
//                }
//
//                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//                fun onCreate() {
//                    Log.i("FragmentVBDelegate", "onCreate,binding:$binding")
//                }
//            })

            fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        clearBindingHandler.post { binding = null }
                        Log.i("FragmentVBDelegate", "onDestroy,binding:$binding")
                    }
                })
            }
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            binding?.let { return it }
            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                error("Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
            }
            @Suppress("UNCHECKED_CAST")
            binding = inflateMethod.invoke(null, thisRef.layoutInflater) as T
            return binding!!
        }
    }


/**
 * 通过自定义属性代理 + 传递 inflate 方法引用
 *
 * @param T ViewBinding 的子类
 * @param inflate LayoutInflater
 */
fun <T : ViewBinding> Fragment.vb(inflate: (LayoutInflater) -> T) =

    object : ReadOnlyProperty<Fragment, T> {
        private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }
        private var binding: T? = null

        init {
            this@vb.viewLifecycleOwnerLiveData.observe(this@vb) { viewLifecycleOwner ->
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        clearBindingHandler.post { binding = null }
                        Log.i("FragmentVBDelegate", "onDestroy,binding:$binding")
                    }
                })
            }
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            binding?.let { return it }
            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                error("Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
            }
            binding = inflate(layoutInflater)
            return binding!!
        }
    }







