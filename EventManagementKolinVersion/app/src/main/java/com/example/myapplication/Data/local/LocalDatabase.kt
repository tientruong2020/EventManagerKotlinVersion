package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.model.HistoricalSearchingText

@Database(entities = [HistoricalSearchingText::class], version = 1)
abstract class LocalDatabase:RoomDatabase() {

    abstract fun localDBDAO(): LocalDBDAO?

    companion object{

        @Volatile
        private var INSTANCE:LocalDatabase? =null

        fun getDatabase(context: Context):LocalDatabase?{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return  tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "local_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}