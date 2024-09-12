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

package io.mcdev.obfex.formats.jam

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
import io.mcdev.obfex.mappings.param
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asParam
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.asRef
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.highPriority
import io.mcdev.obfex.ref.lowPriority

@Suppress("DuplicatedCode")
class JamMappingsFormatParser : UnnamedMappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("jam")

    override fun parse(file: VirtualFile): MappingsDefinition {
        val builder = MappingsDefinitionBuilder(JamMappingsFormatType, file)

        file.forLines { lineNum, parts, _ ->
            if (parts.isEmpty()) {
                return@forLines true
            }

            val key = parts.first()

            when (key.value) {
                "CL" -> parseClassLine(builder, lineNum, parts)
                "FD" -> parseFieldLine(builder, lineNum, parts)
                "MD" -> parseMethodLine(builder, lineNum, parts)
                "MP" -> parseMethodParamLine(builder, lineNum, parts)
                else -> builder.error("Unrecognized key: ${key.value.substring(0, 2)}", FileCoords(lineNum))
            }

            return@forLines true
        }

        return builder.build()
    }

    private fun parseClassLine(builder: MappingsDefinitionBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 3) {
            builder.error("Class line is invalid", FileCoords(lineNum))
            return
        }

        val (_, leftClass, rightClass) = parts
        builder.clazz(lineNum.highPriority) {
            with(leftClass.asClass().from)
            with(rightClass.asClass().to)
        }
    }

    private fun parseFieldLine(builder: MappingsDefinitionBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 5) {
            builder.error("Field line is invalid", FileCoords(lineNum))
            return
        }

        val (_, leftClass, leftName, leftDesc, rightName) = parts

        val leftType = leftDesc.value.asTypeDef()
            ?: return builder.error("Field descriptor is invalid", FileCoords(lineNum, leftDesc))

        builder.clazz(leftClass.asClass().from, lineNum.lowPriority).field(lineNum.highPriority) {
            type = leftType

            with(leftName.asField().from)
            with(rightName.asField().to)
        }
    }

    private fun parseMethodLine(builder: MappingsDefinitionBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 5) {
            builder.error("Method line is invalid", FileCoords(lineNum))
            return
        }

        val (_, leftClass, leftMethod, leftDesc, rightName) = parts

        val leftType = leftDesc.value.asMethodDesc()
            ?: return builder.error("Method descriptor is invalid", FileCoords(lineNum, leftDesc))

        builder.clazz(leftClass.asClass().from, lineNum.lowPriority).method(lineNum.highPriority) {
            desc = leftType

            with(leftMethod.asMethod().from)
            with(rightName.asMethod().to)
        }
    }

    private fun parseMethodParamLine(builder: MappingsDefinitionBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 6) {
            builder.error("Method parameter line is invalid", FileCoords(lineNum))
            return
        }

        val (_, leftClass, leftMethod, leftDesc, index) = parts
        val rightName = parts[5]

        val leftType = leftDesc.value.asMethodDesc()
            ?: return builder.error("Method descriptor is invalid", FileCoords(lineNum, leftDesc))

        val indexNum = index.value.toIntOrNull()
            ?: return builder.error("Method param index is invalid", FileCoords(lineNum, index))

        builder.clazz(leftClass.asClass().from, lineNum.lowPriority)
            .method(leftMethod.asMethod().asRef(leftType).from, lineNum.lowPriority)
            .param(indexNum.asParamIndex(), lineNum.highPriority) {
                with(rightName.asParam().to)
            }
    }
}
