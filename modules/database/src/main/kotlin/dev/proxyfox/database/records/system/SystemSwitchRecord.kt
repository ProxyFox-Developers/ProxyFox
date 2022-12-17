/*
 * Copyright (c) 2022, The ProxyFox Group
 *
 * This Source Code is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.proxyfox.database.records.system

import dev.proxyfox.database.*
import dev.proxyfox.database.records.MongoRecord
import dev.proxyfox.database.records.Record
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.Duration

// Created 2022-09-04T15:18:49

/**
 * A mutable record representing a switch
 *
 * @author Ampflower
 **/
@Serializable
open class SystemSwitchRecord : Record {
    var systemId: PkId
    var id: PkId
    var memberIds: List<PkId>

    var timestamp: Instant
        set(inst) {
            field = Instant.fromEpochSeconds(inst.epochSeconds)
        }

    constructor(systemId: PkId = "", id: PkId = "", memberIds: List<PkId> = ArrayList(), timestamp: Instant? = null) {
        this.systemId = systemId
        this.id = id
        this.memberIds = memberIds
        this.timestamp = timestamp ?: Clock.System.now()
    }

    override fun equals(other: Any?): Boolean {
        return this === other || other is SystemSwitchRecord
                && other.systemId == systemId
                && other.memberIds == memberIds
                && other.timestamp == timestamp
    }

    override fun hashCode(): Int {
        var result = systemId.hashCode()
        result = 31 * result + memberIds.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }

    override fun toString(): String {
        return "Switch{systemId=$systemId, memberIds=$memberIds, timestamp=$timestamp}"
    }

    override fun toMongo() = MongoSystemSwitchRecord(this)
}

@Serializable
class MongoSystemSwitchRecord : SystemSwitchRecord, MongoRecord {
    @Contextual
    override var _id: ObjectId = ObjectId()

    constructor(record: SystemSwitchRecord) {
        this.systemId = record.systemId
        this.id = record.id
        this.memberIds = record.memberIds
        this.timestamp = record.timestamp
    }

    override fun toMongo() = this
}
