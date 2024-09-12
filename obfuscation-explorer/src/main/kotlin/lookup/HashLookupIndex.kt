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

import it.unimi.dsi.fastutil.objects.Object2ObjectFunction
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass

class HashLookupIndex<T, K>(
    private val table: HashLookupTable<T>,
    private val transformer: LookupIndexTransformer<T, K>,
) : LookupIndex<T, K> {

    private var map: Object2ObjectOpenHashMap<K?, HashLookupTable<T>>? = null

    override fun query(key: K?): MultiLookupTable<T> {
        return map?.get(key) ?: MultiLookupTable.empty()
    }

    @Suppress("unused") // indirectly accessed by HashLookupTable
    private fun rebuild() {
        @Suppress("UNCHECKED_CAST")
        val store = tableList.get(table.unwrap(HashLookupTable::class)) as ObjectLinkedOpenHashSet<T>

        map = Object2ObjectOpenHashMap(store.size)

        for (t in store) {
            add(t)
        }
    }

    private fun add(value: T) {
        val keys = transformer(value)

        for (key in keys) {
            map?.computeIfAbsent(key, Object2ObjectFunction { HashLookupTable() })?.add(value)
        }
    }

    @Suppress("unused") // indirectly accessed by HashLookupTable
    private fun remove(value: T) {
        val keys = transformer(value)

        for (key in keys) {
            val list = map?.get(key)
            list?.remove(value)

            if (list?.isEmpty() == true) {
                map?.remove(key)
            }
        }
    }

    override fun <L : LookupIndex<*, *>> unwrap(type: KClass<L>): L? {
        if (type.isInstance(this)) {
            @Suppress("UNCHECKED_CAST")
            return this as L
        }
        return null
    }

    private companion object {
        @JvmStatic
        private val tableList = MethodHandles.privateLookupIn(HashLookupTable::class.java, MethodHandles.lookup())
            .findVarHandle(HashLookupTable::class.java, "store", ObjectLinkedOpenHashSet::class.java)
    }
}
