package com.garamohamed.features.listings.service

import com.garamohamed.features.listings.domain.Listing
import com.garamohamed.features.listings.dto.ListingRequest
import com.garamohamed.features.listings.dto.ListingStatusResponse
import com.garamohamed.features.listings.mapper.toDomain
import com.garamohamed.features.listings.messaging.PublishListingMessage
import com.garamohamed.features.listings.messaging.RabbitPublisher
import com.garamohamed.features.listings.repository.ListingRepository
import com.garamohamed.features.listings.repository.RedisRepository
import java.util.NoSuchElementException

class ListingService(
    private val repository: ListingRepository,
    private val redisRepository: RedisRepository
) {

    fun publishListing(request: ListingRequest): ListingStatusResponse {

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

        redisRepository.saveStatus(
            id,
            "QUEUED"
        )

        RabbitPublisher.publish(
            PublishListingMessage(
                listingId = id
            )
        )

        return ListingStatusResponse(
            jobId = id,
            status = "QUEUED"
        )
    }

    fun getListingStatus(id :Int):ListingStatusResponse{
        val status = redisRepository.getStatus(id)
            ?: throw NoSuchElementException("Listing not found.")

        return ListingStatusResponse(
            jobId = id,
            status = status
        )
    }

    fun getListingById(id:Int):Listing{
         return repository.getById(id)
    }
}