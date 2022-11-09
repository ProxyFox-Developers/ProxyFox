/*
 * Copyright (c) 2022, The ProxyFox Group
 *
 * This Source Code is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.proxyfox.api.models

import dev.proxyfox.common.fromColor
import dev.proxyfox.database.records.misc.AutoProxyMode
import dev.proxyfox.database.records.misc.TrustLevel
import dev.proxyfox.database.records.system.SystemRecord
import kotlinx.serialization.Serializable

@Serializable
data class System(
    val id: String,
    val name: String?,
    val description: String?,
    val tag: String?,
    val pronouns: String?,
    val color: String?,
    val avatarUrl: String?,
    val timezone: String?,
    val created: String,
    val autoProxy: String?,
    val autoType: AutoProxyMode,
    val trust: Map<ULong, TrustLevel>
) {
    companion object {
        fun fromRecord(system: SystemRecord) = System(
            id = system.id,
            name = system.name,
            description = system.description,
            tag = system.tag,
            pronouns = system.pronouns,
            color = system.color.fromColor(),
            avatarUrl = system.avatarUrl,
            timezone = system.timezone,
            created = system.timestamp.toString(),
            autoProxy = system.autoProxy,
            autoType = system.autoType,
            trust = system.trust
        )
    }
}