package com.garamohamed.features.listings.worker

import com.garamohamed.features.listings.domain.Listing
import com.garamohamed.plugins.BrowserFactory

object ListingPublisher {
    fun publish(listing:Listing){
        val page = BrowserFactory.browser.newPage()
        try {
            page.navigate("http://localhost:5173")

            page.locator("#title")
                .fill(listing.title)

            page.locator("#price")
                .fill(listing.price.toString())

            page.locator("#description")
                .fill(listing.description)

            page.locator("button[type='submit']")
                .click()

            page.locator("#title").fill(listing.title)
        } finally {
            page.close()
        }
    }
}