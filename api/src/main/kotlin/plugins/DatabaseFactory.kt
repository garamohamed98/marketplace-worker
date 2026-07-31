package com.garamohamed.plugins

import com.garamohamed.features.listings.database.ListingsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory{
    fun init(environment: ApplicationEnvironment) {

        val config = HikariConfig().apply {
            driverClassName = environment.config.property("database.driver").getString()
            jdbcUrl = environment.config.property("database.url").getString()
            username = environment.config.property("database.user").getString()
            password = environment.config.property("database.password").getString()

            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)

        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(ListingsTable)
        }

        println("Database connected")
    }
}