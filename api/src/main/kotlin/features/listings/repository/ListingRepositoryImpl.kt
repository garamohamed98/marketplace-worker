package com.garamohamed.features.listings.repository

import com.garamohamed.features.listings.database.ListingsTable
import com.garamohamed.features.listings.domain.Listing
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.NoSuchElementException
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll



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

    override fun getById(id: Int): Listing {
        return transaction {
            ListingsTable
                .selectAll()
                .where { ListingsTable.id eq id}
                .singleOrNull()
                ?.let{row ->
                    Listing(
                        id = row[ListingsTable.id],
                        title = row[ListingsTable.title],
                        price = row[ListingsTable.price],
                        description = row[ListingsTable.description]
                    )
                }
                ?:throw NoSuchElementException("Listing with id $id not found")
        }
    }
}