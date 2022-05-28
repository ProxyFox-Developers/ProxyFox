package io.github.proxyfox.webhook

import dev.kord.core.entity.Webhook

data class WebhookHolder(
    val id: ULong,
    val token: String?
)

fun Webhook.toHolder(): WebhookHolder = WebhookHolder(id.value, token)