package com.garamohamed.plugins

import redis.clients.jedis.JedisPooled

object RedisFactory {
    lateinit var jedis: JedisPooled
        private set

    fun init() {
        jedis = JedisPooled("localhost", 6379)

        println("Redis connected")
    }
}