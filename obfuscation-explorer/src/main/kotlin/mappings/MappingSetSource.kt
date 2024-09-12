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

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.formats.MappingsFormatType

/**
 * Defines an issue (warning / error) related to a parsed mapping file. Ideally the [coords] are provided as well to
 * identify where the issue occurs.
 */
data class MappingParseIssue(val message: String, val coords: FileCoords?)

/**
 * Builder interface for registering new issues, shared between several builders.
 */
interface MappingIssuesRegistry {
    fun error(message: String, coords: FileCoords? = null)
    fun warning(message: String, coords: FileCoords? = null)
}

sealed interface MappingSetSource {
    val errors: List<MappingParseIssue>
    val warnings: List<MappingParseIssue>
}

class MappingsFile(
    val file: VirtualFile,
    val type: MappingsFormatType,
    override val errors: List<MappingParseIssue> = emptyList(),
    override val warnings: List<MappingParseIssue> = emptyList(),
) : MappingSetSource

class MappingsFileBuilder(
    val file: VirtualFile,
    val type: MappingsFormatType,
) : MappingIssuesRegistry {

    val errors: MutableList<MappingParseIssue> = mutableListOf()
    val warnings: MutableList<MappingParseIssue> = mutableListOf()

    override fun error(message: String, coords: FileCoords?) {
        errors.add(MappingParseIssue(message, coords))
    }

    override fun warning(message: String, coords: FileCoords?) {
        warnings.add(MappingParseIssue(message, coords))
    }

    fun build() = MappingsFile(file, type, errors.toList(), warnings.toList())
}
