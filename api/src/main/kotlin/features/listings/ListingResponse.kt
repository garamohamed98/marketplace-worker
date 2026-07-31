package com.garamohamed.features.listings

import kotlinx.serialization.Serializable

@Serializable
data class ListingResponse(
    val jobId: Int,
    val status: String
)