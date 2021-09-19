package com.jay.vbhelper.simple.normal_use_vb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jay.vbhelper.simple.R
import com.jay.vbhelper.simple.databinding.*

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedIS: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        InfoLayoutInfoBinding.bind(binding.root)

        //CustomView
        binding.name.alpha = 0.5f

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}