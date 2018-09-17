package com.fake.translatorium.main

import android.content.Context
import com.fake.translatorium.main.api.App
import com.fake.translatorium.main.db.Translated
import com.fake.translatorium.main.db.TranslationDatabase
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

class Presenter(context: Context) {
    var lang1: String = ""
    var lang2: String = ""
    lateinit var langdir: String
    var langMap: TreeMap<String, String> = TreeMap()
    val dao by lazy { TranslationDatabase.getInstance(context).translatedDao() }

    fun all(): Single<List<Translated>> {
        return dao.allSingle()
    }

    fun translate(text: String): Single<Translated> {
        val lang = "${langMap.get(lang1)}-${langMap.get(lang2)}"
        langdir = "$lang1 - $lang2"
        return App.api
                .translate(key = Const.apiKey, lang = lang, text = text, options = "1", format = "plain")
                .subscribeOn(Schedulers.io())
                .map {
                    Translated(
                            id = 0,
                            text = text,
                            langdir = langdir,
                            langdirsmall = lang,
                            translated = it.text.first())
                }
                .flatMap { dao.addSingle(it) }
    }

    fun swapLangs(): Single<Unit> {
        return Single.fromCallable {
            if (lang1 != "" && lang2 != "") {
                lang1 = lang2.also { lang2 = lang1 }
            }
        }
    }

    fun remove(tr: Translated): Single<Translated> {
        return dao.deleteSingle(tr)
    }

    fun langsList(): Single<List<String>> {
        if (langMap.isEmpty()) {
            return App.api
                    .listLangs(lang = Const.lang, key = Const.apiKey)
                    .subscribeOn(Schedulers.io())
                    .map {
                        for ((key, value) in it.langs) {
                            langMap.put(value, key)
                        }
                        langMap.keys.toList()
                    }
        } else return Single.fromCallable { langMap.keys.toList() }
    }

    fun clearAll(): Completable {
        return dao.cleardata()
    }
}