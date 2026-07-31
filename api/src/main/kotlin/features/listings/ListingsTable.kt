package com.garamohamed.features.listings


import org.jetbrains.exposed.v1.core.Table

object ListingsTable : Table("listings") {
    val id = integer("id").autoIncrement()
    val title = varchar("title",255)
    val price = double("price")
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}