package com.fake.translatorium.main

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.fake.translatorium.R
import com.fake.translatorium.main.api.App
import com.fake.translatorium.main.model.Langs
import com.fake.translatorium.main.model.Translate
import com.fake.translatorium.main.model.Translated
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        val langMap = TreeMap<String, String>()
        val chooseLangOne = findViewById<Button>(R.id.lang1)
        val chooseLangTwo = findViewById<Button>(R.id.lang2)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val ask: EditText = findViewById(R.id.ask)
        val answer: EditText = findViewById(R.id.answer)
        viewAdapter = MyAdapter()
        recyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as MyAdapter
                adapter.removeItem(viewHolder.adapterPosition)
            }

            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                val adapter = recyclerView!!.adapter as MyAdapter
                adapter.swapItems(viewHolder!!.adapterPosition, target!!.adapterPosition)
                return true
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        var langsmap: HashMap<String, String>
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder.setTitle("Choose lang")
        App.api.listLangs(lang = Const.lang, key = Const.apiKey)
                .enqueue(object : Callback<Langs> {
                    override fun onFailure(call: Call<Langs>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<Langs>, response: Response<Langs>) {
                        langsmap = response.body()!!.langs
                        for ((key, value) in langsmap) {
                            langMap.put(value, key)
                        }
                        val langsList = (langMap.keys).toTypedArray()
                        chooseLangOne.setOnClickListener {
                            alertDialogBuilder.setItems(langsList) { dialog, which ->
                                chooseLangOne.text = langsList[which]
                            }.show()
                        }
                        chooseLangTwo.setOnClickListener {
                            alertDialogBuilder.setItems(langsList) { dialog, which ->
                                chooseLangTwo.text = langsList[which]
                            }.show()
                        }
                    }
                })
        val translateButton: Button = findViewById(R.id.translateButton)
        translateButton.setOnClickListener {
            val lang = "${langMap.get(chooseLangOne.text)}-${langMap.get(chooseLangTwo.text)}"
            val langdir = "${chooseLangOne.text} - ${chooseLangTwo.text}"
            App.api.translate(key = Const.apiKey, lang = lang, text = ask.text.toString(), options = "1", format = "plain")
                    .enqueue(object : Callback<Translate> {
                        override fun onFailure(call: Call<Translate>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "An error occurred during networking", Toast.LENGTH_SHORT).show()
                            t.printStackTrace()

                        }

                        override fun onResponse(call: Call<Translate>, response: Response<Translate>) {
                            val text = response.body()?.text!!.first()
                            answer.setText(text)
                            val translated = Translated(text = ask.text.toString(), langdir = langdir, langdirsmall = lang, translated = text)
                            (viewAdapter as MyAdapter).addItem(translated)
//                    answer.setText(translated.toString())
                        }

                    })
        }
        val swapButton = findViewById<ImageButton>(R.id.swap)
        swapButton.setOnClickListener {
            val changed = chooseLangOne.text;
            chooseLangOne.setText(chooseLangTwo.text)
            chooseLangTwo.setText(changed)
        }
    }
}
