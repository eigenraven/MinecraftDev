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

import io.mcdev.obfex.MappingPart
import io.mcdev.obfex.ref.LinePriority

/**
 * The location inside a [MappingSetSource] for a particular mapping.
 *
 * [priority] allows for different occurrences of a mapping to specify whether they should take precedence over another.
 *
 * For example, if a member mapping exists which indirectly defines a class mapping, that location would be considered
 * lower priority than the direct class mapping itself. The priority system allows the indirect mapping to still be used
 * if no direct mapping exists.
 */
sealed interface MappingLocation {
    /**
     * Numeric priority for this mapping, where high values are considered higher priority.
     */
    val priority: Int

    fun withPriority(priority: Int): MappingLocation {
        return when (this) {
            is FileCoords -> copy(priority = priority)
            is UnknownLocation -> this
        }
    }
}

/**
 * A [MappingLocation] within a mapping file. Used in conjunction with a [MappingsFile]
 */
data class FileCoords(val line: Int, val col: Int = 1, override val priority: Int = 0) : MappingLocation {
    constructor(line: LinePriority?, col: Int = 1) : this(line?.coord ?: -1, col, line?.priority ?: 0)
    constructor(line: LinePriority?, part: MappingPart) : this(line, part.col)
    constructor(line: Int, part: MappingPart) : this(line, part.col)

    override fun withPriority(priority: Int): FileCoords {
        return super.withPriority(priority) as FileCoords
    }
}

/**
 * An unknown [MappingLocation].
 */
data object UnknownLocation : MappingLocation {
    override val priority: Int
        get() = Int.MIN_VALUE

    override fun withPriority(priority: Int): MappingLocation {
        return this
    }
}
