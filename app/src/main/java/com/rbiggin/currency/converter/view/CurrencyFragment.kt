package com.rbiggin.currency.converter.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rbiggin.currency.converter.R
import com.rbiggin.currency.converter.presentation.CurrencyViewModel
import kotlinx.android.synthetic.main.fragment_currency.*
import org.koin.android.viewmodel.ext.android.viewModel

class CurrencyFragment : Fragment(R.layout.fragment_currency), CurrencyAdapterListener {

    private val viewModel: CurrencyViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(recyclerView) {
            adapter = CurrencyAdapter(
                activity as Activity,
                viewModel.conversionList,
                viewLifecycleOwner,
                this@CurrencyFragment
            )
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        viewModel.listUpdates.observe(viewLifecycleOwner, Observer {
            updateList(it)
        })
    }

    private fun updateList(update: CurrencyViewModel.UpdateType) {
        when (update) {
            is CurrencyViewModel.UpdateType.NewItems ->
                recyclerView.adapter?.notifyItemRangeInserted(
                    update.insertIndex,
                    update.numberOfItems
                )
            is CurrencyViewModel.UpdateType.NewTopItem ->
                recyclerView.apply {
                    adapter?.notifyItemMoved(update.fromIndex, 0)
                    scrollToPosition(0)
                }
            CurrencyViewModel.UpdateType.InitialUpdate ->
                recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onNewInputValue(input: Long) {
        viewModel.setInputValue(input)
    }

    override fun onItemClicked(index: Int) {
        if (!recyclerView.isAnimating) viewModel.onItemTouched(index)
    }
}
