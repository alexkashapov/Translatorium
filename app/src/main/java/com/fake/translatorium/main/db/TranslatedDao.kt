package com.fake.translatorium.main.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.fake.translatorium.main.model.Translated

@Dao
interface TranslatedDao {
    @Query("select * from translations")
    fun getAll(): List<Translated>

    @Insert
    fun add(translated: Translated)

    @Delete
    fun delete(translated: Translated)

}