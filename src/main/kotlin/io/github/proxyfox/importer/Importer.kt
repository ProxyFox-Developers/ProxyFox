package io.github.proxyfox.importer

import com.google.gson.Gson
import io.github.proxyfox.database.*
import java.io.InputStreamReader

val gson = Gson()

/**
 * Imports a system file from a [String]. Supports both PluralKit and TupperBox formats.
 *
 * @param string The JSON to import
 * @return The importer used
 *
 * @author Oliver
 * */
suspend fun import(string: String): Importer {
    val map = gson.fromJson(string, Map::class.java) as Map<String,*>
    val importer = if (map.containsKey("tuppers")) TupperBoxImporter() else PluralKitImporter()
    importer.import(map)
    return importer
}

/**
 * Imports a system file from an [InputStreamReader]. Supports both PluralKit and TupperBox formats.
 *
 * @param reader The JSON to import
 * @return The importer used
 *
 * @author Oliver
 * */
suspend fun import(reader: InputStreamReader): Importer {
    val map = gson.fromJson(reader, Map::class.java) as Map<String,*>
    val importer = if (map.containsKey("tuppers")) TupperBoxImporter() else PluralKitImporter()
    importer.import(map)
    return importer
}

/**
 * Interface to import a JSON file into the database
 *
 * @author Oliver
 * */
interface Importer {
    suspend fun import(map: Map<String,*>)
    suspend fun finalizeImport()

    // Getters:
    suspend fun getSystem(): SystemRecord
    suspend fun getMembers(): List<MemberRecord>
    suspend fun getMemberProxyTags(id: String): List<MemberProxyTagRecord>
}