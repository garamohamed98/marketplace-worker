package com.garamohamed.plugins

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object RabbitFactory {
    private lateinit var connection: Connection

    lateinit var channel: Channel
        private set

    fun init(){
        val factory = ConnectionFactory()

        factory.host = "localhost"
        factory.port = 5672
        factory.username = "guest"
        factory.password = "guest"

        connection = factory.newConnection()

        channel = connection.createChannel()

        channel.queueDeclare(
            "listing-publish",
            true,
            false,
            false,
            null
        )

        println("RabbitMQ connected")
    }

    fun close(){
        channel.close()
        connection.close()
    }
}