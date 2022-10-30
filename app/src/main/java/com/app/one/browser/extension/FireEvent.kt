package com.app.one.browser.extension

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object FireEvent {
    val firebase = Firebase.analytics

    fun initRemote(){
        try {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                val admob = remoteConfig["admob_config"].asString()
                if(admob.isNotEmpty()){
                    val jsonObj = Constant.base64StringToJson(admob)
                    if(jsonObj.length()!=0){
                        Constant.currentAdmob = admob
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}