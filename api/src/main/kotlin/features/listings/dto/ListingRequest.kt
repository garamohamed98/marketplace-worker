package com.garamohamed.features.listings.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListingRequest (
    val title: String,
    val price: Double,
    val description: String
)


