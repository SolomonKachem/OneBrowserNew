package com.app.one.browser.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.one.browser.extension.Constant
import com.app.one.browser.view.activity.WebActivity

class MainViewModel: ViewModel() {
    val currentUrl = MutableLiveData<String>()

    fun searchUrl(url:String){
        currentUrl.postValue(url)
    }
    fun getCurrentImageUrl():String{
        Constant.currentPage += 1
        return "Image_${Constant.currentPage}.png"
    }
    fun startUrl(context: Context,url:String){
        context.startActivity(Intent(context,WebActivity::class.java).apply {
            putExtra("url",url)
        })
    }
}