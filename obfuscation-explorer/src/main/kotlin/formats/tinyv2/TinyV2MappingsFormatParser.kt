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

package io.mcdev.obfex.formats.tinyv2

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.MappingPart
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
import io.mcdev.obfex.mappings.localVar
import io.mcdev.obfex.mappings.method
import io.mcdev.obfex.mappings.ns
import io.mcdev.obfex.mappings.param
import io.mcdev.obfex.ref.LvtIndex
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asLocal
import io.mcdev.obfex.ref.asLocalVar
import io.mcdev.obfex.ref.asLvtIndex
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asParam
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.highPriority
import io.mcdev.obfex.splitMappingLine

class TinyV2MappingsFormatParser : MappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("tiny")

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

        val builder = MappingsDefinitionBuilder(TinyV2MappingsFormatType, file, *namespaces)

        var classMapping: ClassMappingBuilder? = null
        var methodMapping: MethodMappingBuilder? = null
        file.forLinesIndent(skipLines = 1, preserveBlank = true) { indent, lineNum, parts, _ ->
            if (parts.isEmpty()) {
                return@forLinesIndent true
            }

            val key = parts[0].value
            when (key) {
                "c" -> {
                    methodMapping = null
                    classMapping = parseClass(builder, indent, namespaces.size, lineNum, parts)
                }
                "f" -> {
                    methodMapping = null
                    parseField(classMapping, indent, namespaces.size, lineNum, parts)
                }
                "m" -> methodMapping = parseMethod(classMapping, indent, namespaces.size, lineNum, parts)
                "p" -> parseParam(methodMapping, indent, namespaces.size, lineNum, parts)
                "v" -> parseLocalVar(methodMapping, indent, namespaces.size, lineNum, parts)
            }

            return@forLinesIndent true
        }

        return builder.build()
    }

    private fun getNamespaces(line: String?): Array<String>? {
        if (line == null) {
            return null
        }

        val header = line.splitMappingLine()
        if (header.size < 5) {
            return null
        }

        if (header[0].value != "tiny" || header[1].value != "2" || header[2].value != "0") {
            return null
        }

        val namespaces = arrayOfNulls<String?>(header.size - 3)
        for (index in header.indicesFrom(3)) {
            namespaces[index - 3] = header[index].value
        }

        @Suppress("UNCHECKED_CAST")
        return namespaces as Array<String>
    }

    private fun parseClass(
        builder: MappingsDefinitionBuilder,
        indent: Int,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ): ClassMappingBuilder? {
        if (indent != 0) {
            builder.error("Unexpected indent of class mapping line", FileCoords(lineNum))
            return null
        }

        if (parts.size - 1 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return null
        }

        if (parts.size == 1) {
            builder.warning("Class mapping has no names", FileCoords(lineNum))
        }

        return builder.clazz(lineNum.highPriority) {
            for (i in parts.indicesFrom(1)) {
                with(parts[i].asClass().ns(i - 1))
            }
        }
    }

    private fun parseField(
        builder: ClassMappingBuilder?,
        indent: Int,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (builder == null) {
            return
        }

        if (indent != 1) {
            builder.error("Unexpected indent of field mapping line", FileCoords(lineNum))
            return
        }

        if (parts.size - 2 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        if (parts.size < 2) {
            builder.error("Field mapping line does not have all necessary components", FileCoords(lineNum))
            return
        }

        if (parts.size == 2) {
            builder.warning("Field mapping line has no names", FileCoords(lineNum))
        }

        val fieldType = parts[1].value.asTypeDef()
            ?: return builder.error("Field descriptor is invalid", FileCoords(lineNum, parts[1]))

        builder.field(lineNum.highPriority) {
            type = fieldType

            for (i in parts.indicesFrom(2)) {
                with(parts[i].asField().ns(i - 2))
            }
        }
    }

    private fun parseMethod(
        builder: ClassMappingBuilder?,
        indent: Int,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ): MethodMappingBuilder? {
        if (builder == null) {
            return null
        }

        if (indent != 1) {
            builder.error("Unexpected indent of method mapping line", FileCoords(lineNum))
            return null
        }

        if (parts.size - 2 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return null
        }

        if (parts.size < 2) {
            builder.error("Method mapping line does not have all necessary components", FileCoords(lineNum))
            return null
        }

        if (parts.size == 2) {
            builder.warning("Method mapping line has no names", FileCoords(lineNum))
        }

        val methodDesc = parts[1].value.asMethodDesc()
            ?: run {
                builder.error("Method descriptor is invalid", FileCoords(lineNum, parts[1]))
                return null
            }

        return builder.method(lineNum.highPriority) {
            desc = methodDesc

            for (i in parts.indicesFrom(2)) {
                with(parts[i].asMethod().ns(i - 2))
            }
        }
    }

    private fun parseParam(
        builder: MethodMappingBuilder?,
        indent: Int,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (builder == null) {
            return
        }

        if (indent != 2) {
            builder.error("Unexpected indent of method parameter mapping line", FileCoords(lineNum))
            return
        }

        if (parts.size - 2 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        if (parts.size < 2) {
            builder.error("Method parameter mapping line does not have all necessary components", FileCoords(lineNum))
            return
        }

        if (parts.size == 2) {
            builder.warning("Method parameter mapping line has no names", FileCoords(lineNum))
        }

        val index = parts[1].value.toIntOrNull()
            ?: return builder.error("Method parameter index is not an integer", FileCoords(lineNum, parts[1]))

        builder.param(index.asParamIndex(), lineNum.highPriority) {
            for (i in parts.indicesFrom(2)) {
                with(parts[i].asParam().ns(i - 2))
            }
        }
    }

    private fun parseLocalVar(
        builder: MethodMappingBuilder?,
        indent: Int,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (builder == null) {
            return
        }

        if (indent != 2) {
            builder.error("Unexpected indent of method local var mapping line", FileCoords(lineNum))
            return
        }

        if (parts.size < 3) {
            builder.error("Method local var mapping line does not have all necessary components", FileCoords(lineNum))
            return
        }

        val localIndex = parts[1].value.toIntOrNull()?.asLocal()
            ?: return builder.error("Method local var index is not an integer", FileCoords(lineNum, parts[1]))
        val localStart = parts[2].value.toIntOrNull()?.asLocal()
            ?: return builder.error("Method local var start offset is not an integer", FileCoords(lineNum, parts[2]))

        val lvtIndex = if (parts.size >= 4) {
            // lvtIndex is optional
            parts[3].value.toIntOrNull()?.asLvtIndex()
        } else {
            null
        } ?: LvtIndex.UNKNOWN

        val nameStartIndex = if (lvtIndex.isKnown) 4 else 3

        if (parts.size - nameStartIndex > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        builder.localVar(localIndex.asLocalVar(startIndex = localStart), lvtIndex, lineNum.highPriority) {
            for (i in parts.indicesFrom(nameStartIndex)) {
                with(parts[i].asLocal().ns(i - nameStartIndex))
            }
        }
    }
}
