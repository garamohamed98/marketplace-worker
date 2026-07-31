package com.garamohamed.features.listings

interface ListingRepository {
    fun save(request: ListingRequest): Int
}