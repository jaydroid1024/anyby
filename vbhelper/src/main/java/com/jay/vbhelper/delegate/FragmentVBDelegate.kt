/**
 * 在 Fragment 中创建 ViewBinding 绑定类
 * 使用方式如下：
 * 1. 借助 lazy 属性委托  + 反射 VB 的 inflate 方法
 * private val binding: FragmentMainBinding by vb()
 * override fun onCreateView(l: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
 *  return binding.root //通过 inflate 方法获取 VB 的方式需要在 onCreateView 中返回根视图
 * }
 *
 * 2. 借助 lazy 属性委托  + 传递 VB 的 inflate 方法引用
 * private val binding: FragmentMainBinding by vb(FragmentMainBinding::inflate)
 * override fun onCreateView(l: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
 *  return binding.root //通过 inflate 方法获取 VB 的方式需要在 onCreateView 中返回根视图
 * }
 *
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/17
 */

package com.jay.vbhelper.delegate

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass


/**
 * 在 Fragment 中创建 ViewBinding 绑定类
 *
 * @param T ViewBinding 的子类
 * @param inflateMethodRef (LayoutInflater) -> T) VB 中 inflate 方法的函数引用
 */
@MainThread
inline fun <reified T : ViewBinding> Fragment.vb(noinline inflateMethodRef: ((LayoutInflater) -> T)? = null): Lazy<T> =
    FragmentVNLazy(this, T::class, inflateMethodRef)


class FragmentVNLazy<T>(
    private val fragment: Fragment,
    private val kClass: KClass<*>,
    private val inflateMethodRef: ((LayoutInflater) -> T)?
) : Lazy<T> {
    private var cachedBinding: T? = null
    private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }

    init {
        observeFragmentDestroy(fragment) { clearBindingHandler.post { cachedBinding = null } }
    }

    override val value: T
        get() {
            var viewBinding = cachedBinding
            if (viewBinding == null) {
                checkBindingFirstInvoke(fragment)
                viewBinding = if (inflateMethodRef != null) {
                    //借助 lazy 属性委托 + 传递 inflate 方法引用
                    inflateMethodRef.invoke(fragment.layoutInflater)
                } else {
                    //借助 lazy 属性委托  + 反射绑定类的 inflate 方法
                    @Suppress("UNCHECKED_CAST")
                    kClass.java.getMethod(METHOD_INFLATE, LayoutInflater::class.java)
                        .invoke(null, fragment.layoutInflater) as T
                }
                cachedBinding = viewBinding
            }
            return viewBinding!!
        }


    override fun isInitialized() = cachedBinding != null

}


fun observeFragmentDestroy(
    fragment: Fragment,
    callback: () -> Unit
) {
    fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @androidx.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                Log.i(TAG, "${fragment::class.java.simpleName} call onDestroy")
                callback.invoke()
            }
        })
    }
}

private fun checkBindingFirstInvoke(fragment: Fragment) {
    if (!fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
        error("Cannot access view bindings. View lifecycle is ${fragment.viewLifecycleOwner.lifecycle.currentState}!")
    }
}






