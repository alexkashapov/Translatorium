package com.fake.translatorium.main.view

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.fake.translatorium.R
import com.fake.translatorium.main.api.App
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: MyAdapter

    val compositeDisposable = CompositeDisposable()

    fun Disposable.bind(disposableContainer: DisposableContainer) = disposableContainer.add(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        val chooseLangOne = findViewById<Button>(R.id.lang1)
        val chooseLangTwo = findViewById<Button>(R.id.lang2)
        if (App.presenter.lang1 != "") chooseLangOne.text = App.presenter.lang1
        if (App.presenter.lang2 != "") chooseLangTwo.text = App.presenter.lang2
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        val ask: EditText = findViewById(R.id.ask)
        val answer: EditText = findViewById(R.id.answer)
        viewAdapter = MyAdapter()
        recyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        App.presenter.all().observeOn(AndroidSchedulers.mainThread()).subscribe({ viewAdapter.addAll(it) }, { it.printStackTrace() }).bind(compositeDisposable)


        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                App.presenter
                        .remove(viewAdapter.translates[viewHolder.adapterPosition])
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ viewAdapter.removeItem(viewHolder.adapterPosition) }, { it.printStackTrace() }).bind(compositeDisposable)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                viewAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder.setTitle("Choose language")

        chooseLangOne
                .clicks()
                .flatMapSingle { App.presenter.langsList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    val filter = list.filterNot { it == App.presenter.lang2 }
                    alertDialogBuilder
                            .setItems(filter.toTypedArray()) { dialog, which ->
                                chooseLangOne.text = filter[which]
                                App.presenter.lang1 = filter[which]
                            }
                            .show()
                }, { it.printStackTrace() }).bind(compositeDisposable)

        chooseLangTwo
                .clicks()
                .flatMapSingle { App.presenter.langsList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    val filter = list.filterNot { it == App.presenter.lang1 }
                    alertDialogBuilder
                            .setItems(filter.toTypedArray()) { dialog, which ->
                                chooseLangTwo.text = filter[which]
                                App.presenter.lang2 = filter[which]
                            }
                            .show()
                }, { it.printStackTrace() }).bind(compositeDisposable)
        val translateButton: Button = findViewById(R.id.translateButton)
        translateButton
                .clicks()
                .filter { ask.text.isNotEmpty() && App.presenter.isBothLangSelected() }
                .flatMapSingle { App.presenter.translate(ask.text.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewAdapter.addItem(it)
                    answer.setText(it.translated)
                }, { it.printStackTrace() }).bind(compositeDisposable)
        val swapButton = findViewById<ImageButton>(R.id.swap)
        swapButton
                .clicks()
                .flatMapSingle { App.presenter.swapLangs() }
                .subscribe {
                    chooseLangOne.text = App.presenter.lang1
                    chooseLangTwo.text = App.presenter.lang2
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.clear_all -> {
            App.presenter.clearAll().observeOn(AndroidSchedulers.mainThread()).subscribe { viewAdapter.deleteAll() }.bind(compositeDisposable)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
