package com.app.one.browser.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.one.browser.database.DBInit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CleanViewModel: ViewModel() {
    val cleanRecode = MutableLiveData<Int>()
    fun startClean() = viewModelScope.launch {
        val history = DBInit.db.dao.getAllHistory()
        DBInit.db.dao.deleteAllHistory()
        delay((3000L..5000L).random())
        cleanRecode.postValue(history.size)
    }
}