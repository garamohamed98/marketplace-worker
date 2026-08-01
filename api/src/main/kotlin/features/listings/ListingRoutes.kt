package com.garamohamed.features.listings

import com.garamohamed.features.listings.dto.ListingRequest
import com.garamohamed.features.listings.service.ListingService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.NoSuchElementException

fun Route.listingRoutes(
    service: ListingService
){
    post("/marketplace/listings"){
        val request = call.receive<ListingRequest>()
        val response = service.publishListing(request)

        call.respond(response)
    }

    get("/marketplace/listings/{id}/status"){
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid listing id.")

        val response = service.getListingStatus(id)

        call.respond(response)

    }
}