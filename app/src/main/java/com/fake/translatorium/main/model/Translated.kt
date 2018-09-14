package com.fake.translatorium.main.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "translations")
data class Translated(@PrimaryKey(autoGenerate = true)
                      val id: Int,
                      val text: String,
                      val translated: String,
                      val langdirsmall: String,
                      val langdir: String)