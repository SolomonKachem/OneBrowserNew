package com.app.one.browser.entity

import com.app.one.browser.extension.Constant
import kotlinx.coroutines.Job
import org.json.JSONArray
import org.json.JSONObject

data class Admob(var name:String){
    var currentAd:Any? = null
    var cacheTime:Long = 0
    var currentJob: Job? = null
    fun waterFull() : MutableList<MutableMap<String,Any>>{
        val list = mutableListOf<MutableMap<String,Any>>()
        val admobObject = Constant.base64StringToJson(Constant.currentAdmob)
        if(admobObject.length() !=0 && !admobObject.isNull(name)){
            when(admobObject.get(name)){
                is JSONArray->{
                    val jsonArray = admobObject.getJSONArray(name)
                    for(index in 0 until jsonArray.length()){
                        val objc = jsonArray.getJSONObject(index)
                        val map = mutableMapOf<String,Any>()
                        map["id"] = objc.getString("id")
                        map["type"] = objc.getString("type")
                        map["priority"] = objc.getString("priority")
                        list.add(map)
                    }
                    list.sortByDescending { it["priority"].toString().toInt() }
                }
                is JSONObject->{
                    val objc = admobObject.getJSONObject(name)
                    val map = mutableMapOf<String,Any>()
                    map["id"] = objc.getString("id")
                    map["type"] = objc.getString("type")
                    map["priority"] = objc.getString("priority")
                    list.add(map)
                }
            }
        }
        return list
    }
}
