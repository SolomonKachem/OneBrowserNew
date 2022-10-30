package com.app.one.browser.view.activity

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.database.History
import com.app.one.browser.databinding.ActivityHistoryBinding
import com.app.one.browser.extension.setOnRepeatClickListener
import com.app.one.browser.monitor.HistoryListener
import com.app.one.browser.view.adapter.HistoryAdapter
import com.app.one.browser.viewmodel.HistoryViewModel

class HistoryActivity:BaseActivity<ActivityHistoryBinding>(ActivityHistoryBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }
    lateinit var historyAdapter: HistoryAdapter
    override fun onNewCreate() {
        binding.iconBack.setOnRepeatClickListener {
            finish()
        }
        viewModel.bookMarks.observe(this){list->
            historyAdapter = HistoryAdapter(list)
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = historyAdapter
            }
            historyAdapter.listener = object :HistoryListener{
                override fun deleteHistory(history: History) {
                    viewModel.deleteHistory(history)
                }

                override fun browseHistory(history: History) {
                    startActivity(Intent(context,WebActivity::class.java).apply {
                        putExtra("url",history.url)
                    })
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllHistory()
    }
}