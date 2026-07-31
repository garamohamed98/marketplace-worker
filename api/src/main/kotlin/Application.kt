package com.garamohamed

import com.garamohamed.plugins.configureRouting
import com.garamohamed.plugins.configureSerialization
import com.garamohamed.plugins.DatabaseFactory
import io.ktor.server.application.*

fun Application.module() {
    configureSerialization()
    DatabaseFactory.init(environment)
    configureRouting()
}