package com.example.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventory.databinding.FragmentItemListBinding

class ItemListFragment : Fragment() {
    private val viewModel:InventoryViewModel by activityViewModels(){
        InventoryViewModelFactory((activity?.application as InventoryApplication).database.itemDao())
    }
    private var _binding:FragmentItemListBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentItemListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter=ItemListAdapter{
            val action=ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager=LinearLayoutManager(this.context)
        binding.recyclerView.adapter=adapter
        viewModel.allItems.observe(this.viewLifecycleOwner){item->
            item.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener{
            val action=ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(getString(R.string.add_fragment_title))
            this.findNavController().navigate(action)
        }
    }
}