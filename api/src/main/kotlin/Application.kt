package com.garamohamed

import com.garamohamed.plugins.configureRouting
import com.garamohamed.plugins.configureSerialization
import com.garamohamed.plugins.DatabaseFactory
import com.garamohamed.plugins.configureStatusPages

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.module() {
    configureSerialization()
    configureStatusPages()
    DatabaseFactory.init(environment)
    configureRouting()
}