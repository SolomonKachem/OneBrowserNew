package com.app.one.browser.view.activity

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.database.BookMark
import com.app.one.browser.databinding.ActivityBookmarkBinding
import com.app.one.browser.extension.setOnRepeatClickListener
import com.app.one.browser.monitor.BookMarkListener
import com.app.one.browser.view.adapter.BookMarkAdapter
import com.app.one.browser.viewmodel.BookMarkViewModel

class BookMarkActivity:BaseActivity<ActivityBookmarkBinding>(ActivityBookmarkBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[BookMarkViewModel::class.java] }
    lateinit var bookmarkAdapter: BookMarkAdapter
    override fun onNewCreate() {
        binding.iconBack.setOnRepeatClickListener {
            finish()
        }
        viewModel.bookMarks.observe(this){list->
            bookmarkAdapter = BookMarkAdapter(list)
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = bookmarkAdapter
            }
            bookmarkAdapter.listener = object :BookMarkListener{
                override fun deleteBookMark(bookMark: BookMark) {
                    viewModel.deleteBookMark(bookMark)
                }

                override fun browseBookMark(bookMark: BookMark) {
                    startActivity(Intent(context,WebActivity::class.java).apply {
                        putExtra("url",bookMark.url)
                    })
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllBookMarks()
    }
}