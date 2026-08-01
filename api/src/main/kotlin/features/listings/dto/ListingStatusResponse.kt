package com.garamohamed.features.listings.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListingStatusResponse(
    val jobId: Int,
    val status: String
)