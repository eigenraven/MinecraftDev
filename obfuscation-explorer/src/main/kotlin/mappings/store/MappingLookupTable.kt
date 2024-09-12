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

package io.mcdev.obfex.mappings.store

import io.mcdev.obfex.lookup.HashLookupTable
import io.mcdev.obfex.lookup.LookupIndexTransformer
import io.mcdev.obfex.lookup.MultiLookupTable
import io.mcdev.obfex.lookup.SingleLookupIndexTransformer
import io.mcdev.obfex.mappings.MappingElement
import io.mcdev.obfex.mappings.MappingNamespace
import io.mcdev.obfex.mappings.MappingSet
import kotlin.reflect.KClass

open class MappingLookupTable<T : MappingElement>(
    private val set: MappingSet,
    private val table: MultiLookupTable<T> = HashLookupTable()
) : MultiLookupTable<T> by table {

    // lookups based on namespace
    private val namespaceIndices = mutableListOf<NamespaceLookup<T>>()

    fun forNamespace(namespaceIndex: Int): MappingLookupIndex<T, String> {
        val lookup = namespaceIndices.firstOrNull { it.index == namespaceIndex }
        if (lookup != null) {
            return lookup.lookup
        }

        val newLookup = index { it.name(namespaceIndex) }
        namespaceIndices += NamespaceLookup(namespaceIndex, newLookup)
        return newLookup
    }

    fun forNamespace(namespace: MappingNamespace): MappingLookupIndex<T, String> {
        val index = set.namespaceIndex(namespace)
        return forNamespace(index)
    }

    val byName: MappingLookupIndex<T, String> by lazy {
        MappingLookupIndex(set, indexMulti { it.names.filterNotNull() })
    }

    override fun <K> index(transformer: SingleLookupIndexTransformer<T, K>): MappingLookupIndex<T, K> {
        return MappingLookupIndex(set, table.index(transformer))
    }

    override fun <K> indexMulti(transformer: LookupIndexTransformer<T, K>): MappingLookupIndex<T, K> {
        return MappingLookupIndex(set, table.indexMulti(transformer))
    }

    override fun <L : MultiLookupTable<*>> unwrap(type: KClass<L>): L? {
        if (type.isInstance(this)) {
            @Suppress("UNCHECKED_CAST")
            return this as L
        }
        return table.unwrap(type)
    }
}
