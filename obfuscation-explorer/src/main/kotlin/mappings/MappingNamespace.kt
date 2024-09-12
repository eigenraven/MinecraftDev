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

package io.mcdev.obfex.mappings

/**
 * A namespace is a collection of names in a mapping. A single mapping set can have an arbitrary number of namespaces,
 * each namespace providing a different name for the same unique element.
 */
data class MappingNamespace(
    val name: String,
    val index: Int,
    val associatedSet: MappingSet?,
) {
    companion object {
        const val UNNAMED_FROM: String = "from"
        const val UNNAMED_TO: String = "to"

        fun unnamedFrom(set: MappingSet) = set.namespaceOf(UNNAMED_FROM)
        fun unnamedTo(set: MappingSet) = set.namespaceOf(UNNAMED_TO)
    }
}
