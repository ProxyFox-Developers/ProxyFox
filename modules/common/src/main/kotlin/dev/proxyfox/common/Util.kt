/*
 * Copyright (c) 2022, The ProxyFox Group
 *
 * This Source Code is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.proxyfox.common

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.time.OffsetDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Important functions and variables needed for proxyfox
 * @author Oliver
 * */

val logger = LoggerFactory.getLogger("ProxyFox")

const val spacedDot = " \u2009• \u2009"
const val ellipsis = "…"

fun printFancy(input: String) {
    val edges = "*".repeat(input.length + 4)
    logger.info(edges)
    logger.info("* $input *")
    logger.info(edges)
}

fun printStep(input: String, step: Int) {
    val add = "  ".repeat(step)
    logger.info(step.toString() + add + input)
}

fun String?.toColor(): Int {
    return if (this == null || this == "") -1 else (toUIntOrNull(16)?.toInt() ?: Integer.decode(this)) and 0xFFFFFF
}

fun Int.fromColor() = fromColorForExport()?.let { "#$it" }

fun Int.fromColorForExport() = if (this < 0) null else toString(16).run { padStart(6, '0') }

inline fun String?.notBlank(action: (String) -> Unit) {
    if (!isNullOrBlank()) action(this)
}

fun String?.ifBlankThenNull(): String? = if (isNullOrBlank()) null else this

val OffsetDateTime.epochMilli
    get() = (toEpochSecond() * 1000L) + (nano / 1000000)

@OptIn(DelicateCoroutinesApi::class)
fun runAsync(action: suspend () -> Unit): Int {
    GlobalScope.launch {
        action()
    }
    return 0
}

@OptIn(ExperimentalContracts::class)
suspend inline fun <T> T.applyAsync(block: suspend T.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
    return this
}

//We just need a classloader to get a resource
val hash = object {}.javaClass.getResource("commit_hash.txt")?.readText(Charset.defaultCharset()) ?: "Unknown Hash"

class DebugException: Exception("Debug Exception - Do Not Report")