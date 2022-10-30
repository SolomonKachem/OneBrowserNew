package com.app.one.browser.view.dialog

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.databinding.DialogSettingBinding
import com.app.one.browser.extension.setOnRepeatClickListener
import com.app.one.browser.view.activity.BookMarkActivity
import com.app.one.browser.view.activity.HistoryActivity

class SettingDialog:BaseActivity<DialogSettingBinding>(DialogSettingBinding::inflate) {
    override fun onNewCreate() {
        setActivityDialog(Gravity.CENTER)
        binding.iconClose.setOnRepeatClickListener {
            setResult(Activity.RESULT_OK,Intent().apply {
                putExtra("type","close")
            })
            finish()
        }
        binding.tvAdd.setOnRepeatClickListener {
            setResult(Activity.RESULT_OK,Intent().apply {
                putExtra("type","add")
            })
            finish()
        }
        binding.tvBookmark.setOnRepeatClickListener {
            setResult(Activity.RESULT_OK,Intent().apply {
                putExtra("type","bookmark")
            })
            finish()
        }
        binding.tvHistory.setOnRepeatClickListener {
            setResult(Activity.RESULT_OK,Intent().apply {
                putExtra("type","history")
            })
            finish()
        }
    }
}