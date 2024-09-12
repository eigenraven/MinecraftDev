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

package io.mcdev.obfex.formats.tsrg

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.MappingPart
import io.mcdev.obfex.forLinesIndent
import io.mcdev.obfex.mappings.ClassMappingBuilder
import io.mcdev.obfex.mappings.FileCoords
import io.mcdev.obfex.mappings.MappingsDefinition
import io.mcdev.obfex.mappings.MappingsDefinitionBuilder
import io.mcdev.obfex.mappings.UnnamedMappingsFormatParser
import io.mcdev.obfex.mappings.clazz
import io.mcdev.obfex.mappings.field
import io.mcdev.obfex.mappings.method
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.highPriority

@Suppress("DuplicatedCode")
class TSrgMappingsFormatParser : UnnamedMappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("tsrg")

    override fun parse(file: VirtualFile): MappingsDefinition {
        val builder = MappingsDefinitionBuilder(TSrgMappingsFormatType, file)

        var classMapping: ClassMappingBuilder? = null
        file.forLinesIndent { indent, lineNum, parts, _ ->
            if (indent > 0) {
                val c = classMapping
                if (c == null) {
                    builder.error("Member mapping line with no associated class", FileCoords(lineNum))
                } else {
                    parseMember(c, lineNum, parts)
                }
            } else {
                classMapping = parseClass(builder, lineNum, parts)
            }

            return@forLinesIndent true
        }

        return builder.build()
    }

    private fun parseClass(
        builder: MappingsDefinitionBuilder,
        lineNum: Int,
        parts: Array<MappingPart>,
    ): ClassMappingBuilder? {
        if (parts.size != 2) {
            builder.error("Invalid class line", FileCoords(lineNum))
            return null
        }

        val (leftClass, rightClass) = parts

        return builder.clazz(lineNum.highPriority) {
            with(leftClass.asClass().from)
            with(rightClass.asClass().to)
        }
    }

    private fun parseMember(builder: ClassMappingBuilder, lineNum: Int, parts: Array<MappingPart>) {
        when (parts.size) {
            2 -> parseField(builder, lineNum, parts[0], parts[1])
            3 -> parseMethod(builder, lineNum, parts[0], parts[1], parts[2])
            else -> builder.error("Invalid member line", FileCoords(lineNum))
        }
    }

    private fun parseField(builder: ClassMappingBuilder, lineNum: Int, leftName: MappingPart, rightName: MappingPart) {
        builder.field(lineNum.highPriority) {
            with(leftName.asField().from)
            with(rightName.asField().to)
        }
    }

    private fun parseMethod(
        builder: ClassMappingBuilder,
        lineNum: Int,
        leftName: MappingPart,
        leftDesc: MappingPart,
        rightName: MappingPart,
    ) {
        val methodDesc = leftDesc.value.asMethodDesc()
        if (methodDesc == null) {
            builder.error("Invalid method descriptor", FileCoords(lineNum, leftDesc))
            return
        }

        builder.method(lineNum.highPriority) {
            desc = methodDesc

            with(leftName.asMethod().from)
            with(rightName.asMethod().to)
        }
    }
}
