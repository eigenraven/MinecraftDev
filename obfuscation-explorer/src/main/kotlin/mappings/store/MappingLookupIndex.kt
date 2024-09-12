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

import io.mcdev.obfex.lookup.LookupIndex
import io.mcdev.obfex.mappings.MappingElement
import io.mcdev.obfex.mappings.MappingSet
import kotlin.reflect.KClass

class MappingLookupIndex<T : MappingElement, K>(
    private val set: MappingSet,
    private val index: LookupIndex<T, K>
) : LookupIndex<T, K> by index {

    override fun query(key: K?): MappingLookupTable<T> {
        return MappingLookupTable(set, index.query(key))
    }

    override fun <L : LookupIndex<*, *>> unwrap(type: KClass<L>): L? {
        if (type.isInstance(this)) {
            @Suppress("UNCHECKED_CAST")
            return this as L
        }
        return index.unwrap(type)
    }
}
