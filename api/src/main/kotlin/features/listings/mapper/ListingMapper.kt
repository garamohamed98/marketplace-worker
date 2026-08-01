package com.garamohamed.features.listings.mapper

import com.garamohamed.features.listings.domain.Listing
import com.garamohamed.features.listings.dto.ListingRequest

fun ListingRequest.toDomain(): Listing =
    Listing(
        title = title,
        price = price,
        description = description
    )
