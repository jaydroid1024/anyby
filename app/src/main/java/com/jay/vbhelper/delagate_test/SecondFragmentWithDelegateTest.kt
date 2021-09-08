package com.jay.vbhelper.delagate_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.jay.vbhelper.R
import com.jay.vbhelper.databinding.FragmentSecondBinding
import com.jay.vbhelper.databinding.LayoutInfoBinding
import com.jay.vbhelper.databinding.LayoutInfoMergeBinding
import com.jay.vbhelper.databinding.LayoutInfoViewStubBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class SecondFragmentWithDelegateTest : Fragment() {

    private val binding1: FragmentSecondBinding by binding1()

    private val binding2: FragmentSecondBinding by binding2()


    //通过自定义属性代理 + 反射
    //reified 实化类型参数，作用是将泛型替换为真实的类型用于反射等
    inline fun <reified VB : ViewBinding> Fragment.binding1() =
        object : ReadOnlyProperty<Fragment, VB> {
            private var binding: VB? = null
            override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
                if (binding == null) {
                    Log.d("Jay", "getValue")
                    binding = requireView().bind()
                    thisRef.doOnDestroyView {
                        binding = null
                        Log.d("Jay", "doOnDestroyView")
                    }
                }
                return binding!!
            }

            inline fun <reified VB : ViewBinding> View.bind(): VB {
                return VB::class.java.getMethod("bind", View::class.java).invoke(null, this) as VB
            }

            inline fun Fragment.doOnDestroyView(crossinline block: () -> Unit) =
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroyView() {
                        block.invoke()
                    }
                })
        }

    //通过自定义属性代理 + 反射
    //reified 实化类型参数，作用是将泛型替换为真实的类型用于反射等
    inline fun <reified VB : ViewBinding> Fragment.binding2() =
        object : ReadOnlyProperty<Fragment, VB> {
            private var binding: VB? = null
            override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
                if (binding == null) {
                    Log.d("Jay", "getValue")
                    binding = inflate()
                    thisRef.doOnDestroyView {
                        binding = null
                        Log.d("Jay", "doOnDestroyView")
                    }
                }
                return binding!!
            }

            inline fun <reified VB : ViewBinding> Fragment.inflate(): VB {
                //经过内联后VB是可以确切知道具体类型的，所以这里可以反射获取具体的 ViewBinding
                return VB::class.java.getMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                ).invoke(layoutInflater, null, false) as VB
            }


            inline fun Fragment.doOnDestroyView(crossinline block: () -> Unit) =
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroyView() {
                        block.invoke()
                    }
                })
        }


//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_second, container, false)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding2.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding2

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        val item = arguments?.get("item")?.toString()

        binding.textviewSecond.text = item

        //include
//        binding.includeLayout.tvInfoInclude.text = "tvInfoInclude:$item"
        // todo  include 方式有时候无法识别到真实的绑定类类型只能识别它是个View类型但是编译不会报错, 这种情况清理缓存可能会好 ，或者也可以强制类型转换或者自己bind
//        val tvInfoInclude: LayoutInfoBinding = binding.includeLayout as LayoutInfoBinding
        // 找到include的根布局，手动绑定
        val includeLayout = view.findViewById<ConstraintLayout>(R.id.include_layout)
        val tvInfoInclude = LayoutInfoBinding.bind(includeLayout)
        tvInfoInclude.tvInfoInclude.text = "tvInfoInclude:$item"


        //include+merge 只能手动调用绑定类的bind方法
        val layoutInfoMergeBinding = LayoutInfoMergeBinding.bind(binding.root)
        val tvInfoMerge = layoutInfoMergeBinding.tvInfoMerge
        tvInfoMerge.text = "tvInfoMerge:$item"

        //ViewStub 只能手动调用绑定类的bind方法
        binding.layoutViewStub.setOnInflateListener { _, inflateId ->
            val layoutInfoViewStubBinding = LayoutInfoViewStubBinding.bind(inflateId)
            val tvInfoViewStub = layoutInfoViewStubBinding.tvInfoViewStub
            tvInfoViewStub.text = "tvInfoViewStub:$item"
        }
        binding.layoutViewStub.inflate()

        //CustomView
        binding.name.alpha = 0.5f

    }

}