package com.garamohamed.plugins

import com.garamohamed.features.listings.listingModule
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing{
        listingModule()
    }
}