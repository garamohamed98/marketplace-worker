package com.garamohamed.features.listings.messaging


import com.garamohamed.plugins.RabbitFactory
import kotlinx.serialization.json.Json
import com.rabbitmq.client.MessageProperties

object RabbitPublisher {

    private const val QUEUE_NAME = "listing-publish"

    fun publish(message: PublishListingMessage){

        val json = Json.encodeToString(message)

        RabbitFactory.channel.basicPublish(
            "",
            QUEUE_NAME,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            json.toByteArray()
        )
        println("Message sent to RabbitMQ: $json")
    }
}