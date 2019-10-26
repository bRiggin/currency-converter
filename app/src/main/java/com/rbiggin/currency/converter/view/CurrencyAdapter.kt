package com.rbiggin.currency.converter.view

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.rbiggin.currency.converter.R
import com.rbiggin.currency.converter.model.CurrencyModel
import kotlinx.android.synthetic.main.list_item_currency_view.view.*

class CurrencyAdapter(
    private val activity: Activity,
    private val list: List<CurrencyModel>,
    private val inputListener: CurrencyAdapterListener? = null
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(parent.inflate(R.layout.list_item_currency_view))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val isTopItem = position == 0

        val listItem = list[position]
        with(holder) {
            listItem.currencyName?.let { holder.setCurrencyName(it) }
            listItem.flagAssetUrl?.let { holder.setFlag(it, activity) }
            setCurrencyCode(listItem.currencyCode)
            setValue(listItem.value, isTopItem)
            setEditTextEnabled(isTopItem)
            if (isTopItem) {
                listener = { inputValue ->
                    inputListener?.onNewInputValue(inputValue)
                }
            }
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

    class CurrencyViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val textWatcher = GenericTextWatcher()
        private var currencyValue: Int? = null
        var listener: ((Int) -> Unit)? = null

        fun setEditTextEnabled(enabled: Boolean) {
            view.currencyValueEditText.isEnabled = enabled
            val editText = view.currencyValueEditText

            if (enabled) {
                editText.addTextChangedListener(textWatcher)
            } else {
                editText.removeTextChangedListener(textWatcher)
            }
        }

        fun setCurrencyCode(code: String) {
            if (view.currencyCode.text != code)
                view.currencyCode.text = code
        }

        fun setCurrencyName(name: String) {
            view.currencyName.text = name
        }

        fun setValue(newValue: Int, isSubjectCode: Boolean) {
            if (isSubjectCode) {
                if (currencyValue == null) setValue(newValue)
            } else {
                setValue(newValue)
            }
        }

        private fun setValue(newValue: Int) {
            currencyValue = newValue
            view.currencyValueEditText.setText(newValue.toString())
        }


        fun setFlag(url: String, activity: Activity) {
            SvgLoader.pluck()
                .with(activity)
                .setPlaceHolder(R.drawable.ic_flag_black_24dp, R.drawable.ic_flag_black_24dp)
                .load(url, view.currencyFlag)
        }

        inner class GenericTextWatcher : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener?.invoke(s.toString().toInt())
            }
        }
    }
}