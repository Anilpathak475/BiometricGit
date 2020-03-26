package com.anilpathak475.staxter.data.api

import com.squareup.moshi.Json

data class ReposListResponse(
    @field:Json(name = "total_count")
    val totalCount: Int,
    @field:Json(name = "incomplete_results")
    val incompleteResults: Boolean,
    @field:Json(name = "items")
    val items: List<ReposResponse>
)