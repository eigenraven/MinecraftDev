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

package io.mcdev.obfex.formats.tsrg2

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.MappingPart
import io.mcdev.obfex.Tristate
import io.mcdev.obfex.forLinesIndent
import io.mcdev.obfex.formats.util.indicesFrom
import io.mcdev.obfex.mappings.ClassMappingBuilder
import io.mcdev.obfex.mappings.FileCoords
import io.mcdev.obfex.mappings.MappingsDefinition
import io.mcdev.obfex.mappings.MappingsDefinitionBuilder
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.MethodMappingBuilder
import io.mcdev.obfex.mappings.clazz
import io.mcdev.obfex.mappings.field
import io.mcdev.obfex.mappings.method
import io.mcdev.obfex.mappings.ns
import io.mcdev.obfex.mappings.param
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asParam
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.highPriority
import io.mcdev.obfex.splitMappingLine

class TSrg2MappingsFormatParser : MappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("tsrg")

    override fun isSupportedFile(file: VirtualFile): Boolean {
        val firstLine = file.inputStream.bufferedReader(file.charset).use { reader ->
            reader.readLine()
        }
        return getNamespaces(firstLine) != null
    }

    override fun parse(file: VirtualFile): MappingsDefinition? {
        val firstLine = file.inputStream.bufferedReader(file.charset).use { reader ->
            reader.readLine()
        }
        val namespaces = getNamespaces(firstLine) ?: return null
        val namespaceCount = namespaces.size

        val builder = MappingsDefinitionBuilder(TSrg2MappingsFormatType, file, *namespaces)

        var classMapping: ClassMappingBuilder? = null
        var methodMapping: MethodMappingBuilder? = null
        file.forLinesIndent(skipLines = 1, preserveBlank = true) { indent, lineNum, parts, _ ->
            when (indent) {
                0 -> {
                    methodMapping = null
                    classMapping = parseClass(builder, namespaceCount, lineNum, parts)
                }
                1 -> {
                    methodMapping = parseClassMember(classMapping, namespaceCount, lineNum, parts)
                }
                2 -> parseMethodMember(methodMapping, namespaceCount, lineNum, parts)
            }

            return@forLinesIndent true
        }

        return builder.build()
    }

    private fun parseClass(
        builder: MappingsDefinitionBuilder,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ): ClassMappingBuilder? {
        if (parts.size > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return null
        }

        return builder.clazz(lineNum.highPriority) {
            for (i in parts.indices) {
                with(parts[i].asClass().ns(i))
            }
        }
    }

    private fun parseClassMember(
        builder: ClassMappingBuilder?,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ): MethodMappingBuilder? {
        val desc = parts.getOrNull(1)?.value?.asMethodDesc()
        if (desc != null) {
            return parseMethod(builder, namespaceCount, lineNum, parts, desc)
        } else {
            parseField(builder, namespaceCount, lineNum, parts)
            return null
        }
    }

    private fun parseField(
        builder: ClassMappingBuilder?,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (builder == null) {
            return
        }

        if (parts.size > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        builder.field(lineNum.highPriority) {
            for (i in parts.indices) {
                with(parts[i].asField().ns(i))
            }
        }
    }

    private fun parseMethod(
        builder: ClassMappingBuilder?,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>,
        methodDesc: MethodDescriptor,
    ): MethodMappingBuilder? {
        if (builder == null) {
            return null
        }

        if (parts.size - 1 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return null
        }

        return builder.method(lineNum.highPriority) {
            desc = methodDesc
            // We default static to false, it will be set to true later if we see the static keyword
            meta = meta.copy(isStatic = Tristate.FALSE)

            with(parts[0].asMethod().ns(0))

            for (i in parts.indicesFrom(2)) {
                with(parts[i].asMethod().ns(i - 1))
            }
        }
    }

    private fun parseMethodMember(
        builder: MethodMappingBuilder?,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (builder == null) {
            return
        }

        if (parts.size == 1 && parts[0].value == "static") {
            builder.meta = builder.meta.copy(isStatic = Tristate.TRUE)
            return
        }

        if (parts.size - 1 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        if (parts.isEmpty()) {
            return
        }
        val index = parts[0].value.toIntOrNull()
        if (index == null) {
            builder.error("Method parameter index is not an integer", FileCoords(lineNum, parts[0]))
            return
        }

        builder.param(index.asParamIndex(), lineNum.highPriority) {
            for (i in parts.indicesFrom(1)) {
                with(parts[i].asParam().ns(i - 1))
            }
        }
    }

    private fun getNamespaces(line: String?): Array<String>? {
        if (line == null) {
            return null
        }

        val header = line.splitMappingLine()
        if (header.size < 3) {
            return null
        }

        if (header[0].value != "tsrg2") {
            return null
        }

        val namespaces = arrayOfNulls<String?>(header.size - 1)
        for (index in header.indicesFrom(1)) {
            namespaces[index - 1] = header[index].value
        }

        @Suppress("UNCHECKED_CAST")
        return namespaces as Array<String>
    }
}
