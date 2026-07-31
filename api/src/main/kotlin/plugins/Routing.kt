package com.garamohamed.plugins

import com.garamohamed.features.listings.ListingService
import com.garamohamed.features.listings.listingRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val listingService = ListingService()

    routing{
        listingRoutes(listingService)
    }
}