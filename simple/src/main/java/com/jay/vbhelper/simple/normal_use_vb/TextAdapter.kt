package com.jay.vbhelper.simple.normal_use_vb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jay.vbhelper.simple.databinding.LayoutItemTextBinding

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class TextAdapter(
    diffCallback: DiffUtil.ItemCallback<String>,
    private val list: List<String>
) : ListAdapter<String, TextAdapter.TextHolder>(diffCallback) {

    private var function: ((item: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHolder {
        val itemBinding =
            LayoutItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //绑定类交给Holder
        return TextHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TextHolder, position: Int) {
        val item: String = list[position]
        //数据交给Holder
        holder.bind(item)
        holder.itemBinding.root.setOnClickListener {
            function?.invoke(item)
        }
    }

    class TextHolder(val itemBinding: LayoutItemTextBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(name: String) {
            itemBinding.tvName.text = name
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnItemClickListener(function: (item: String) -> Unit) {
        this.function = function
    }


}
