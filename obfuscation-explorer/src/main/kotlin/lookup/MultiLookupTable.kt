/*
 * Minecraft Development for IntelliJ
 *
 * https://mcdev.io/
 *
 * Copyright (C) 2024 minecraft-dev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, version 3.0 only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.mcdev.obfex.lookup

import java.util.Collections
import kotlin.reflect.KClass

typealias SingleLookupIndexTransformer<T, K> = (T) -> K?
typealias LookupIndexTransformer<T, K> = (T) -> List<K>

/**
 * Data structure which uses [indices][LookupIndex] to improve query performance of multiple distinct lookups.
 */
interface MultiLookupTable<T> {

    /**
     * Get the backing store for this table. This list should not be modified, use [add] or [remove] methods on
     * this class instead.
     */
    fun seq(): Sequence<T>

    fun list(): List<T>

    fun firstOrNull(): T? {
        return seq().firstOrNull()
    }

    fun singleOrNull(): T? {
        return seq().singleOrNull()
    }

    /**
     * Returns `true` if this table is empty.
     */
    fun isEmpty(): Boolean = size == 0

    /**
     * Returns the number of elements in this table.
     */
    val size: Int

    /**
     * Create a new [index][LookupIndex] to allow for efficient queries to the data store. Lookup tables with more
     * indices have worse write performance, as the indices have to be rebuilt, so a balance is a good idea.
     */
    fun <K> indexMulti(transformer: LookupIndexTransformer<T, K>): LookupIndex<T, K>

    fun <K> index(transformer: SingleLookupIndexTransformer<T, K>): LookupIndex<T, K> = indexMulti {
        listOfNotNull(transformer(it))
    }

    /**
     * Add a new value to the table.
     */
    fun add(value: T)

    /**
     * Remove a value from the table.
     */
    fun remove(value: T)

    fun <L : MultiLookupTable<*>> unwrap(type: KClass<L>): L?

    companion object {
        private val emptyTable = HashLookupTable<Any>()

        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): MultiLookupTable<T> = emptyTable as MultiLookupTable<T>
    }
}

/**
 * Naive query, not using any optimization, simply look through the raw store for any matches for the given
 * predicate and return the result. For repeated queries consider using an [index][MultiLookupTable.index] to improve
 * performance.
 */
inline fun <T> MultiLookupTable<T>.query(predicate: (T) -> Boolean): List<T> =
    Collections.unmodifiableList(list().filter(predicate))
