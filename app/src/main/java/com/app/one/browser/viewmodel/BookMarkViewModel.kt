package com.app.one.browser.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.one.browser.database.BookMark
import com.app.one.browser.database.DBInit
import com.app.one.browser.extension.showSystemLog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class BookMarkViewModel: ViewModel() {
    val bookMarks = MutableLiveData<MutableList<BookMark>>()
    fun getAllBookMarks() = viewModelScope.launch {
        val list = DBInit.db.dao.getAllBookMarks()
        bookMarks.postValue(list)
    }
    fun deleteBookMark(bookMark: BookMark) = viewModelScope.launch{
        DBInit.db.dao.deleteBookMark(bookMark)
        getAllBookMarks()
    }
    fun deleteCache(context:Context,path:String){
        try {
            val file = File(context.cacheDir,path)
            file.delete()
        } catch (e: java.lang.Exception) {
            e.message?.showSystemLog()
        }
    }
}