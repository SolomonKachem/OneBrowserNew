package com.app.one.browser.viewmodel

import android.animation.ValueAnimator
import androidx.core.animation.addListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.one.browser.entity.Admob
import com.app.one.browser.extension.AdManager
import kotlinx.coroutines.launch

class LaunchViewModel: ViewModel() {
    val showAd = MutableLiveData<Admob>()
    val progress = MutableLiveData<Int>()
    fun startAnimation(animator:ValueAnimator) = viewModelScope.launch {
        animator.apply {
            duration = 10000L
            addUpdateListener {
                val currentValue = it.animatedValue as Int
                progress.postValue(currentValue)
                if(currentValue>2000){
                    if(AdManager.loadingAdmob.currentAd != null || AdManager.loadingAdmob.currentJob?.isActive == false){
                        progress.postValue(10000)
                        animator.cancel()
                    }
                }
            }
            addListener(onEnd = {
                showAd.postValue(AdManager.loadingAdmob)
            })
            start()
        }
    }
}