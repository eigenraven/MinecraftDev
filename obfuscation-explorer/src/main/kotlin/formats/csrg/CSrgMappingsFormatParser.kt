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

package io.mcdev.obfex.formats.csrg

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.MappingPart
import io.mcdev.obfex.forLines
import io.mcdev.obfex.mappings.FileCoords
import io.mcdev.obfex.mappings.MappingsDefinition
import io.mcdev.obfex.mappings.MappingsDefinitionBuilder
import io.mcdev.obfex.mappings.UnnamedMappingsFormatParser
import io.mcdev.obfex.mappings.clazz
import io.mcdev.obfex.mappings.field
import io.mcdev.obfex.mappings.method
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.highPriority
import io.mcdev.obfex.ref.lowPriority

class CSrgMappingsFormatParser : UnnamedMappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("csrg")

    override fun parse(file: VirtualFile): MappingsDefinition {
        val builder = MappingsDefinitionBuilder(CSrgMappingsFormatType, file)

        file.forLines { lineNum, parts, line ->
            when (parts.size) {
                2 -> parseClassLine(builder, lineNum, parts[0], parts[1])
                3 -> parseFieldLine(builder, lineNum, parts[0], parts[1], parts[2])
                4 -> parseMethodLine(builder, lineNum, parts[0], parts[1], parts[2], parts[3])
                else -> {
                    builder.error("Unrecognized line: $line", FileCoords(lineNum))
                }
            }

            true
        }

        return builder.build()
    }

    private fun parseClassLine(
        builder: MappingsDefinitionBuilder,
        lineNum: Int,
        leftClass: MappingPart,
        rightClass: MappingPart,
    ) {
        builder.clazz(lineNum.highPriority) {
            with(leftClass.asClass().from)
            with(rightClass.asClass().to)
        }
    }

    private fun parseFieldLine(
        builder: MappingsDefinitionBuilder,
        lineNum: Int,
        className: MappingPart,
        leftField: MappingPart,
        rightField: MappingPart,
    ) {
        builder.clazz(className.asClass().from, lineNum.lowPriority).field(lineNum.highPriority) {
            with(leftField.asField().from)
            with(rightField.asField().to)
        }
    }

    private fun parseMethodLine(
        builder: MappingsDefinitionBuilder,
        lineNum: Int,
        className: MappingPart,
        leftMethod: MappingPart,
        methodDesc: MappingPart,
        rightMethod: MappingPart,
    ) {
        val descParsed = MethodDescriptor.parse(methodDesc)
        if (descParsed == null) {
            builder.error("Invalid method descriptor: ${methodDesc.value}", FileCoords(lineNum, methodDesc))
            return
        }

        builder.clazz(className.asClass().from, lineNum.lowPriority).method(lineNum.highPriority) {
            desc = descParsed

            with(leftMethod.asMethod().from)
            with(rightMethod.asMethod().to)
        }
    }
}
