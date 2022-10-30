package com.app.one.browser.extension

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.app.one.browser.view.activity.LaunchActivity

object AppStatus: Application.ActivityLifecycleCallbacks {
    var activityNum = 0
    private var activityList = mutableListOf<Activity>()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if(!activityList.contains(activity)){
            activityList.add(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if(activityNum++ == 0){
            if(activity !is LaunchActivity && activity.localClassName.indexOf("view.activity")!=-1){
                activity.startActivity(Intent(activity,LaunchActivity::class.java).apply {
                    putExtra("renew",true)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if(--activityNum == 0){
            activityList.filter { it.localClassName.indexOf("view.") == -1 }.forEach {
                it.finish()
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if(activityList.contains(activity)){
            activityList.remove(activity)
        }
    }
    fun isBackground() :Boolean = activityNum == 0
}