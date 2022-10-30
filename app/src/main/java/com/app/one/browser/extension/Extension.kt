package com.app.one.browser.extension

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.app.one.browser.R
import com.app.one.browser.WebApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


fun Context.isUIProcess():Boolean{
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val processInfo = am.runningAppProcesses
    val mainProcessName = packageName
    val myPid = Process.myPid()
    if(processInfo == null){
        return false
    }
    for (info in processInfo) {
        if (info.pid == myPid && mainProcessName == info.processName) {
            return true
        }
    }
    return false
}

fun String.isNotNull():Boolean{
    return if(this.trim().isEmpty()){
        false
    }else this.trim() != "null"
}
inline fun View.setOnRepeatClickListener(delayMillis: Long=3000L, crossinline onClick: () -> Unit) {
    this.setOnClickListener {
        this.isClickable = false
        onClick()
        this.postDelayed({
            this.isClickable = true
        }, delayMillis)
    }
}

fun getCurrentMill():Long{
    return System.currentTimeMillis()
}
fun getCurrentDate():String{
    val date = Date(getCurrentMill())
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    return simpleDateFormat.format(date)
}
fun Long.formatDate(format:String):String{
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat(format,Locale.ENGLISH)
    return simpleDateFormat.format(date)
}
fun Context.isDebugApk():Boolean{
    val info = applicationInfo
    return try {
        info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }catch (e:Exception){
        e.printStackTrace()
        false
    }
}
fun String.showSystemLog(){
    if(WebApp.context.isDebugApk()){
        this.showLogcat("system_log")
    }
}
fun String.showLogcat(tag:String){
    var msg = this
    val segmentSize = 3 * 1024
    val length: Int = msg.length
    if (length <= segmentSize) { // 长度小于等于限制直接打印
        Log.i(tag, msg)
    } else {
        while (msg.length > segmentSize) { // 循环分段打印日志
            val logContent: String = msg.substring(0, segmentSize)
            msg = msg.replace(logContent, "")
            Log.i(tag, logContent)
        }
        Log.i(tag, msg) // 打印剩余日志
    }
}
fun Context.showToast(msg:String){
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@showToast,msg,Toast.LENGTH_SHORT).show()
    }
}
fun ImageView.loadUrl(url:String,radius:Int = 0){
    if(radius>0){
        val roundedCorners = RoundedCorners(radius)
        val options = RequestOptions.bitmapTransform(roundedCorners)
        Glide.with(WebApp.context).load(url).error(R.drawable.icon_web_default).apply(options).into(this)
    }else{
        Glide.with(WebApp.context).load(url).error(R.drawable.icon_web_default).into(this)
    }
}
fun Float.dip2px():Int{
    val scale = WebApp.context.resources.displayMetrics.density
    return (this*scale+0.5f).toInt()
}
