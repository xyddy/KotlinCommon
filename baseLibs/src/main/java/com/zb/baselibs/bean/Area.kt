package com.zb.baselibs.bean

data class ProvinceInfo(
    var provinceId: Long = 0,
    var provinceName: String = ""
)

data class CityInfo(
    var cityId: Long = 0,
    var cityName: String = "",
    var provinceId: Long = 0
)

data class DistrictInfo(
    var districtId: Long = 0,
    var districtName: String = "",
    var cityId: Long = 0
)