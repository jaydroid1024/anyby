/**
 * 在 Activity 中创建 ViewBinding 绑定类
 * 使用方式如下：
 * 1. 借助 lazy 属性委托  + 反射 VB 的 inflate 方法
 * private val binding: ActivityMainBinding by vb()
 * 2. 借助 lazy 属性委托  + 传递 inflate 方法引用
 * private val binding: ActivityMainBinding by vb(ActivityMainBinding::inflate)
 *
 * 注意：需要在生命周期方法中访问 binding 属性才能执行 setContentView 布局填充流程，如果页面中没有交互的控件时需要注意这一点
 *
 * 借助 lazy 属性委托的优势：
 * Kotlin 1.4 做的优化，当某些委托属性不会使用 KProperty。
 * 对于他们来说，在 $$delegatedProperties 中生成 KProperty 对象是多余的。
 * Kotlin 1.4 版本将优化此类情况。如果委托的属性运算符是内联的，并且没有使用 KProperty 参数，则不会生成相应的反射对象。
 * 参考博客：What to Expect in Kotlin 1.4 and Beyond | Optimized delegated properties
 * https://blog.jetbrains.com/kotlin/2019/12/what-to-expect-in-kotlin-1-4-and-beyond/
 *
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/17
 */
package com.jay.vbhelper.delegate

import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass


/**
 * 在 Activity 中创建 ViewBinding 绑定类，
 *
 * @param inflateMethodRef (LayoutInflater) -> T) VB 中 inflate 方法的函数引用
 * @param T ViewBinding 的子类
 */
@MainThread
inline fun <reified T : ViewBinding> ComponentActivity.vb(noinline inflateMethodRef: ((LayoutInflater) -> T)? = null): Lazy<T> =
    ActivityVBLazyWithReflect(this, T::class, inflateMethodRef)


class ActivityVBLazyWithReflect<T : ViewBinding>(
    private val activity: ComponentActivity,
    private val kClass: KClass<*>,
    private val inflateMethodRef: ((LayoutInflater) -> T)?
) : Lazy<T> {
    private var cachedBinding: T? = null
    override val value: T
        get() {
            var viewBinding = cachedBinding
            if (viewBinding == null) {
                viewBinding = if (inflateMethodRef != null) {
                    //借助 lazy 属性委托 + 传递 inflate 方法引用
                    inflateMethodRef.invoke(activity.layoutInflater)
                } else {
                    //借助 lazy 属性委托  + 反射绑定类的 inflate 方法
                    @Suppress("UNCHECKED_CAST")
                    kClass.java.getMethod(METHOD_INFLATE, LayoutInflater::class.java)
                        .invoke(null, activity.layoutInflater) as T
                }
                activity.setContentView(viewBinding.root)
                cachedBinding = viewBinding
            }
            return viewBinding
        }

    override fun isInitialized() = cachedBinding != null
}


const val TAG = "VBHelper"
const val METHOD_INFLATE = "inflate"