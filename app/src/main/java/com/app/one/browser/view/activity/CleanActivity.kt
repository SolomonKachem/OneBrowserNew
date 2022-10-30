package com.app.one.browser.view.activity

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.databinding.ActivityCleanBinding
import com.app.one.browser.extension.AdManager
import com.app.one.browser.viewmodel.CleanViewModel

class CleanActivity:BaseActivity<ActivityCleanBinding>(ActivityCleanBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[CleanViewModel::class.java] }
    override fun onNewCreate() {
        viewModel.cleanRecode.observe(this){size->
            startActivity(Intent(context,ResultActivity::class.java).apply {
                putExtra("number",size)
            })
            finish()
        }
        viewModel.startClean()
        AdManager.loadAd(AdManager.nativeAdmob,context)
    }
}