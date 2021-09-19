package com.jay.vbhelper.delagate_vb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jay.vbhelper.R
import com.jay.vbhelper.databinding.FragmentSecondBinding
import com.jay.vbhelper.databinding.LayoutInfoBinding
import com.jay.vbhelper.databinding.LayoutInfoMergeBinding
import com.jay.vbhelper.databinding.LayoutInfoViewStubBinding
import com.jay.vbhelper.delegate.vb

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class SecondFragmentWithDelegate : Fragment() {

    //通过自定义属性代理 + 反射 bind 方法，这种方式需要提前加载好 view
    private val binding2: FragmentSecondBinding by vb()

    private val binding: FragmentSecondBinding by vb(FragmentSecondBinding::inflate)

//    private val binding: FragmentSecondBinding by vb(R.layout.fragment_second)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Jay", "00layoutIdRes")

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
        binding.name1.setName("CustomView")
        binding.name.setName("CustomViewWithDelegate")

    }

}