package com.app.one.browser

import android.app.Application
import com.app.one.browser.database.DBInit
import com.app.one.browser.extension.AppStatus
import com.app.one.browser.extension.FireEvent
import com.app.one.browser.extension.isUIProcess
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

class WebApp: Application() {
    companion object{
        lateinit var context : WebApp
    }
    override fun onCreate() {
        super.onCreate()
        context = this
        if(isUIProcess()){
            registerActivityLifecycleCallbacks(AppStatus)
            Firebase.initialize(this)
            MMKV.initialize(this)
            DBInit.init(this)
            MobileAds.initialize(this){}
            FireEvent.initRemote()
        }
    }
}