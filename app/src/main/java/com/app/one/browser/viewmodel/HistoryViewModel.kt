package com.app.one.browser.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.one.browser.database.DBInit
import com.app.one.browser.database.History
import kotlinx.coroutines.launch

class HistoryViewModel: ViewModel() {
    val bookMarks = MutableLiveData<MutableList<History>>()
    fun getAllHistory() = viewModelScope.launch {
        val list = DBInit.db.dao.getAllHistory()
        bookMarks.postValue(list)
    }
    fun deleteHistory(history: History) = viewModelScope.launch{
        DBInit.db.dao.deleteHistory(history)
        getAllHistory()
    }
}