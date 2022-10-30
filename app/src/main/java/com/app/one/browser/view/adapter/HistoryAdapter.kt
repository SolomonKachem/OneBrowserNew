package com.app.one.browser.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.one.browser.database.History
import com.app.one.browser.databinding.ViewHistoryBinding
import com.app.one.browser.extension.dip2px
import com.app.one.browser.extension.formatDate
import com.app.one.browser.extension.loadUrl
import com.app.one.browser.extension.setOnRepeatClickListener
import com.app.one.browser.monitor.HistoryListener

class HistoryAdapter(var list:MutableList<History>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    var listener:HistoryListener? = null
    inner class HistoryViewHolder(val binding:ViewHistoryBinding):RecyclerView.ViewHolder(binding.root){
        fun bindView(history: History,isShow:Boolean,listener: HistoryListener?){
            binding.iconImage.loadUrl(history.icon,32f.dip2px())
            binding.tvContent.text = history.title
            binding.tvUrl.text = history.url
            if(isShow){
                binding.tvDate.text = history.time.formatDate("EEEE - MMMM dd,yyyy")
                binding.tvDate.visibility = View.VISIBLE
            }else{
                binding.tvDate.visibility = View.GONE
            }
            binding.iconClose.setOnRepeatClickListener {
                listener?.deleteHistory(history)
            }
            binding.view.setOnRepeatClickListener {
                listener?.browseHistory(history)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(ViewHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val isShow = if(position == 0) true else list[position-1].time.formatDate("yyyy-MM-dd") != list[position].time.formatDate("yyyy-MM-dd")
        holder.bindView(list[position],isShow,listener)
    }

    override fun getItemCount(): Int = list.size
}