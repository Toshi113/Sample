package com.example.sample

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 普通にOpenHelper。SQLはググったものを今回のケースに合うように適当に改造すればできる。
// 高1の情報集中講座でAidemyコース取る人は(60期とカリキュラム同じか知らんけど)そこでSQLを勉強しておくと便利。
class MemoDatabaseOpenHelper(context: Context, databaseName:String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version)  {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table if not exists MemoTable (id integer primary key, title text, detail text)");

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("alter table MemoTable add column deleteFlag integer default 0")
        }
    }
}