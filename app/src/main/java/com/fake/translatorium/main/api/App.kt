package com.fake.translatorium.main.api

import android.app.Application
import com.fake.translatorium.main.Const
import com.fake.translatorium.main.Presenter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory


class App : Application() {
    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Const.baseUrl) //Базовая часть адреса
                .addConverterFactory(JacksonConverterFactory.create(ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
                .client(client)//Конвертер, необходимый для преобразования JSON'а в объекты
                .build()
        api = retrofit.create<YandexService>(YandexService::class.java) //Создаем объект, при помощи которого будем выполнять запросы
        presenter = Presenter(applicationContext)
    }

    companion object {
        lateinit var api: YandexService
            private set
        lateinit var presenter: Presenter
            private set
    }
}