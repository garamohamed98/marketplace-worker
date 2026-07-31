package com.garamohamed.features.listings.domain

data class Listing(
    val id: Int? = null,
    val title: String,
    val price: Double,
    val description: String
)
