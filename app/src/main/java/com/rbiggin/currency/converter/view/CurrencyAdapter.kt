package com.rbiggin.currency.converter.view

import android.app.Activity
import android.text.InputType
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
    private val listener: CurrencyAdapterListener? = null
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(parent.inflate(R.layout.list_item_currency_view))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val subjectCode = list[0].currencyCode

        val listItem = list[position]
        with(holder) {
            setEditTextEnabled(position == 0)
            listItem.currencyName?.let { holder.setCurrencyName(it) }
            listItem.flagAssetUrl?.let { holder.setFlag(it, activity) }
            setCurrencyCode(listItem.currencyCode)
            setValue(listItem.value, listItem.currencyCode == subjectCode)
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

    class CurrencyViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        private var currencyValue: Int? = null

        fun setEditTextEnabled(enabled: Boolean) {
            view.currencyValueEditText.isEnabled = enabled
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
    }
}