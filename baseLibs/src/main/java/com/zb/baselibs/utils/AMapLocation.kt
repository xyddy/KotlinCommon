package com.zb.baselibs.utils

import android.content.Context
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.zb.baselibs.app.BaseApp

class AMapLocation(context: Context?) {
    //声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient = AMapLocationClient(context)

    //声明AMapLocationClientOption对象
    private var mLocationOption: AMapLocationClientOption = AMapLocationClientOption()

    fun start(callBack: CallBack?) {

        //设置定位回调监听
        mLocationClient.setLocationListener { location: AMapLocation? ->
            if (location != null) {
                if (location.errorCode == 0) {
                    val cityName = location.city
                    val provinceName = location.province
                    val districtName = location.district
                    val address = location.address
                    val longitude = location.longitude.toString()
                    val latitude = location.latitude.toString()

                    saveString("${BaseApp.projectName}_longitude", longitude)
                    saveString("${BaseApp.projectName}_latitude", latitude)
                    saveString("${BaseApp.projectName}_provinceName", provinceName)
                    saveString("${BaseApp.projectName}_cityName", cityName)
                    saveString("${BaseApp.projectName}_selectCityName", cityName)
                    saveString("${BaseApp.projectName}_districtName", districtName)
                    saveString("${BaseApp.projectName}_address", address)

                    callBack?.success()
                } else {
                    if (getString("${BaseApp.projectName}_longitude").isEmpty()) {
                        Toast.makeText(BaseApp.context, "定位失败，请检查定位是否开启或连接WIFI重新尝试", Toast.LENGTH_SHORT).show()
                    } else {
                        callBack?.success()
                    }
                }
                mLocationClient.stopLocation()
                mLocationClient.onDestroy()
            }
        }
        //启动定位
        mLocationClient.startLocation()
    }

    interface CallBack {
        fun success()
    }

    init {
        //初始化定位
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果
        mLocationOption.isOnceLocationLatest = true
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.isMockEnable = true
        //关闭缓存机制
        mLocationOption.isLocationCacheEnable = false
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption)
    }
}