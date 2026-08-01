package com.garamohamed.features.listings.service

import com.garamohamed.features.listings.dto.ListingRequest
import com.garamohamed.features.listings.repository.ListingRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ListingServiceTest {
    private val repository = mock<ListingRepository>()

    private val service = ListingService(repository)

    @Test
    fun `should publish listing successfully`(){
        //Arrange
        val request = ListingRequest(
            title = "Nike Air Max",
            price = 120.0,
            description = "Almost new"
        )

        whenever(repository.save(org.mockito.kotlin.any()))
            .thenReturn(1)

        //Act
        val response = service.publishListing(request)

        //Assert
        assertEquals(1, response.jobId)
        assertEquals("QUEUED", response.status)
    }

    @Test
    fun `should reject listing when title is blank`() {
        //Arrange
        val request = ListingRequest(
            title = "",
            price = 120.0,
            description = "Almost new"
        )

        //Act
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.publishListing(request)
        }

        //Assert
        assertEquals("Title is required.", exception.message)
    }

    @Test
    fun `should reject listing when price is zero`() {
        //Arrange
        val request = ListingRequest(
            title = "Nike Air Max",
            price = 0.0,
            description = "Almost new"
        )

        //Act
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.publishListing(request)
        }

        //Assert
        assertEquals("Price must be greater than 0.", exception.message)
    }

    @Test
    fun `should reject listing when price is negative`() {
        //Arrange
        val request = ListingRequest(
            title = "Nike Air Max",
            price = -10.0,
            description = "Almost new"
        )

        //Act
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.publishListing(request)
        }

        //Assert
        assertEquals("Price must be greater than 0.", exception.message)
    }

    @Test
    fun `should reject listing when description is blank`() {
        //Arrange
        val request = ListingRequest(
            title = "Nike Air Max",
            price = 120.0,
            description = ""
        )

        //Act
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.publishListing(request)
        }

        //Assert
        assertEquals("Description is required.", exception.message)
    }

}