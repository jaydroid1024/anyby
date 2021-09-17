/**
 * FragmentVMDelegateKt
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/17
 */
@file:kotlin.jvm.JvmName("FragmentVMDelegateKt")

package com.jay.vbhelper.delegate_vm.vm

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass


/**
 * 返回一个属性委托来访问 Activity 的ViewModel ，如果指定了工厂，它将第一次用于创建ViewModel 。
 *  class MyFragment : Fragment() {
 *  val viewmodel: NyViewModel by vm()
 *   }
 * 只有在 Fragment attached 之后才能访问此属性，即在 Fragment.onAttach() 之后，在此之前访问将导致 IllegalArgumentException。
 *
 * @param VM  NyViewModel
 * @param factory AndroidViewModelFactory
 * @return viewModelInstance
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.vm(factory: Factory? = null): Lazy<VM> =
    FragmentVMLazy(this, VM::class, factory)


/**
 * Fragment VM 使用实现 Lazy 接口的方式实现委托并与给定的 fragment、 viewModelClass 、factory 相关联
 * @param VM NyViewModel
 * @property fragment MyFragment
 * @property viewModelClass viewModelClass
 * @property factory AndroidViewModelFactory
 */
class FragmentVMLazy<VM : ViewModel>(
    private val fragment: Fragment,
    private val viewModelClass: KClass<VM>,
    private val factory: Factory?
) : Lazy<VM> {
    private var cached: VM? = null
    override val value: VM
        get() {
            var viewModel = cached
            if (viewModel == null) {
                val application = fragment.activity?.application
                    ?: throw IllegalArgumentException(
                        "ViewModel can be accessed only when Fragment is attached"
                    )
                val resolvedFactory = factory ?: AndroidViewModelFactory.getInstance(application)
                viewModel = ViewModelProvider(fragment, resolvedFactory).get(viewModelClass.java)
                cached = viewModel
            }
            return viewModel
        }

    override fun isInitialized() = cached != null
}


/**
 * Returns a property delegate to access [ViewModel] by **default** scoped to this [Fragment]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MYViewModel by viewmodels()
 * }
 * ```
 *
 * Custom [ViewModelProvider.Factory] can be defined via [factoryProducer] parameter,
 * factory returned by it will be used to create [ViewModel]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MYViewModel by viewmodels { myFactory }
 * }
 * ```
 *
 * Default scope may be overridden with parameter [ownerProducer]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MYViewModel by viewmodels ({requireParentFragment()})
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.vm2(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> Factory)? = null
) = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)

/**
 * Returns a property delegate to access parent activity's [ViewModel],
 * if [factoryProducer] is specified then [ViewModelProvider.Factory]
 * returned by it will be used to create [ViewModel] first time. Otherwise, the activity's
 * [androidx.activity.ComponentActivity.getDefaultViewModelProviderFactory](default factory)
 * will be used.
 *
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MyViewModel by activityViewModels()
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModels(
    noinline factoryProducer: (() -> Factory)? = null
) = createViewModelLazy(VM::class, { requireActivity().viewModelStore },
    factoryProducer ?: { requireActivity().defaultViewModelProviderFactory })

/**
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 */
@MainThread
fun <VM : ViewModel> Fragment.createViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise)
}