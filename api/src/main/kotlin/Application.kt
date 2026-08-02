package com.garamohamed

import com.garamohamed.plugins.*

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import com.garamohamed.features.listings.worker.ListingWorker

fun Application.module() {
    configureSerialization()
    configureStatusPages()
    DatabaseFactory.init(environment)
    RedisFactory.init()
    RabbitFactory.init()
    BrowserFactory.init()
    ListingWorker.start()
    configureRouting()
}