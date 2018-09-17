package com.fake.translatorium.main.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class TranslatedDao {
    @Query("select * from translations")
    abstract fun all(): List<Translated>

    fun allSingle(): Single<List<Translated>> {
        return Single.fromCallable { all() }.subscribeOn(Schedulers.io())
    }

    @Insert
    abstract fun insert(translated: Translated)

    fun addSingle(translated: Translated): Single<Translated> {
        return Single.just(translated)
                .observeOn(Schedulers.io())
                .doOnSuccess(this::insert)
    }

    @Delete
    abstract fun delete(translated: Translated)

    fun deleteSingle(translated: Translated):Single<Translated>{
        return Single.just(translated)
                .observeOn(Schedulers.io())
                .doOnSuccess { delete(it) }
    }

    @Query("DELETE FROM translations")
    abstract fun clear()

    fun cleardata(): Completable {
        return Completable.fromCallable { clear() }.subscribeOn(Schedulers.io())
    }

}