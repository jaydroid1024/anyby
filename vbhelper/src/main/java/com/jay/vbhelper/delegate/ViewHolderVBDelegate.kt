/**
 * 在 Adapter 中创建包含了属性代理绑定类的 ViewHolder
 * 使用方式如下：
 * 1. 借助 lazy 属性委托  + 反射绑定类的 inflate 三参数方法
 * val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent)
 * 2. 借助 lazy 属性委托  + 传递绑定类的 inflate 三参数方法引用
 * val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent, LayoutItemTextBinding::inflate)
 *
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/17
 */

package com.jay.vbhelper.delegate

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * 借助 lazy 属性委托  + 反射绑定类的 inflate 三参数方法
 *
 * @param T ViewBinding 的子类
 * @param parent RecyclerView 父布局
 */
inline fun <reified T : ViewBinding> Any.vh(parent: ViewGroup) =
    object : ReadOnlyProperty<Nothing?, BindingViewHolder<T>> {
        @SuppressLint("StaticFieldLeak")
        private var binding: BindingViewHolder<T>? = null
        override fun getValue(thisRef: Nothing?, property: KProperty<*>): BindingViewHolder<T> {
            binding?.let { return it }
            binding = BindingViewHolder(inflateBinding(parent))
            return binding!!
        }
    }

/**
 * 借助 lazy 属性委托  + 传递绑定类的 inflate 三参数方法引用
 *
 * @param T ViewBinding 的子类
 * @param parent RecyclerView 父布局
 */
inline fun <reified T : ViewBinding> vh(
    parent: ViewGroup,
    noinline inflate: (LayoutInflater, ViewGroup, Boolean) -> T
) = object : ReadOnlyProperty<Nothing?, BindingViewHolder<T>> {
    @SuppressLint("StaticFieldLeak")
    private var binding: BindingViewHolder<T>? = null
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): BindingViewHolder<T> {
        binding?.let { return it }
        binding = BindingViewHolder(parent, inflate)
        return binding!!
    }
}

/**
 * 包含了属性代理绑定类的 ViewHolder
 *
 * @param T ViewBinding 的子类
 * @property binding 绑定类
 */
class BindingViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup, inflate: (LayoutInflater, ViewGroup, Boolean) -> T) : this(
        inflate(LayoutInflater.from(parent.context), parent, false)
    )
}

/**
 * 反射绑定类的 inflate 三参数方法
 *
 * @param T ViewBinding 的子类
 * @param parent RecyclerView 父布局
 */
inline fun <reified T : ViewBinding> inflateBinding(parent: ViewGroup) = T::class.java.getMethod(
    METHOD_INFLATE,
    LayoutInflater::class.java,
    ViewGroup::class.java,
    Boolean::class.java
).invoke(null, LayoutInflater.from(parent.context), parent, false) as T

