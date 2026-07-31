package com.garamohamed.features.listings.service

import com.garamohamed.features.listings.domain.Listing
import com.garamohamed.features.listings.dto.ListingRequest
import com.garamohamed.features.listings.dto.ListingResponse
import com.garamohamed.features.listings.mapper.toDomain
import com.garamohamed.features.listings.repository.ListingRepository

class ListingService(
    private val repository: ListingRepository
) {
    fun publishListing(request: ListingRequest): ListingResponse {

        require(request.title.isNotBlank()) {
            "Title is required."
        }

        require(request.price > 0) {
            "Price must be greater than 0."
        }

        require(request.description.isNotBlank()) {
            "Description is required."
        }

        val listing: Listing = request.toDomain()
        val id = repository.save(listing)

        return ListingResponse(
            jobId = id,
            status = "QUEUED"
        )
    }
}