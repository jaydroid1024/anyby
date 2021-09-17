/**
 * ActivityVMDelegate
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/17
 */
@file:kotlin.jvm.JvmName("ActivityVMDelegateKt")

package com.jay.vbhelper.delegate_vm.vm

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import kotlin.reflect.KClass

/**
 * 返回一个 Lazy 委托来访问 ComponentActivity 的 ViewModel，如果指定了工厂，它将用于第一次创建ViewModel。
 * class MyComponentActivity : ComponentActivity() {
 * val viewmodel: MyViewModel by vm()
 * }
 * 只有在 Activity attached 到应用程序后才能访问此属性，在此之前访问将导致 IllegalArgumentException
 *
 * @param VM NyViewModel
 * @param factory AndroidViewModelFactory
 * @return viewModelInstance
 */

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.vm(
    factory: ViewModelProvider.Factory? = null
): Lazy<VM> = ActivityVMLazy(this, VM::class, factory)

/**
 * ComponentActivity VM 使用实现 Lazy 接口的方式实现委托并与给定的 activity、 viewModelClass 、factory 相关联
 * @param VM NyViewModel
 * @property activity MyFragment
 * @property viewModelClass viewModelClass
 * @property factory AndroidViewModelFactory
 */
class ActivityVMLazy<VM : ViewModel>(
    private val activity: ComponentActivity,
    private val viewModelClass: KClass<VM>,
    private val factory: ViewModelProvider.Factory?
) : Lazy<VM> {
    private var cached: VM? = null
    override val value: VM
        get() {
            var viewModel = cached
            if (viewModel == null) {
                val application = activity.application
                    ?: throw IllegalArgumentException(
                        "ViewModel can be accessed only when Activity is attached"
                    )
                val resolvedFactory = factory ?: AndroidViewModelFactory.getInstance(application)
                viewModel = ViewModelProvider(activity, resolvedFactory).get(viewModelClass.java)
                cached = viewModel
            }
            return viewModel
        }

    override fun isInitialized() = cached != null
}


/**
 * Returns a [Lazy] delegate to access the ComponentActivity's ViewModel, if [factoryProducer]
 * is specified then [ViewModelProvider.Factory] returned by it will be used
 * to create [ViewModel] first time.
 *
 * ```
 * class MyComponentActivity : ComponentActivity() {
 *     val viewmodel: MyViewModel by viewmodels()
 * }
 * ```
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.vm2(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {

    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}