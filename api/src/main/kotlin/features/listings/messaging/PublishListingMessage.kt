package com.garamohamed.features.listings.messaging

import kotlinx.serialization.Serializable

@Serializable
data class PublishListingMessage(
    val listingId: Int
)
