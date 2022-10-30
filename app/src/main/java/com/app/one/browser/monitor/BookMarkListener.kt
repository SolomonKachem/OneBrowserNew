package com.app.one.browser.monitor

import com.app.one.browser.database.BookMark

interface BookMarkListener {
    fun deleteBookMark(bookMark: BookMark)
    fun browseBookMark(bookMark: BookMark)
}