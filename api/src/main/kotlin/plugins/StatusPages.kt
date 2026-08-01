package com.garamohamed.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.util.*

fun Application.configureStatusPages(){
    install(StatusPages) {

        exception<NoSuchElementException> { call, cause ->

            call.respond(
                HttpStatusCode.NotFound,
                mapOf(
                    "error" to cause.message
                )
            )
        }

        exception<IllegalArgumentException> { call, cause ->

            call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "error" to cause.message
                )
            )
        }

        exception<Throwable> { call, cause ->

            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "error" to (cause.message ?: "Unknown error")
                )
            )
        }
    }
}