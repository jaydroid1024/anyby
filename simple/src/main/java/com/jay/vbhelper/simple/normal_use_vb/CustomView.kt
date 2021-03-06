package com.jay.vbhelper.simple.normal_use_vb

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.jay.vbhelper.delegate.vb
import com.jay.vbhelper.simple.databinding.LayoutViewBinding


@SuppressLint("SetTextI18n")
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutViewBinding by vb()

    fun setName(name: String) {
        binding.firstName.text = name
    }

}