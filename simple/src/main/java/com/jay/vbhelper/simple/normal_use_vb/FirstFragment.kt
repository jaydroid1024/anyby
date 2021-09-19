package com.jay.vbhelper.simple.normal_use_vb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.jay.vbhelper.simple.R
import com.jay.vbhelper.simple.databinding.FragmentFirstBinding
import com.jay.vbhelper.simple.delagate_vb.TextAdapterWithDelegate

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class FirstFragment : Fragment() {

    private lateinit var firstViewModel: FirstViewModel
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firstViewModel = ViewModelProvider(this).get(FirstViewModel::class.java)
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = adapter
        firstViewModel.dataList.observe(viewLifecycleOwner, {
            data.clear()
            data.addAll(it)
            adapter.submitList(data)
            // It will always be more efficient to use more specific change events if you can. Rely on notifyDataSetChanged as a last resort.
//            adapter.notifyDataSetChanged()
        })
        adapter.setOnItemClickListener {
            findNavController().navigate(
                R.id.action_FirstFragment_to_SecondFragment,
                Bundle().apply { putString("item", it) })
        }
    }

    private val data: ArrayList<String> by lazy { arrayListOf() }

    private val diffCallback: DiffCallback by lazy { DiffCallback() }

    private val adapter: TextAdapterWithDelegate by lazy {
        TextAdapterWithDelegate(diffCallback, data)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem.hashCode() == newItem.hashCode()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}