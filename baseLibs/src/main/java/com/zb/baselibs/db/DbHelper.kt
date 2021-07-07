package com.zb.baselibs.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.zb.baselibs.app.BaseApp
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper


class DbHelper private constructor(ctx: Context) :
    ManagedSQLiteOpenHelper(ctx, "${BaseApp.projectName}Db", null, 1) {
    init {
        instance = this
    }

    companion object {
        private var instance: DbHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: DbHelper(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 在这里创建表
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 在这里，您可以像往常一样升级表
    }
}

// Context的访问属性
val Context.database: DbHelper
    get() = DbHelper.getInstance(this)