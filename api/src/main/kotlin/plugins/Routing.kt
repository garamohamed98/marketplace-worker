package com.garamohamed.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.garamohamed.features.listings.repository.ListingRepositoryImpl
import com.garamohamed.features.listings.service.ListingService
import com.garamohamed.features.listings.listingRoutes

fun Application.configureRouting() {

    val repository = ListingRepositoryImpl()
    val listingService = ListingService(repository)

    routing{
        listingRoutes(listingService)
    }
}