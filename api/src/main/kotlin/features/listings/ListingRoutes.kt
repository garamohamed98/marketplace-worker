package com.garamohamed.features.listings

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listingRoutes(
    service: ListingService
){
    post("/marketplace/vinted/listings"){
        val request = call.receive<ListingRequest>()
        val response = service.publishListing(request)

        call.respond(response)
    }
}