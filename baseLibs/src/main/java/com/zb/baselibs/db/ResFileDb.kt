package com.zb.baselibs.db

import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ResFile
import org.jetbrains.anko.db.*

class ResFileDb {
    //数据库操作
    private val database = DbHelper.getInstance(BaseApp.context)

    //初始化 调用createTable建表
    init {
        createTable()
    }

    //单例模式
    companion object {
        val instance: ResFileDb by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ResFileDb() }

        //表名
        private const val TABLE_NAME = "ResFile"
        private const val UNIQUE_ID = "_id"
        private const val RES_LINK = "resLink"
        private const val FILE_PATH = "filePath"
    }


    //建表
    private fun createTable() {
        // NULL，INTEGER，REAL，TEXT和BLOB
        database.use {
            //检查表是否存在 不存在则创建表
            createTable(
                TABLE_NAME, true,
                UNIQUE_ID to INTEGER + PRIMARY_KEY + UNIQUE,//主键 int型 唯一 递增
                RES_LINK to TEXT,
                FILE_PATH to TEXT
            )
        }
    }


    //增加数据
    fun insertData(data: ResFile) {
        database.use {
            insert(
                TABLE_NAME,
                RES_LINK to data.resLink,//主键 int型 唯一 递增
                FILE_PATH to data.filePath
            )
        }
    }

    fun isExit(resLink: String): Boolean {
        var isExit = false
        database.use {
            select(TABLE_NAME).whereSimple("resLink = ?", resLink).exec {
                if (moveToFirst()) {
                    isExit = true
                }
            }
        }
        return isExit
    }

    fun querySingle(resLink: String): ResFile {
        val resFile = ResFile()
        database.use {
            select(TABLE_NAME).whereSimple("resLink = ?", resLink).exec {
                if (moveToFirst()) {
                    resFile.resLink = getString(getColumnIndex(RES_LINK))
                    resFile.filePath = getString(getColumnIndex(FILE_PATH))
                }
            }
        }
        return resFile
    }

    fun queryFilePath(resLink: String): String {
        var filePath: String = ""
        database.use {
            select(TABLE_NAME).whereSimple("resLink = ?", resLink).exec {
                if (moveToFirst()) {
                    filePath = getString(getColumnIndex(FILE_PATH))
                }
            }
        }
        return filePath
    }
}