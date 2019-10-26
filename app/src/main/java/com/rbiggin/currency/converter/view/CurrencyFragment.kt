package com.rbiggin.currency.converter.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rbiggin.currency.converter.R
import com.rbiggin.currency.converter.presentation.CurrencyConversionViewModel
import kotlinx.android.synthetic.main.fragment_currency.*
import org.koin.android.viewmodel.ext.android.viewModel

class CurrencyFragment : Fragment(R.layout.fragment_currency), CurrencyAdapterListener {

    private val viewModel: CurrencyConversionViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = CurrencyAdapter(activity as Activity, viewModel.conversionList,
            { viewModel.inputValue = it },
            { viewModel.onItemTouched(it)})

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }

        viewModel.listUpdates.observe(viewLifecycleOwner, Observer {
            updateList(it)
            Log.i("TAG", "List: ${viewModel.conversionList}")
            Log.d("TAG", "Update: $it")
        })
    }

    private fun updateList(update: CurrencyConversionViewModel.UpdateType) {
        when (update) {
            is CurrencyConversionViewModel.UpdateType.ItemsUpdate -> {
                update.newItems?.let {
                    recyclerView.adapter?.notifyItemRangeInserted(it.insertIndex, it.numberOfItems)
                }
//                update.indexesChanged.forEach {
//                    recyclerView.adapter?.notifyItemChanged(it)
//                }
                if (update.indexesChanged.isNotEmpty()) {
                    recyclerView.post { recyclerView.adapter?.notifyDataSetChanged() }
                }
            }
            is CurrencyConversionViewModel.UpdateType.NewTopItem ->
                recyclerView.adapter?.notifyItemMoved(update.fromIndex, 0)
            CurrencyConversionViewModel.UpdateType.InitialUpdate ->
                recyclerView.adapter = CurrencyAdapter(activity as Activity, viewModel.conversionList,
                    { viewModel.inputValue = it },
                    { viewModel.onItemTouched(it)})
            CurrencyConversionViewModel.UpdateType.Pop ->
                recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onNewInputValue(input: Int) {
        viewModel.inputValue = input
    }

    override fun onItemClicked(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
