package com.zb.baselibs.db

import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ProvinceInfo
import org.jetbrains.anko.db.*

class ProvinceDb {
    //数据库操作
    private val database = DbHelper.getInstance(BaseApp.context)

    //初始化 调用createTable建表
    init {
        createTable()
    }

    //单例模式
    companion object {
        val instance: ProvinceDb by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ProvinceDb() }

        //表名
        private const val TABLE_NAME = "ProvinceInfo"
        private const val UNIQUE_ID = "_id"
        private const val PROVINCE_ID = "provinceId"
        private const val PROVINCE_NAME = "provinceName"
    }


    //建表
    private fun createTable() {
        // NULL，INTEGER，REAL，TEXT和BLOB
        database.use {
            //检查表是否存在 不存在则创建表
            createTable(
                TABLE_NAME, true,
                UNIQUE_ID to INTEGER + PRIMARY_KEY + UNIQUE,//主键 int型 唯一 递增
                PROVINCE_ID to INTEGER,
                PROVINCE_NAME to TEXT
            )
        }
    }


    //增加数据
    fun insertData(data: ProvinceInfo) {
        database.use {
            val provinceInfo = querySingle(data.provinceId)
            if (provinceInfo == null)
                insert(
                    TABLE_NAME, PROVINCE_ID to data.provinceId, PROVINCE_NAME to data.provinceName
                )
        }
    }

    /**
     * 是否有数据
     */
    fun isExist(): Boolean {
        var isExist = false
        database.use {
            select(TABLE_NAME).exec {
                isExist = this.count != 0
            }
        }
        return isExist
    }

    fun querySingle(id: Long): ProvinceInfo? {
        var provinceInfo: ProvinceInfo? = null
        database.use {
            select(TABLE_NAME).whereSimple("provinceId = ?", id.toString()).exec {
                if (moveToFirst()) {
                    val provinceId = getLong(getColumnIndex(PROVINCE_ID))
                    val provinceName = getString(getColumnIndex(PROVINCE_NAME))
                    provinceInfo = ProvinceInfo(provinceId, provinceName)
                }
            }
        }
        return provinceInfo
    }

    fun queryProvinceId(provinceName: String): Long? {
        var provinceId = 0L
        database.use {
            select(TABLE_NAME).whereSimple("provinceName = ?", provinceName).exec {
                if (moveToFirst()) {
                    provinceId = getLong(getColumnIndex(PROVINCE_ID))
                }
            }
        }
        return provinceId
    }
}