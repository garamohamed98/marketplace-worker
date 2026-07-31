package com.garamohamed.features.listings.repository

import com.garamohamed.features.listings.database.ListingsTable
import com.garamohamed.features.listings.domain.Listing
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ListingRepositoryImpl : ListingRepository {
    override fun save(listing: Listing): Int {
        return transaction {
            ListingsTable.insert {
                it[title] = listing.title
                it[price] = listing.price
                it[description] = listing.description
            } get ListingsTable.id
        }
    }
}