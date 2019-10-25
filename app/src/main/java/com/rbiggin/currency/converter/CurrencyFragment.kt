package com.rbiggin.currency.converter

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.rbiggin.currency.converter.presentation.CurrencyConversionViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class CurrencyFragment : Fragment(R.layout.fragment_currency) {

    private val viewModel: CurrencyConversionViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.listUpdates.observe(viewLifecycleOwner, Observer {
            Log.i("TAG", "Update: ${viewModel.conversionList}")
        })
    }
}
