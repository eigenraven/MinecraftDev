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
 * Complete top-level definition for a collection of mappings, which includes the mappings themselves and their source.
 */
class MappingsDefinition(
    val mappings: MappingSet,
    val source: MappingSetSource,
)

class MappingsDefinitionBuilder(
    val mappings: MappingSetBuilder,
    val source: MappingsFileBuilder,
) : MappingSetBuilderCore by mappings, MappingIssuesRegistry by mappings.issues {

    constructor(fileBuilder: MappingsFileBuilder, vararg namespaces: String) :
        this(MappingSetBuilder(fileBuilder, *namespaces), fileBuilder)

    constructor(type: MappingsFormatType, file: VirtualFile, vararg namespaces: String) :
        this(MappingsFileBuilder(file, type), *namespaces)

    val namespaces: Array<String>
        get() = mappings.namespaces

    val issues: MappingIssuesRegistry
        get() = mappings.issues

    fun ns(namespace: String): Int = namespace.indexOf(namespace)

    fun build() = MappingsDefinition(mappings.build(), source.build())
}
