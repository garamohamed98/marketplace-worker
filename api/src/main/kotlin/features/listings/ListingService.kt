package com.garamohamed.features.listings

class ListingService {
    fun publishListing(request: ListingRequest): ListingResponse{
        val jobId = (1..1000).random()

        return ListingResponse(
            jobId = jobId,
            status = "QUEUED"
        )
    }
}