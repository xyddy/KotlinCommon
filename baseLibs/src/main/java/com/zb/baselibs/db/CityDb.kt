package com.zb.baselibs.db

import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.CityInfo
import org.jetbrains.anko.db.*

class CityDb {
    //数据库操作
    private val database = DbHelper.getInstance(BaseApp.context)

    //初始化 调用createTable建表
    init {
        createTable()
    }

    //单例模式
    companion object {
        val instance: CityDb by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CityDb() }

        //表名
        private const val TABLE_NAME = "CityInfo"
        private const val UNIQUE_ID = "_id"
        private const val CITY_ID = "cityId"
        private const val CITY_NAME = "cityName"
        private const val PROVINCE_ID = "provinceId"
    }


    //建表
    private fun createTable() {
        // NULL，INTEGER，REAL，TEXT和BLOB
        database.use {
            //检查表是否存在 不存在则创建表
            createTable(
                TABLE_NAME, true,
                UNIQUE_ID to INTEGER + PRIMARY_KEY + UNIQUE,//主键 int型 唯一 递增
                CITY_ID to INTEGER,
                CITY_NAME to TEXT,
                PROVINCE_ID to INTEGER
            )
        }
    }


    //增加数据
    fun insertData(data: CityInfo) {
        database.use {
            val cityInfo = querySingle(data.cityId)
            if (cityInfo == null)
                insert(
                    TABLE_NAME,
                    CITY_ID to data.cityId,
                    CITY_NAME to data.cityName,
                    PROVINCE_ID to data.provinceId
                )
        }
    }

    fun querySingle(id: Long): CityInfo? {
        var cityInfo: CityInfo? = null
        database.use {
            select(TABLE_NAME).whereSimple("cityId = ?", id.toString()).exec {
                if (moveToFirst()) {
                    val cityId = getLong(getColumnIndex(CITY_ID))
                    val cityName = getString(getColumnIndex(CITY_NAME))
                    val provinceId = getLong(getColumnIndex(PROVINCE_ID))
                    cityInfo = CityInfo(cityId, cityName, provinceId)
                }
            }
        }
        return cityInfo
    }

    fun queryCityId(cityName: String): Long {
        var cityId = 0L
        database.use {
            select(TABLE_NAME).whereSimple("cityName = ?", cityName).exec {
                if (moveToFirst()) {
                    cityId = getLong(getColumnIndex(CITY_ID))
                }
            }
        }
        return cityId
    }
}