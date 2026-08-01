package com.garamohamed.features.listings

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import com.garamohamed.features.listings.dto.ListingResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListingRoutesTest {

    @Test
    fun `POST listings with valid data returns 200 and queued response`() = testApplication {
        configure()

        val response = client.post("/marketplace/vinted/listings") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Nike Air Max","price":120.0,"description":"Almost new"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = Json.decodeFromString<ListingResponse>(response.bodyAsText())
        assertEquals("QUEUED", body.status)
        assertTrue(body.jobId > 0)
    }

    @Test
    fun `POST listings with blank title returns 400`() = testApplication {
        configure()

        val response = client.post("/marketplace/vinted/listings") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"","price":120.0,"description":"Almost new"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST listings with zero price returns 400`() = testApplication {
        configure()

        val response = client.post("/marketplace/vinted/listings") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Nike Air Max","price":0,"description":"Almost new"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}