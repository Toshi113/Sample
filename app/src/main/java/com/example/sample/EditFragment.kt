package com.example.sample

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import java.lang.Exception

// 引数をまとめたBundleインスタンスから実際の値を受け取るときのキー
private const val KEY_ID = "ID"
private const val KEY_TITLE = "TITLE"
private const val KEY_DETAIL = "DETAIL"
private const val KEY_ISNEW = "ISNEW"

class EditFragment : Fragment() ,View.OnClickListener{
    // フィールド
    // Viewたち
    private var m_editText_title: EditText? = null
    private var m_editText_detail: EditText? = null
    private var m_button_save: Button? = null

    // データベース関連
    private val dbName: String = "MemoDB"
    private val tableName: String = "MemoTable"
    private val dbVersion: Int = 1

    // 受け取った引数を入れとく変数
    private var Id: Int? = null
    private var Title: String? = null
    private var Detail: String? = null
    // 新規の場合はtrue,編集の場合はfalse
    private var IsNew: Boolean? = null

    // 引数用のBundle(後述)から値を受け取る。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Id = it.getInt(KEY_ID)
            Title = it.getString(KEY_TITLE)
            Detail = it.getString(KEY_DETAIL)
            IsNew = it.getBoolean(KEY_ISNEW)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // つまりfragmentが作られたときの処理。ActivityのonCreate()に相当するもの(だと思う)
        // フラグメントをインフレート(オブジェクトを作成)する
        val view: View = inflater.inflate(R.layout.fragment_edit, container, false)

        // KotlinExtention使えないからしかたがなくfindViewByIdでViewをいれとく
        // これらはフラグメントというViewの中(こんかいはviewという名前のインスタンス)に含まれているので「view.」をつける
        m_editText_title = view.findViewById(R.id.editText_tite)
        m_editText_detail  = view.findViewById(R.id.editText_detail)
        m_button_save = view.findViewById(R.id.button_save)
        m_button_save!!.setOnClickListener(this)
        m_editText_title!!.setText(Title)
        m_editText_detail!!.setText(Detail)
        // 作成したfragmentのオブジェクト(インスタンス)をreturn
        return view
    }
    // このFragmentを実際にインスタンス化するための処理。ここで引数をとってBundleにいれとく。そしてさっきのonCreate()で値をうけとっているのだ
    companion object {
        @JvmStatic
        fun newInstance(id: Int,title: String, detail: String, is_new: Boolean) =
                EditFragment().apply {
                    arguments = Bundle().apply {
                        putInt(KEY_ID, id)
                        putString(KEY_TITLE, title)
                        putString(KEY_DETAIL, detail)
                        putBoolean(KEY_ISNEW,is_new)
                    }
                }
    }

    // 普通にボタンたちのonClick処理。今回ボタン1個しか無いから特に分岐とかさせてない。
    override fun onClick(v: View?) {
        if(IsNew!!) {
            // isNewがtrue,つまり新規作成の場合
            // idとか全部MainFragmentからもらってるからアクセスできる。
            insertData(Id!!,m_editText_title!!.text.toString(),m_editText_detail!!.text.toString())
        }else{
            // isNewがfalse,つまり編集の場合
            updateData(Id!!,m_editText_title!!.text.toString(),m_editText_detail!!.text.toString())
        }
        fragmentManager!!.popBackStack()
        fragmentManager!!.beginTransaction().commit()
    }

    // 以下データベースをいじるメソッド
    // データを上書きする処理。上書き編集用
    private fun updateData(whereId: Int, newTitle: String, newDetail: String) {
        try{
            val dbHelper = MemoDatabaseOpenHelper(activity!!.applicationContext,dbName,null,dbVersion)
            val database = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("title",newTitle)
            values.put("detail",newDetail)

            val whereClauses = "id = ?"
            val whereArgs = arrayOf(whereId.toString())
            database.update(tableName, values, whereClauses, whereArgs)
        }catch(exception: Exception) {
            Log.e("updateData", exception.toString())
        }
    }

    // 新しくデータを追加する処理。新規メモ用
    private fun insertData(id: Int, title: String, detail: String) {
        try{
            val dbHelper = MemoDatabaseOpenHelper(activity!!.applicationContext,dbName,null,dbVersion)
            val database = dbHelper.writableDatabase

            val values = ContentValues()
            values.put("id",id)
            values.put("title",title)
            values.put("detail",detail)
            database.insertOrThrow(tableName,null,values)
        }catch (exception: Exception) {
            Log.e("insertData",exception.toString())
        }
    }
}