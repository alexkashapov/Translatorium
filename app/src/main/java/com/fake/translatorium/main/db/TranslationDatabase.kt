package com.fake.translatorium.main.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.fake.translatorium.main.model.Translated

@Database(entities = arrayOf(Translated::class),version = 1)
    abstract class TranslationDatabase : RoomDatabase() {
        abstract fun translatedDao(): TranslatedDao
    }