package com.rbiggin.currency.converter.view

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.rbiggin.currency.converter.R
import com.rbiggin.currency.converter.model.CurrencyModel
import kotlinx.android.synthetic.main.list_item_currency_view.view.*

class CurrencyAdapter(
    private val activity: Activity,
    private val list: List<LiveData<CurrencyModel>>,
    private val lifeCycleOwner: LifecycleOwner,
    private val listener: CurrencyAdapterListener
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(parent.inflate(R.layout.list_item_currency_view), activity, lifeCycleOwner, listener)

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        with(holder) {
            setLiveData(list[position])
            view.setOnClickListener { listener.onItemClicked(adapterPosition) }
        }
    }

    class CurrencyViewHolder(
        val view: View,
        private val activity: Activity,
        private val lifeCycleOwner: LifecycleOwner,
        private val listener: CurrencyAdapterListener
    ) : RecyclerView.ViewHolder(view) {
        private val textWatcher = GenericTextWatcher()

        fun setLiveData(data: LiveData<CurrencyModel>) {
            data.removeObservers(lifeCycleOwner)
            data.observe(lifeCycleOwner, Observer {
                setCurrencyCode(it.currencyCode)
                setValue(it.value, it.isTop)
                setCurrencyName(it.currencyName)
                setFlag(it.flagAssetUrl, activity)
                setAsTopView(it.isTop)
            })
        }

        private fun setAsTopView(isTop: Boolean) {
            with(view) {
                viewSwitcher.displayedChild = if (isTop) {
                    currencyValueEditText.addTextChangedListener(textWatcher)
                    viewSwitcher.indexOfChild(currencyValueEditText)
                } else {
                    currencyValueEditText.removeTextChangedListener(textWatcher)
                    viewSwitcher.indexOfChild(currencyValueTextView)
                }
            }
        }

        private fun setCurrencyCode(code: String) {
            view.currencyCode.text = code
        }

        private fun setCurrencyName(name: String?) {
            view.currencyName.text = name
        }

        private fun setValue(newValue: Long, isTop: Boolean) {
            view.currencyValueTextView.text = newValue.toString()
            if (!isTop || isTop && view.currencyValueEditText.text.toString().isBlank()) {
                view.currencyValueEditText.setText(newValue.toString())
            }
        }

        private fun setFlag(url: String?, activity: Activity) {
            url?.let {
                SvgLoader.pluck()
                    .with(activity)
                    .setPlaceHolder(R.drawable.ic_flag_black_24dp, R.drawable.ic_flag_black_24dp)
                    .load(it, view.currencyFlag)
            }
        }

        inner class GenericTextWatcher : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener.onNewInputValue(s?.toString()?.toLongOrNull() ?: 0)
            }
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}