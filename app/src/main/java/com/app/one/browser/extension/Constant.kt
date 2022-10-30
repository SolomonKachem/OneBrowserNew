package com.app.one.browser.extension

import android.text.format.DateUtils
import com.tencent.mmkv.MMKV
import android.util.Base64
import org.json.JSONObject

object Constant {
    private val mmkv: MMKV by lazy { MMKV.defaultMMKV() }
    const val common_db:String = "one_browser"
    const val table_history = "table_browsing_history"
    const val table_bookmark = "table_browsing_bookmark"
    const val admobConfig = "ewogICJsb2FkaW5nIjogWwogICAgewogICAgICAiaWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzEwMzMxNzM3MTIiLAogICAgICAidHlwZSI6ICJpbnRlcnN0aXRpYWwiLAogICAgICAicHJpb3JpdHkiOiAyCiAgICB9LAogICAgewogICAgICAiaWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzEwMzMxNzM3MTIiLAogICAgICAidHlwZSI6ICJpbnRlcnN0aXRpYWwiLAogICAgICAicHJpb3JpdHkiOiAxCiAgICB9CiAgXSwKICAiaG9tZV9uYXRpdmUiOiBbCiAgICB7CiAgICAgICJpZCI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICJ0eXBlIjogIm5hdGl2ZSIsCiAgICAgICJwcmlvcml0eSI6IDIKICAgIH0sCiAgICB7CiAgICAgICJpZCI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICJ0eXBlIjogIm5hdGl2ZSIsCiAgICAgICJwcmlvcml0eSI6IDEKICAgIH0KICBdCn0="

    var autoLoadImage:Boolean get() = mmkv.decodeBool("auto_load_image",true)
        set(value) {
            mmkv.encode("auto_load_image",value)
        }

    var searchEngine:String get() = mmkv.decodeString("search_engine","0").toString()
        set(value) {
            mmkv.encode("search_engine",value)
        }

    var lastUrl:String get() = mmkv.decodeString("last_url","").toString()
        set(value) {
            mmkv.encode("last_url",value)
        }
    var currentPage:Int get() = mmkv.decodeInt("current_page",0)
        set(value) {
            mmkv.encode("current_page",value)
        }

    var currentAdmob:String get() = mmkv.decodeString("current_admob",admobConfig).toString()
        set(value) {
            mmkv.encode("current_admob",value)
        }

    var admobCache:Long get() = mmkv.decodeLong("admob_cache",50*DateUtils.MINUTE_IN_MILLIS)
        set(value) {
            mmkv.encode("admob_cache",value)
        }
    var admobClick:Int get() = mmkv.decodeInt("admob_click",5)
        set(value) {
            mmkv.encode("admob_click",value)
        }
    var admobShow:Int get() = mmkv.decodeInt("admob_show",30)
        set(value) {
            mmkv.encode("admob_show",value)
        }

    private var admobDate:String get() = mmkv.decodeString("admob_date","").toString()
        set(value) {
            mmkv.encode("admob_date",value)
        }

    var currentAdmobClick:Int get() {
            initDate()
            return mmkv.decodeInt("current_admob_click",0)
        }set(value) {
            mmkv.encode("current_admob_click",value)
        }

    var currentAdmobShow:Int get() {
            initDate()
            return mmkv.decodeInt("current_admob_show",0)
        }set(value) {
            mmkv.encode("current_admob_show",value)
        }

    var admobClickEnable:Boolean get() = mmkv.decodeBool("admob_click_enable",false)
        set(value) {
            mmkv.encode("admob_click_enable",value)
        }
    private fun initDate(){
        val currentDay = getCurrentDate()
        if(admobDate != currentDay){
            mmkv.encode("admob_date",currentDay)
            mmkv.encode("current_admob_click",0)
            mmkv.encode("current_admob_show",0)
        }
    }

    /**
     * base64 to string
     */
    private fun base64ToString(string:String):String{
        return String(Base64.decode(string.toByteArray(), Base64.NO_WRAP))
    }
    fun base64StringToJson(string: String): JSONObject {
        val rewardJson = base64ToString(string)
        return try {
            JSONObject(rewardJson)
        } catch (e: Exception) {
            JSONObject()
        }
    }
}