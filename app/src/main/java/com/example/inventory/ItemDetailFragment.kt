package com.example.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.data.getFormattedPrice
import com.example.inventory.databinding.FragmentItemDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ItemDetailFragment : Fragment() {
    private val navigationArgs:ItemDetailFragmentArgs by navArgs()

    private var _binding:FragmentItemDetailBinding?=null
    private val binding get()=_binding!!
    lateinit var item:Item
    private val viewModel:InventoryViewModel by activityViewModels(){
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }
    private fun bind(item: Item){
        binding.apply {
            itemName.text = item.itemName
            itemPrice.text = item.getFormattedPrice()
            itemCount.text = item.quantityInStock.toString()
            sellItem.isEnabled=viewModel.isStockAvailable(item)
            sellItem.setOnClickListener { viewModel.sellItem(item) }
            deleteItem.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editItem() }
        }
    }
    private fun editItem(){
        val action=ItemDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(getString(R.string.edit_fragmnent_title),item.id)
        this.findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id=navigationArgs.itemId
        viewModel.retrieveItem(id).observe(this.viewLifecycleOwner){ selectItem->
            item=selectItem
            bind(item)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentItemDetailBinding.inflate(inflater,container,false)
        return binding.root
    }
    private fun showConfirmationDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Are you sure you want to delete?")
            .setCancelable(false)
            .setNegativeButton("No"){_,_->}
            .setPositiveButton("Yes"){_,_->
                deleteItem()
            }
            .show()
    }
    private fun deleteItem(){
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}