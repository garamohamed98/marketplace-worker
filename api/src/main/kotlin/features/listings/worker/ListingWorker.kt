package com.garamohamed.features.listings.worker


import com.garamohamed.features.listings.messaging.PublishListingMessage
import com.garamohamed.features.listings.repository.ListingRepositoryImpl
import com.garamohamed.features.listings.repository.RedisRepository
import com.garamohamed.features.listings.service.ListingService
import com.garamohamed.plugins.BrowserFactory
import com.garamohamed.plugins.RabbitFactory
import kotlinx.serialization.json.Json
import com.rabbitmq.client.DeliverCallback

object ListingWorker {


    private const val QUEUE_NAME = "listing-publish"

    val listingRepository = ListingRepositoryImpl()
    val redisRepository = RedisRepository()

    val listingService = ListingService(
        listingRepository,
        redisRepository
    )


    fun start() {


        val deliverCallback = DeliverCallback { _, delivery ->


            try {


                val message = String(
                    delivery.body
                )


                val listingMessage =
                    Json.decodeFromString<PublishListingMessage>(
                        message
                    )


                println(
                    "Worker received listing ${listingMessage.listingId}"
                )

                val listing = listingService.getListingById(listingMessage.listingId)

                ListingPublisher.publish(listing)


                RabbitFactory.channel.basicAck(
                    delivery.envelope.deliveryTag,
                    false
                )


            } catch (exception: Exception) {


                println(
                    "Error processing message: ${exception.message}"
                )


                RabbitFactory.channel.basicNack(
                    delivery.envelope.deliveryTag,
                    false,
                    true
                )

            }

        }

        RabbitFactory.channel.basicConsume(
            QUEUE_NAME,
            false,
            deliverCallback
        ) { consumerTag ->

            println(
                "Consumer cancelled: $consumerTag"
            )
        }


        println(
            "Listing Worker started..."
        )
    }
}