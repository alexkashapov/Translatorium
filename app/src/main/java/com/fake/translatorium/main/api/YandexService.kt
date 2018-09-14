package com.fake.translatorium.main.api

import com.fake.translatorium.main.model.Langs
import com.fake.translatorium.main.model.Translate
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface YandexService {
    @POST("getLangs")
    fun listLangs(@Query("ui")lang: String,@Query("key")key:String): Call<Langs>

    @POST("translate")
    fun translate(@Query("key")key: String ,
                  @Query("lang")lang: String,
                  @Query("text") text: String,
                  @Query("options") options:String,
                  @Query("format")format:String):Call<Translate>
}