package com.jay.vbhelper.delagate_vb

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jay.vbhelper.databinding.LayoutItemTextBinding
import com.jay.vbhelper.delegate.BindingViewHolder
import com.jay.vbhelper.delegate.vh

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class TextAdapterWithDelegate(
    diffCallback: DiffUtil.ItemCallback<String>,
    private val list: List<String>
) : ListAdapter<String, BindingViewHolder<LayoutItemTextBinding>>(diffCallback) {

    private var function: ((item: String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<LayoutItemTextBinding> {

        val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent)
        val holder2: BindingViewHolder<LayoutItemTextBinding> by vh(
            parent,
            LayoutItemTextBinding::inflate
        )
        return holder2
    }

    override fun onBindViewHolder(holder: BindingViewHolder<LayoutItemTextBinding>, position: Int) {
        val item: String = list[position]
        holder.binding.tvName.text = item
        holder.binding.root.setOnClickListener {
            function?.invoke(item)
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnItemClickListener(function: (item: String) -> Unit) {
        this.function = function
    }


}


