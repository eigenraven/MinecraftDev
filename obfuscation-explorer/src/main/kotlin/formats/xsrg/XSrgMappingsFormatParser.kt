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

package io.mcdev.obfex.formats.xsrg

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
import io.mcdev.obfex.mappings.pack
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asPackage
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.highPriority
import io.mcdev.obfex.ref.lowPriority
import io.mcdev.obfex.splitOnLast

@Suppress("DuplicatedCode")
class XSrgMappingsFormatParser : UnnamedMappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("xsrg")

    override fun parse(file: VirtualFile): MappingsDefinition {
        val builder = MappingsDefinitionBuilder(XSrgMappingsFormatType, file)

        file.forLines { lineNum, parts, _ ->
            if (parts.isEmpty()) {
                return@forLines true
            }

            val key = parts.first()

            when (key.value) {
                "PK:" -> parsePackageLine(builder, lineNum, parts)
                "CL:" -> parseClassLine(builder, lineNum, parts)
                "MD:" -> parseMethodLine(builder, lineNum, parts)
                "FD:" -> parseFieldLine(builder, lineNum, parts)
                else -> builder.error("Unrecognized key: ${key.value.substring(0, 3)}", FileCoords(lineNum))
            }

            return@forLines true
        }

        return builder.build()
    }

    private fun parsePackageLine(builder: MappingsDefinitionBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 3) {
            builder.error("Package line is invalid", FileCoords(lineNum))
            return
        }

        val (_, leftPack, rightPack) = parts
        builder.pack(lineNum.highPriority) {
            with(leftPack.asPackage().from)
            with(rightPack.asPackage().to)
        }
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
        when (parts.size) {
            5 -> {
                val (leftClass, leftField) = parts[1].splitOnLast('/')
                if (leftField == null) {
                    builder.error(
                        "Field mapping is invalid, no field is specified after class",
                        FileCoords(lineNum, parts[1])
                    )
                    return
                }

                val (rightClass, rightField) = parts[3].splitOnLast('/')
                if (rightField == null) {
                    builder.error(
                        "Field mapping is invalid, no field is specified after class",
                        FileCoords(lineNum, parts[2])
                    )
                    return
                }

                val leftDesc = parts[2].value.asTypeDef()
                if (leftDesc == null) {
                    builder.error("Field descriptor is invalid", FileCoords(lineNum, parts[2]))
                    return
                }

                val rightDesc = parts[4].value.asTypeDef()
                if (rightDesc == null) {
                    builder.error("Field descriptor is invalid", FileCoords(lineNum, parts[4]))
                    return
                }

                builder.clazz(leftClass.asClass().from, lineNum.lowPriority) {
                    unlessExists(rightClass.asClass().to)

                    field(lineNum.highPriority) {
                        type = leftDesc

                        with(leftField.asField().from)
                        with(rightField.asField().to)
                    }
                }
            }
            3 -> {
                val (leftClass, leftField) = parts[1].splitOnLast('/')
                if (leftField == null) {
                    builder.error(
                        "Field mapping is invalid, no field is specified after class",
                        FileCoords(lineNum, parts[1])
                    )
                    return
                }

                val (rightClass, rightField) = parts[2].splitOnLast('/')
                if (rightField == null) {
                    builder.error(
                        "Field mapping is invalid, no field is specified after class",
                        FileCoords(lineNum, parts[2])
                    )
                    return
                }

                builder.clazz(leftClass.asClass().from, lineNum.lowPriority) {
                    unlessExists(rightClass.asClass().to)

                    field(lineNum.highPriority) {
                        with(leftField.asField().from)
                        with(rightField.asField().to)
                    }
                }
            }
            else -> {
                builder.error("Field line is invalid", FileCoords(lineNum))
            }
        }
    }

    private fun parseMethodLine(builder: MappingsDefinitionBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 5) {
            builder.error("Method line is invalid", FileCoords(lineNum))
            return
        }

        val (leftClass, leftMethod) = parts[1].splitOnLast('/')
        if (leftMethod == null) {
            builder.error(
                "Method mapping is invalid, no method is specified after class",
                FileCoords(lineNum, parts[1])
            )
            return
        }

        val (rightClass, rightMethod) = parts[3].splitOnLast('/')
        if (rightMethod == null) {
            builder.error(
                "Method mapping is invalid, no method is specified after class",
                FileCoords(lineNum, parts[3])
            )
            return
        }

        val leftDesc = parts[2].value.asMethodDesc()
        if (leftDesc == null) {
            builder.error("Method descriptor is invalid", FileCoords(lineNum, parts[2]))
            return
        }

        val rightDesc = parts[4].value.asMethodDesc()
        if (rightDesc == null) {
            builder.error("Method descriptor is invalid", FileCoords(lineNum, parts[4]))
            return
        }

        builder.clazz(leftClass.asClass().from, lineNum.lowPriority) {
            unlessExists(rightClass.asClass().to)

            method(lineNum.highPriority) {
                desc = leftDesc

                with(leftMethod.asMethod().from)
                with(rightMethod.asMethod().to)
            }
        }
    }
}
