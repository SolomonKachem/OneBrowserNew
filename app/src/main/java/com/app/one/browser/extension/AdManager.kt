package com.app.one.browser.extension

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.app.one.browser.R
import com.app.one.browser.entity.Admob
import com.app.one.browser.monitor.ShowListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*
import kotlin.coroutines.resume

object AdManager {
    val loadingAdmob = Admob("loading")
    val nativeAdmob = Admob("home_native")

    private suspend fun loadInterstitialAd(context: Context, admobId:String) = suspendCancellableCoroutine<Any?> { callback->
        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(context,admobId, adRequest, object:InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                if(callback.isActive) callback.resume(interstitialAd)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                if(callback.isActive) callback.resume(null)
            }
        })
    }
    private suspend fun loadNativeAd(context: Context, admobId:String) = suspendCancellableCoroutine<Any?> { callback->

        val adBuilder = AdLoader.Builder(context,admobId)
            .forNativeAd { ad->
                if(callback.isActive) callback.resume(ad)
            }
            .withAdListener(object :AdListener(){
                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    if(callback.isActive) callback.resume(null)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder()
                .setRequestCustomMuteThisAd(true)
                .build())
            .build()
        adBuilder.loadAd(AdRequest.Builder().build())


    }
    private fun checkAdHasShowTime():Boolean{
        return Constant.admobShow >= Constant.currentAdmobShow && Constant.admobClick >= Constant.currentAdmobClick
    }
    fun loadAd(admob: Admob,context: Context): Job?{
        var job = admob.currentJob
        // 判断展示次数or点击次数是否达到设定限制
        val isLimited = checkAdHasShowTime()
        if (!isLimited) {
            "广告超过限制".showSystemLog()
            return null
        }
        // 如果在上次请求中，则不重新请求
        if (job?.isActive == true) {
            "上次请求未完成".showSystemLog()
            return job
        }
        // 移除过期广告
        val currentTime = getCurrentMill()
        if (currentTime > admob.cacheTime) {
            "广告缓存超时".showSystemLog()
            admob.currentAd = null
        }
        // 如果有缓存，则不重新请求
        if (admob.currentAd != null) {
            "当前存在缓存".showSystemLog()
            return null
        }
        val flow = admob.waterFull()
        if (flow.size == 0) {
            "当前广告瀑布流为空".showSystemLog()
            return null
        }
        job = CoroutineScope(Dispatchers.Main).launch {
            for(ids in flow){
                val currentId = ids["id"].toString()
                val currentType = ids["type"].toString()
                "$currentId 广告开始加载".showSystemLog()
                val adData = if(currentId.isEmpty()){
                    null
                }else{
                    when (currentType) {
                        "interstitial" -> loadInterstitialAd(context, currentId)
                        "native" -> loadNativeAd(context, currentId)
                        else -> null
                    }
                }
                if(adData != null){
                    "$currentId 广告开始加载成功".showSystemLog()
                    admob.currentAd = adData
                    admob.cacheTime = getCurrentMill() + Constant.admobCache
                    break
                }else{
                    "$currentId 广告开始加载失败".showSystemLog()
                }
            }
        }
        admob.currentJob = job
        return job
    }
    fun showInterstitialAd(activity: FragmentActivity,admob: Admob,listener: ShowListener){
        val currentAd = admob.currentAd
        if(currentAd != null && currentAd is InterstitialAd){
            admob.currentAd = null
            admob.currentJob = null
            currentAd.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {
                    Constant.currentAdmobClick += 1
                }

                override fun onAdDismissedFullScreenContent() {
                    listener.onClose()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    listener.onClose()
                }

                override fun onAdImpression() {

                }

                override fun onAdShowedFullScreenContent() {
                    Constant.currentAdmobShow += 1
                }
            }
            currentAd.show(activity)
        }else{
            listener.onClose()
        }
    }
    fun showNativeAd(parent: ViewGroup,admob: Admob){
        val currentAd = admob.currentAd
        if(currentAd != null && currentAd is NativeAd){
            admob.currentAd = null
            admob.currentJob = null
            val adView = LinearLayout.inflate(parent.context,R.layout.admob_native,null) as NativeAdView
            adView.mediaView = adView.findViewById(R.id.ad_media)
            val headlineView = adView.findViewById<TextView>(R.id.tv_content)
            val bodyView = adView.findViewById<TextView>(R.id.tv_title)
            adView.callToActionView = adView.findViewById(R.id.install_action)
            val iconView = adView.findViewById<ImageView>(R.id.icon_logo)
            headlineView.text = currentAd.headline
            currentAd.mediaContent?.let { mediaCount->
                adView.mediaView?.mediaContent = mediaCount
                adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                if(Constant.admobClickEnable) {
                    adView.mediaView?.isClickable = false
                    adView.mediaView?.isEnabled = false
                }
                val vc = currentAd.mediaContent?.videoController
                if (vc!=null && vc.hasVideoContent()) {
                    vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {}
                }
            }

            if (currentAd.body == null) {
                bodyView.visibility = View.INVISIBLE
            } else {
                bodyView.visibility = View.VISIBLE
                bodyView.text = currentAd.body
            }
            if (currentAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = currentAd.callToAction
            }
            if (currentAd.icon == null) {
                iconView.visibility = View.GONE
            } else {
                currentAd.icon?.drawable?.let {
                    iconView.setImageDrawable(it)
                }
                iconView.visibility = View.VISIBLE
            }
            adView.setNativeAd(currentAd)
            parent.removeAllViews()
            parent.addView(adView)
        }
    }
}