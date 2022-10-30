package com.app.one.browser.database

import androidx.room.*
import com.app.one.browser.extension.Constant

@Entity(tableName = Constant.table_history)
data class History(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var url:String = "",
    var icon:String = "",
    var title:String = "",
    var image:String = "",
    var subscription:String = "",
    var time: Long = 0){
    constructor(url: String,icon: String,title: String,image: String,subscription: String):this(){
        time = System.currentTimeMillis()
        this.url = url
        this.icon = icon
        this.title = title
        this.image = image
        this.subscription = subscription
    }
}
@Entity(tableName = Constant.table_bookmark)
data class BookMark(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var url:String = "",
    var icon:String = "",
    var title:String = "",
    var image:String = "",
    var subscription:String = "",
    var time: Long = 0){
    constructor(url: String,icon: String,title: String,image: String,subscription: String):this(){
        time = System.currentTimeMillis()
        this.url = url
        this.icon = icon
        this.title = title
        this.image = image
        this.subscription = subscription
    }
}

@Dao
interface BrowserDao{
    @Query("select * from ${Constant.table_history} order by time desc")
    suspend fun getAllHistory():MutableList<History>

    @Query("delete from ${Constant.table_history}")
    suspend fun deleteAllHistory()

    @Delete(entity = History::class)
    suspend fun deleteHistory(data: History)

    @Insert
    suspend fun insertHistory(vararg data: History):MutableList<Long>

    @Query("select * from ${Constant.table_bookmark} order by time desc")
    suspend fun getAllBookMarks():MutableList<BookMark>

    @Delete(entity = BookMark::class)
    suspend fun deleteBookMark(data: BookMark)

    @Insert
    suspend fun insertBookMark(vararg data: BookMark):MutableList<Long>
}

@Database(entities = [History::class,BookMark::class], exportSchema = false, version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dbDao(): BrowserDao
    val dao: BrowserDao by lazy { dbDao() }
}



