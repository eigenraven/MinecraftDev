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

package io.mcdev.obfex.formats.tinyv1

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.MappingPart
import io.mcdev.obfex.forLines
import io.mcdev.obfex.formats.util.indicesFrom
import io.mcdev.obfex.mappings.FileCoords
import io.mcdev.obfex.mappings.MappingsDefinition
import io.mcdev.obfex.mappings.MappingsDefinitionBuilder
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.clazz
import io.mcdev.obfex.mappings.field
import io.mcdev.obfex.mappings.method
import io.mcdev.obfex.mappings.ns
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.highPriority
import io.mcdev.obfex.ref.lowPriority
import io.mcdev.obfex.splitMappingLine

class TinyV1MappingsFormatParser : MappingsFormatParser {

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

        val builder = MappingsDefinitionBuilder(TinyV1MappingsFormatType, file, *namespaces)

        file.forLines(skipLines = 1, preserveBlank = true) { lineNum, parts, _ ->
            if (parts.isEmpty()) {
                return@forLines true
            }

            val key = parts[0].value
            when (key) {
                "CLASS" -> parseClass(builder, namespaces.size, lineNum, parts)
                "FIELD" -> parseField(builder, namespaces.size, lineNum, parts)
                "METHOD" -> parseMethod(builder, namespaces.size, lineNum, parts)
            }

            return@forLines true
        }

        return builder.build()
    }

    private fun getNamespaces(line: String?): Array<String>? {
        if (line == null) {
            return null
        }

        val header = line.splitMappingLine()
        if (header.size < 3) {
            return null
        }

        if (header[0].value != "v1") {
            return null
        }

        val namespaces = arrayOfNulls<String?>(header.size - 1)
        for (index in 1 until header.size) {
            namespaces[index - 1] = header[index].value
        }

        @Suppress("UNCHECKED_CAST")
        return namespaces as Array<String>
    }

    private fun parseClass(
        builder: MappingsDefinitionBuilder,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (parts.size - 1 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        if (parts.size == 1) {
            builder.warning("Class mapping line has no names", FileCoords(lineNum))
            return
        }

        builder.clazz(lineNum.highPriority) {
            for (i in parts.indicesFrom(1)) {
                with(parts[i].asClass().ns(i - 1))
            }
        }
    }

    private fun parseField(
        builder: MappingsDefinitionBuilder,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (parts.size - 3 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        if (parts.size < 3) {
            builder.error("Field mapping line does not have all necessary components", FileCoords(lineNum))
            return
        }

        if (parts.size == 3) {
            builder.warning("Field mapping line has no names", FileCoords(lineNum))
        }

        val owner = parts[1]
        val fieldType = parts[2].value.asTypeDef()
            ?: return builder.error("Field mapping has an invalid type", FileCoords(lineNum, parts[2]))

        builder.clazz(owner.asClass().ns(0), lineNum.lowPriority).field {
            type = fieldType

            for (i in parts.indicesFrom(3)) {
                with(parts[i].asField().ns(i - 3))
            }
        }
    }

    private fun parseMethod(
        builder: MappingsDefinitionBuilder,
        namespaceCount: Int,
        lineNum: Int,
        parts: Array<MappingPart>
    ) {
        if (parts.size - 3 > namespaceCount) {
            builder.error("Unexpected number of names present", FileCoords(lineNum))
            return
        }

        if (parts.size < 3) {
            builder.error("Method mapping line does not have all necessary components", FileCoords(lineNum))
            return
        }

        if (parts.size == 3) {
            builder.warning("Method mapping line has no names", FileCoords(lineNum))
        }

        val owner = parts[1]
        val methodDesc = parts[2].value.asMethodDesc()
            ?: return builder.error("Method mapping has an invalid descriptor", FileCoords(lineNum, parts[2]))

        builder.clazz(owner.asClass().ns(0)).method {
            desc = methodDesc

            for (i in parts.indicesFrom(3)) {
                with(parts[i].asMethod().ns(i - 3))
            }
        }
    }
}
