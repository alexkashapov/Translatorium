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
import com.fake.translatorium.R
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: MyAdapter
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        val presenter = Presenter(applicationContext)

        val chooseLangOne = findViewById<Button>(R.id.lang1)
        val chooseLangTwo = findViewById<Button>(R.id.lang2)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val ask: EditText = findViewById(R.id.ask)
        val answer: EditText = findViewById(R.id.answer)
        viewAdapter = MyAdapter()
        recyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        presenter.all().observeOn(AndroidSchedulers.mainThread()).subscribe({viewAdapter.addAll(it)},{it.printStackTrace()})


        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                presenter
                        .remove(viewAdapter.translates[viewHolder.adapterPosition])
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ viewAdapter.removeItem(viewHolder.adapterPosition) }, { it.printStackTrace() })
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                viewAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder.setTitle("Choose lang")
        chooseLangOne
                .clicks()
                .flatMapSingle { presenter.langsList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    alertDialogBuilder
                            .setItems(it.toTypedArray()) { dialog, which ->
                                chooseLangOne.text = it[which]
                                presenter.lang1 = it[which]
                            }
                            .show()
                }, { it.printStackTrace() })
        chooseLangTwo
                .clicks()
                .flatMapSingle { presenter.langsList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    alertDialogBuilder
                            .setItems(it.toTypedArray()) { dialog, which ->
                                chooseLangTwo.text = it[which]
                                presenter.lang2 = it[which]
                            }
                            .show()
                }, { it.printStackTrace() })
        val translateButton: Button = findViewById(R.id.translateButton)
        translateButton
                .clicks()
                .flatMapSingle { presenter.translate(ask.text.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewAdapter.addItem(it)
                    answer.setText(it.translated)
                }, { it.printStackTrace() })
        val swapButton = findViewById<ImageButton>(R.id.swap)
        swapButton
                .clicks()
                .flatMapCompletable { presenter.swapLangs() }
                .subscribe {
                    chooseLangOne.text = presenter.lang1
                    chooseLangTwo.text = presenter.lang2
                }
    }
}
