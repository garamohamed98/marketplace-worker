package com.garamohamed.features.listings

import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ListingRepositoryImpl : ListingRepository {
    override fun save(request: ListingRequest): Int {
        return transaction {
            ListingsTable.insert {
                it[title] = request.title
                it[price] = request.price
                it[description] = request.description
            } get ListingsTable.id
        }
    }
}