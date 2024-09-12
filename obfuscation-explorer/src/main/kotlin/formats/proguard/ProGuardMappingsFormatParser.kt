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

package io.mcdev.obfex.formats.proguard

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
import io.mcdev.obfex.ref.ArrayTypeDef
import io.mcdev.obfex.ref.ClassTypeDef
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.PrimitiveTypeDef
import io.mcdev.obfex.ref.ReturnTypeDef
import io.mcdev.obfex.ref.TypeDef
import io.mcdev.obfex.ref.VoidTypeDef
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.highPriority

@Suppress("DuplicatedCode")
class ProGuardMappingsFormatParser : UnnamedMappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("map")

    override fun parse(file: VirtualFile): MappingsDefinition {
        val builder = MappingsDefinitionBuilder(ProGuardMappingsFormatType, file)

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
        if (parts.size != 3) {
            builder.error("Invalid class mapping line", FileCoords(lineNum))
            return null
        }

        if (parts[1].value != "->") {
            builder.error("Unrecognized class mapping separator", FileCoords(lineNum))
            return null
        }

        val leftClass = parts[0]
        val rightClass = parts[2].value.trimEnd(':')

        return builder.clazz(lineNum.highPriority) {
            with(leftClass.asClass().from)
            with(rightClass.asClass().to)
        }
    }

    private val methodLinesRegex = Regex("\\d+:\\d+:")

    private fun parseMember(builder: ClassMappingBuilder, lineNum: Int, parts: Array<MappingPart>) {
        if (parts.size != 4) {
            builder.error("Unrecognized member mapping line", FileCoords(lineNum))
            return
        }

        if (parts[2].value != "->") {
            builder.error("Unrecognized member mapping separator", FileCoords(lineNum))
            return
        }

        val memberDesc = parts[0]
        val leftName = parts[1]
        val rightName = parts[3]

        val match = methodLinesRegex.matchAt(memberDesc.value, 0)
        if (match != null) {
            // method
            val returnTypeText = memberDesc.value.substring(match.range.last + 1)

            val paramsIndex = leftName.value.indexOf('(')
            if (paramsIndex == -1) {
                builder.error("Invalid method descriptor", FileCoords(lineNum, leftName))
                return
            }

            val methodName = leftName.value.substring(0, paramsIndex)
            val returnType = parseType(returnTypeText)
                ?: return builder.error("Invalid return type", FileCoords(lineNum, memberDesc))

            val methodParams = leftName.value.substring(paramsIndex)

            val params = parseMethodParams(methodParams)
                ?: return builder.error("Invalid method descriptor", FileCoords(lineNum, leftName))

            val methodDesc = MethodDescriptor(params, returnType)

            builder.method(lineNum.highPriority) {
                desc = methodDesc

                with(methodName.asMethod().from)
                with(rightName.asMethod().to)
            }
        } else {
            val fieldType = parseType(memberDesc.value)
            if (fieldType !is TypeDef) {
                builder.error("Invalid field type", FileCoords(lineNum, memberDesc))
                return
            }

            builder.field(lineNum.highPriority) {
                type = fieldType

                with(leftName.asField().from)
                with(rightName.asField().to)
            }
        }
    }

    // Proguard doesn't use standard descriptor syntax
    private fun parseMethodParams(params: String): List<TypeDef>? {
        val res = mutableListOf<TypeDef>()

        // first index is (
        var index = 1
        while (index < params.length) {
            val nextIndex = params.indexOfAny(charArrayOf(',', ')'), index)
            if (nextIndex == -1) {
                return null
            }

            val type = parseType(params.substring(index, nextIndex))
            if (type !is TypeDef) {
                return null
            }
            res += type

            index = nextIndex + 1
        }

        return res
    }

    private fun parseType(text: String): ReturnTypeDef? {
        return when (text) {
            "boolean" -> PrimitiveTypeDef.BOOLEAN
            "char" -> PrimitiveTypeDef.CHAR
            "byte" -> PrimitiveTypeDef.BYTE
            "short" -> PrimitiveTypeDef.SHORT
            "int" -> PrimitiveTypeDef.INT
            "long" -> PrimitiveTypeDef.LONG
            "float" -> PrimitiveTypeDef.FLOAT
            "double" -> PrimitiveTypeDef.DOUBLE
            "void" -> VoidTypeDef
            else -> {
                var arrayCount = 0
                while (true) {
                    if (text.lastIndexOf("[]", text.lastIndex - (arrayCount * 2)) > 0) {
                        arrayCount++
                        continue
                    } else {
                        break
                    }
                }

                if (arrayCount > 0) {
                    val baseType = parseType(text.substring(0, text.lastIndex - arrayCount * 2))
                    if (baseType !is TypeDef) {
                        return null
                    }

                    ArrayTypeDef(baseType, arrayCount)
                } else {
                    ClassTypeDef(text.asClass())
                }
            }
        }
    }
}
