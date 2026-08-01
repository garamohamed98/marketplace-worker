package com.garamohamed.features.listings.repository

import com.garamohamed.plugins.RedisFactory

class RedisRepository {
    fun saveStatus(id: Int, status: String){
        RedisFactory.jedis.set(
            "listing:$id",
            status
        )
    }

    fun getStatus(id: Int):String?{
        return RedisFactory.jedis.get("listing:$id")
    }
}