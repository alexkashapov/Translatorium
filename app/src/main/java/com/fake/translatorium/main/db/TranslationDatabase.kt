package com.fake.translatorium.main.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Translated::class), version = 1)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translatedDao(): TranslatedDao

    companion object {
        private var INSTANCE: TranslationDatabase? = null

        fun getInstance(context: Context): TranslationDatabase {
            if (INSTANCE == null) {
                synchronized(TranslationDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    TranslationDatabase::class.java, "translate.db").build()
                }
            }
            return INSTANCE!!
        }
    }
}