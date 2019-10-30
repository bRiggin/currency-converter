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
            view.setOnClickListener {
                holder.copyValueToEditText()
                listener.onItemClicked(adapterPosition)
            }
        }
    }

    override fun onViewRecycled(holder: CurrencyViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    class CurrencyViewHolder(
        val view: View,
        private val activity: Activity,
        private val lifeCycleOwner: LifecycleOwner,
        private val listener: CurrencyAdapterListener
    ) : RecyclerView.ViewHolder(view) {

        private val textWatcher = GenericTextWatcher()
        private var liveData: LiveData<CurrencyModel>? = null

        private val observer: Observer<CurrencyModel> = Observer {
            setAsTopView(it.isTop)
            setCurrencyCode(it.currencyCode)
            setValue(it.value, it.isTop)
            setCurrencyName(it.currencyName)
            setFlag(it.flagAssetUrl, activity)
        }

        fun setLiveData(data: LiveData<CurrencyModel>) {
            resetLiveData()
            liveData = data
            data.observe(lifeCycleOwner, observer)
        }

        fun onRecycled() {
            resetLiveData()
        }

        fun copyValueToEditText() {
            view.currencyValueEditText.setText(view.currencyValueTextView.text.toString())
        }

        private fun setAsTopView(isTop: Boolean) {
            with(view) {
                if (isTop) {
                    editTextGroup.visibility = View.VISIBLE
                    currencyValueEditText.addTextChangedListener(textWatcher)
                    textViewGroup.visibility = View.GONE
                } else {
                    textViewGroup.visibility = View.VISIBLE
                    currencyValueEditText.removeTextChangedListener(textWatcher)
                    editTextGroup.visibility = View.GONE
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
            if (isTop && view.currencyValueEditText.text.isNullOrBlank()){
                copyValueToEditText()
            }
        }

        private fun setFlag(url: String?, activity: Activity) {

            url?.let {
                //Horrible hack to work around RESTCountries API limitation, see Read Me for more detail
                if (it == "https://restcountries.eu/data/deu.svg"){
                    view.currencyFlag.setImageResource(R.drawable.img_flag_eu)
                } else {
                    SvgLoader.pluck()
                        .with(activity)
                        .setPlaceHolder(R.drawable.ic_flag_grey_24dp, R.drawable.ic_flag_grey_24dp)
                        .load(it, view.currencyFlag)
                }
            } ?: run {
                view.currencyFlag.setImageResource(R.drawable.ic_flag_grey_24dp)
            }
        }

        private fun resetLiveData() {
            liveData?.removeObserver(observer)
            liveData = null
        }

        inner class GenericTextWatcher : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                editable?.let { handleTextChange(it) }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            private fun handleTextChange(editable: Editable) {
                if (editable.isEmpty()) {
                    listener.onNewInputValue(0)
                } else {
                    editable.toString().toLongOrNull()?.let {
                        listener.onNewInputValue(it)
                    }
                }
            }
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}