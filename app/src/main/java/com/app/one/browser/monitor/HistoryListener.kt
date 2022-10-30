package com.app.one.browser.monitor

import com.app.one.browser.database.History

interface HistoryListener {
    fun deleteHistory(history: History)
    fun browseHistory(history: History)
}