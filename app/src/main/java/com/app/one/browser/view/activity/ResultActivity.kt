package com.app.one.browser.view.activity

import com.app.one.browser.R
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.databinding.ActivityResultBinding
import com.app.one.browser.extension.AdManager
import com.app.one.browser.monitor.ShowListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResultActivity:BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {
    private val number by lazy { intent.getIntExtra("number",0) }
    override fun onNewCreate() {
        AdManager.showInterstitialAd(this,AdManager.loadingAdmob,object:ShowListener{
            override fun onClose() {
                AdManager.loadAd(AdManager.loadingAdmob,context)
            }
        })
        binding.tvResult.text = getString(R.string.result_clean, number.toString())
        AdManager.loadAd(AdManager.nativeAdmob,this)
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            delay(230)
            AdManager.nativeAdmob.currentJob?.join()
            CoroutineScope(Dispatchers.Main).launch {
                AdManager.showNativeAd(binding.fragmentAd,AdManager.nativeAdmob)
            }
        }
    }
}