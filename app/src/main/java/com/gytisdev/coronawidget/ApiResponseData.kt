package com.gytisdev.coronawidget

import com.google.gson.annotations.SerializedName

data class ApiResponseData(
    @SerializedName("provincestate")
    val provinceState : String,
    @SerializedName("countryregion")
    val countryRegion : String,
    @SerializedName("lastupdate")
    val lastUpdate: String,
    val location: Location,
    @SerializedName("countrycode")
    val countryCode: CountryCode,
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int
)

data class Location (
    val lat: Double,
    val lng: Double
)

data class CountryCode(
    val iso2: String,
    val iso3: String
)
