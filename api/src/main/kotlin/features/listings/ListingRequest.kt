package com.garamohamed.features.listings

import kotlinx.serialization.Serializable

@Serializable
data class ListingRequest (
    val title: String,
    val price: Double,
    val description: String
)


