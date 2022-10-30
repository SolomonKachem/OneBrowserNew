package com.app.one.browser.view.activity

import android.animation.ValueAnimator
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.app.one.browser.base.BaseActivity
import com.app.one.browser.databinding.ActivityLaunchBinding
import com.app.one.browser.extension.AdManager
import com.app.one.browser.extension.AppStatus
import com.app.one.browser.monitor.ShowListener
import com.app.one.browser.viewmodel.LaunchViewModel

class LaunchActivity: BaseActivity<ActivityLaunchBinding>(ActivityLaunchBinding::inflate) {
    private val viewModel by lazy { ViewModelProvider(this)[LaunchViewModel::class.java] }
    private val renew by lazy { intent.getBooleanExtra("renew",false) }
    private val animator: ValueAnimator = ValueAnimator.ofInt(0,10000)
    var isShow:Boolean = false
    override fun onNewCreate() {
        viewModel.progress.observe(this){progress->
            binding.progress.progress = progress
        }
        viewModel.showAd.observe(this){admob->
            isShow = true
            AdManager.showInterstitialAd(this, admob,object:ShowListener{
                override fun onClose() {
                    checkStartMain()
                    isShow = false
                }
            })
        }
        AdManager.loadAd(AdManager.loadingAdmob,this)
        AdManager.loadAd(AdManager.nativeAdmob,this)
    }
    fun checkStartMain(){
        AdManager.loadAd(AdManager.loadingAdmob,this)
        if(!AppStatus.isBackground()){
            if(!renew){
                startActivity(Intent(context, MainActivity::class.java))
            }
        }
        finish()
    }
    override fun onBackPressed() {

    }

    override fun onStop() {
        super.onStop()
        if(animator.isRunning || animator.isStarted){
            animator.pause()
        }
    }
    override fun onResume() {
        super.onResume()
        if(!isShow){
            viewModel.startAnimation(animator)
        }
    }
}