package com.garamohamed.features.listings.repository

import com.garamohamed.features.listings.domain.Listing

interface ListingRepository {
    fun save(listing: Listing): Int
}