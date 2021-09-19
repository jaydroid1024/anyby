package com.jay.vbhelper.delagate_vb

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.jay.vbhelper.databinding.LayoutViewBinding
import com.jay.vbhelper.delegate.vb


@SuppressLint("SetTextI18n")
class CustomViewWithDelegate @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutViewBinding by vb()
//    private val binding: LayoutViewBinding by vb(LayoutViewBinding::inflate)

    fun setName(name: String) {
        binding.firstName.text = name
    }


}
