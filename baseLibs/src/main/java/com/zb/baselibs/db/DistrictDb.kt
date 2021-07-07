package com.zb.baselibs.db

import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.DistrictInfo
import org.jetbrains.anko.db.*

class DistrictDb {
    //数据库操作
    private val database = DbHelper.getInstance(BaseApp.context)

    //初始化 调用createTable建表
    init {
        createTable()
    }

    //单例模式
    companion object {
        val instance: DistrictDb by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DistrictDb() }

        //表名
        private const val TABLE_NAME = "DistrictInfo"
        private const val UNIQUE_ID = "_id"
        private const val DISTRICT_ID = "districtId"
        private const val DISTRICT_NAME = "districtName"
        private const val CITY_ID = "cityId"
    }


    //建表
    private fun createTable() {
        // NULL，INTEGER，REAL，TEXT和BLOB
        database.use {
            //检查表是否存在 不存在则创建表
            createTable(
                TABLE_NAME, true,
                UNIQUE_ID to INTEGER + PRIMARY_KEY + UNIQUE,//主键 int型 唯一 递增
                DISTRICT_ID to INTEGER,
                DISTRICT_NAME to TEXT,
                CITY_ID to INTEGER
            )
        }
    }


    //增加数据
    fun insertData(data: DistrictInfo) {
        database.use {
            val districtInfo = querySingle(data.districtId)
            if (districtInfo == null)
                insert(
                    TABLE_NAME,
                    DISTRICT_ID to data.districtId,
                    DISTRICT_NAME to data.districtId,
                    CITY_ID to data.cityId
                )
        }
    }

    fun querySingle(id: Long): DistrictInfo? {
        var districtInfo: DistrictInfo? = null
        database.use {
            select(TABLE_NAME).whereSimple("districtId = ?", id.toString()).exec {
                if (moveToFirst()) {
                    val districtId = getLong(getColumnIndex(DISTRICT_ID))
                    val districtName = getString(getColumnIndex(DISTRICT_ID))
                    val cityId = getLong(getColumnIndex(CITY_ID))
                    districtInfo = DistrictInfo(districtId, districtName, cityId)
                }
            }
        }
        return districtInfo
    }

    fun queryDistrictId(districtName: String): Long? {
        var districtId = 0L
        database.use {
            select(TABLE_NAME).whereSimple("districtName = ?", districtName).exec {
                if (moveToFirst()) {
                    districtId = getLong(getColumnIndex(DISTRICT_ID))
                }
            }
        }
        return districtId
    }
}