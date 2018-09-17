package com.fake.translatorium.main.api

import com.fake.translatorium.main.jsondata.Langs
import com.fake.translatorium.main.jsondata.Translate
import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.Query

interface YandexService {
    @POST("getLangs")
    fun listLangs(@Query("ui")lang: String,@Query("key")key:String): Single<Langs>

    @POST("translate")
    fun translate(@Query("key")key: String ,
                  @Query("lang")lang: String,
                  @Query("text") text: String,
                  @Query("options") options:String,
                  @Query("format")format:String): Single<Translate>
}