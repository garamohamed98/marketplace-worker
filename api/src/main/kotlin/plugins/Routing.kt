package com.garamohamed.plugins

import com.garamohamed.features.listings.ListingRepositoryImpl
import com.garamohamed.features.listings.ListingService
import com.garamohamed.features.listings.listingRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val repository = ListingRepositoryImpl()
    val listingService = ListingService(repository)

    routing{
        listingRoutes(listingService)
    }
}