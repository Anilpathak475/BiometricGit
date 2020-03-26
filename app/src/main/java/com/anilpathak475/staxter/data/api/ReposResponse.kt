package com.anilpathak475.staxter.data.api

import com.squareup.moshi.Json

data class ReposResponse(
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "description")
    val description: String?,
    @field:Json(name = "stargazers_count")
    val stars: Long
)