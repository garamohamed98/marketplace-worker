package com.garamohamed.features.listings

import com.garamohamed.features.listings.repository.ListingRepositoryImpl
import com.garamohamed.features.listings.repository.RedisRepository
import com.garamohamed.features.listings.service.ListingService
import io.ktor.server.routing.Route

fun Route.listingModule() {

    val listingRepository = ListingRepositoryImpl()
    val redisRepository = RedisRepository()

    val listingService = ListingService(
        listingRepository,
        redisRepository
    )

    listingRoutes(listingService)
}