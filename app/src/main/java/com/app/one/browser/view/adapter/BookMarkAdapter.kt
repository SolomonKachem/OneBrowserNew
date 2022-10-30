package com.app.one.browser.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.one.browser.database.BookMark
import com.app.one.browser.databinding.ViewBookmarkBinding
import com.app.one.browser.extension.dip2px
import com.app.one.browser.extension.loadUrl
import com.app.one.browser.extension.setOnRepeatClickListener
import com.app.one.browser.monitor.BookMarkListener

class BookMarkAdapter(var list:MutableList<BookMark>): RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder>() {
    var listener:BookMarkListener? = null
    inner class BookMarkViewHolder(val binding:ViewBookmarkBinding):RecyclerView.ViewHolder(binding.root){
        fun bindView(bookMark: BookMark,listener: BookMarkListener?){
            binding.iconImage.loadUrl(bookMark.icon,32f.dip2px())
            binding.tvContent.text = bookMark.title
            binding.tvUrl.text = bookMark.url

            binding.iconClose.setOnRepeatClickListener {
                listener?.deleteBookMark(bookMark)
            }
            binding.view.setOnRepeatClickListener {
                listener?.browseBookMark(bookMark)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMarkViewHolder {
        return BookMarkViewHolder(ViewBookmarkBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BookMarkViewHolder, position: Int) {
        holder.bindView(list[position],listener)
    }

    override fun getItemCount(): Int = list.size
}