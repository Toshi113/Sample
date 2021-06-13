package com.example.sample


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Button
import java.lang.Exception


class MainFragment : Fragment(),RecyclerViewHolder.ItemClickListener, View.OnClickListener{
    // データベース関連のデータ
    private val dbName: String = "MemoDB"
    private val tableName: String = "MemoTable"
    private val dbVersion: Int = 1
    // データベースから複数のデータを取得した場合にいれとくところ。今考えたらMemoStructureのList1つで済む。けど僕は疲れててそれに気づかなかった()
    private var arrayListId: ArrayList<Int> = arrayListOf()
    private var arrayListTitle: ArrayList<String> = arrayListOf()
    private var arrayListDetail: ArrayList<String> = arrayListOf()
    // リサイクラービュー用
    private var m_recyclerView_main: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        val m_floatingActionButton_main: Button = view.findViewById(R.id.floatingActionButton_main)
        m_recyclerView_main = view.findViewById(R.id.recyclerView_main)
        m_floatingActionButton_main.setOnClickListener(this)
        initRecyclerView()
        return view
    }

    // このFragment特に引数ないからデフォルトのままでいい
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    // RecyclerViewをinitする。データベースからデータを取り直してリサイクラービューのアダプターに新しいデータ渡してる。
    private fun initRecyclerView(){
        selectAll()
        var data = mutableListOf<MemoStructure>()
        repeat(arrayListTitle.count()) {
            data.add(MemoStructure(arrayListId[it],arrayListTitle[it],arrayListDetail[it]))
        }
        m_recyclerView_main?.adapter = RecyclerAdapter(activity!!.applicationContext,this,data)
        m_recyclerView_main?.layoutManager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    // 以下データベース用
    // 行の番号からデータ取得。結局使わなかったけどせっかくだからそのまま残してる
    private fun selectData(row: Int) {
        try{
            arrayListId.clear()
            arrayListTitle.clear()
            arrayListDetail.clear()

            val databaseOpenHelper = MemoDatabaseOpenHelper(activity!!.applicationContext,dbName,null,dbVersion)
            val database = databaseOpenHelper.readableDatabase

            val sql = "select id, title, detail from $tableName where id = $row"
            // val sql = "select id, title, detail from " + tableName + " where id = " + row.toString() と同じ

            val cursor = database.rawQuery(sql,null)
            if(cursor.count > 0) {
                cursor.moveToFirst()
                while(!cursor.isAfterLast) {
                    arrayListId.add(cursor.getInt(0))
                    arrayListTitle.add(cursor.getString(1))
                    arrayListDetail.add(cursor.getString(2))
                    cursor.moveToNext()
                }
            }
        }catch(exception: Exception) {
            Log.e("selectData", exception.toString())
        }
    }

    // 全データ取得
    private fun selectAll() {
        try{
            arrayListTitle.clear()
            arrayListDetail.clear()

            val databaseOpenHelper = MemoDatabaseOpenHelper(activity!!.applicationContext,dbName,null,dbVersion)
            val database = databaseOpenHelper.readableDatabase

            val sql = "select id, title, detail from $tableName"

            val cursor = database.rawQuery(sql,null)
            if(cursor.count > 0) {
                cursor.moveToFirst()
                while(!cursor.isAfterLast) {
                    arrayListId.add(cursor.getInt(0))
                    arrayListTitle.add(cursor.getString(1))
                    arrayListDetail.add(cursor.getString(2))
                    cursor.moveToNext()
                }
            }
        }catch(exception: Exception) {
            Log.e("selectAll", exception.toString())
        }
    }

    // 一番最後のidを取得。新規作成するときにEditFragmentに新しいメモのidを伝えなくちゃいけないから必要。一番でかいidに+1すれば絶対重複しない。
    private fun getLastId(): Int? {
        try{
            arrayListId.clear()
            val databaseOpenHelper = MemoDatabaseOpenHelper(activity!!.applicationContext,dbName,null,dbVersion)
            val database = databaseOpenHelper.readableDatabase

            val sql = "select id from $tableName"
            val cursor = database.rawQuery(sql,null)
            if(cursor.count > 0) {
                cursor.moveToFirst()
                while(!cursor.isAfterLast) {
                    arrayListId.add(cursor.getInt(0))
                    cursor.moveToNext()
                }
            }
            return if(arrayListId.count() == 0) {
                0
            }else {
                arrayListId.max()
            }
        }catch (exception: Exception) {
            Log.e("getLastId", exception.toString())
            throw exception
        }
    }

    // 各RecyclerViewのアイテムが押されたときの処理。編集画面に移行
    override fun onItemClick(view: View, position: Int) {
        // タグ使って各Itemのスペースにid保存してあるからそこからid取得できる
        val idOfView = view.findViewById<Space>(R.id.id_container).getTag() as Int
        // タイトルとか内容は普通にtextViewのtextを取得しちゃえばいい
        val titleOfView = view.findViewById<TextView>(R.id.title_textView).text.toString()
        val detailOfView = view.findViewById<TextView>(R.id.detail_textView).text.toString()

        // EditFragmentに移行
        val editFragment = EditFragment.newInstance(idOfView,titleOfView,detailOfView,false)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.replace(R.id.fragment_container, editFragment)
        fragmentTransaction.commit()
    }

    // 新規ボタンが押された処理。新規の編集画面へ移行
    override fun onClick(v: View?) {
        val newID = getLastId()!! + 1
        val editFragment = EditFragment.newInstance(newID,"","",true)

        // EditFragmentに移行
        val fragmentTransaction =fragmentManager!!.beginTransaction()
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.replace(R.id.fragment_container, editFragment)
        fragmentTransaction.commit()
    }

