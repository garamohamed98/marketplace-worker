package com.garamohamed.features.listings

class ListingService(
    private val repository: ListingRepository
) {
    fun publishListing(request: ListingRequest): ListingResponse{
        val id = repository.save(request)

        return ListingResponse(
            jobId = id,
            status = "QUEUED"
        )
    }
}