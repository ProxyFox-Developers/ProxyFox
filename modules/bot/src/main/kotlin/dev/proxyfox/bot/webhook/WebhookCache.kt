/*
 * Copyright (c) 2022, The ProxyFox Group
 *
 * This Source Code is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.proxyfox.bot.webhook

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import dev.kord.common.entity.Snowflake
import java.util.concurrent.TimeUnit

/**
 * A cache for webhooks
 * @author Oliver
 * */
object WebhookCache {
    val webhookCache: Cache<Snowflake, WebhookHolder> = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<Snowflake, WebhookHolder>()

    operator fun get(key: Snowflake): WebhookHolder? = webhookCache.getIfPresent(key)
    operator fun set(key: Snowflake, value: WebhookHolder) {
        webhookCache.put(key, value)
    }
}