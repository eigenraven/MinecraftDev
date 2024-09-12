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

package io.mcdev.obfex.formats.enigma

import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.MappingPart
import io.mcdev.obfex.forLinesIndent
import io.mcdev.obfex.formats.util.indicesFrom
import io.mcdev.obfex.mappings.ClassMappingBuilder
import io.mcdev.obfex.mappings.FileCoords
import io.mcdev.obfex.mappings.MappingIssuesRegistry
import io.mcdev.obfex.mappings.MappingSetBuilderCore
import io.mcdev.obfex.mappings.MappingsDefinition
import io.mcdev.obfex.mappings.MappingsDefinitionBuilder
import io.mcdev.obfex.mappings.MethodMappingBuilder
import io.mcdev.obfex.mappings.UnnamedMappingsFormatParser
import io.mcdev.obfex.mappings.clazz
import io.mcdev.obfex.mappings.field
import io.mcdev.obfex.mappings.method
import io.mcdev.obfex.mappings.ns
import io.mcdev.obfex.mappings.param
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asField
import io.mcdev.obfex.ref.asMethod
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asParam
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.highPriority

class EnigmaMappingsFormatParser : UnnamedMappingsFormatParser {

    override val expectedFileExtensions: Array<String> = arrayOf("mapping", "mappings")

    private data class State(
        val builder: MappingsDefinitionBuilder,
        var baseIndent: Int = 0,
        var classNamesStack: ArrayDeque<ClassMappingBuilder> = ArrayDeque(),
        var classMapping: ClassMappingBuilder? = null,
        var methodMapping: MethodMappingBuilder? = null,
    ) : MappingSetBuilderCore by builder, MappingIssuesRegistry by builder

    override fun parse(file: VirtualFile): MappingsDefinition {
        val builder = MappingsDefinitionBuilder(EnigmaMappingsFormatType, file)

        val state = State(builder)
        file.forLinesIndent { indent, lineNum, parts, _ ->
            when {
                indent == state.baseIndent -> parseClass(state, lineNum, parts)
                indent == state.baseIndent + 1 -> parseMember(state, lineNum, parts)
                indent == state.baseIndent + 2 -> parseSubMember(state, lineNum, parts)
                indent < state.baseIndent -> {
                    // new class starting
                    state.baseIndent = indent
                    while (state.classNamesStack.size > state.baseIndent) {
                        state.classNamesStack.removeLastOrNull()
                    }
                    parseClass(state, lineNum, parts)
                }
            }

            return@forLinesIndent true
        }

        return builder.build()
    }

    private fun parseClass(state: State, lineNum: Int, parts: Array<MappingPart>) {
        state.classMapping = null
        state.methodMapping = null

        if (parts.isEmpty()) {
            return
        }

        when (parts[0].value) {
            "CLASS" -> {
                if (parts.size != 3) {
                    state.error("Unexpected number of entries in class mapping line", FileCoords(lineNum))
                    return
                }

                val mapping = state.clazz(lineNum.highPriority, parts[0].col) {
                    for (i in parts.indicesFrom(1)) {
                        val className = fullClassName(state, i - 1, parts[i].value)
                        if (className == null) {
                            state.error(
                                "All component names for class mapping could not be built as" +
                                    "not all names in the hierarchy are present",
                                FileCoords(lineNum)
                            )
                        } else {
                            with(className.asClass().ns(i - 1))
                        }
                    }
                }

                state.classMapping = mapping
                state.classNamesStack.addLast(mapping)
            }
            "COMMENT" -> return
            else -> state.error("Unexpected mapping file entry", FileCoords(lineNum))
        }
    }

    private fun fullClassName(state: State, index: Int, name: String): String? {
        if (state.classNamesStack.isEmpty()) {
            return name
        }

        val last = state.classNamesStack.lastOrNull()?.names?.get(index) ?: return null
        @Suppress("ConvertToStringTemplate")
        return last + '$' + name
    }

    private fun parseMember(state: State, lineNum: Int, parts: Array<MappingPart>) {
        state.methodMapping = null

        if (parts.isEmpty()) {
            return
        }

        when (parts[0].value) {
            "FIELD" -> parseField(state, lineNum, parts)
            "METHOD" -> parseMethod(state, lineNum, parts)
            "CLASS" -> {
                state.baseIndent++
                parseClass(state, lineNum, parts)
            }
            "COMMENT" -> return
            else -> state.error("Unexpected mapping file entry", FileCoords(lineNum))
        }
    }

    private fun parseField(state: State, lineNum: Int, parts: Array<MappingPart>) {
        val builder = state.classMapping ?: return

        val fieldType = when (parts.size) {
            3 -> null
            4 -> parts[3].value.asTypeDef()
                ?: return builder.error("Invalid field type", FileCoords(lineNum, parts[3]))
            else -> return state.error("Unexpected number of entries in field mapping line", FileCoords(lineNum))
        }

        builder.field(lineNum.highPriority, parts[0].col) {
            type = fieldType

            with(parts[1].asField().from)
            with(parts[2].asField().to)
        }
    }

    private fun parseMethod(state: State, lineNum: Int, parts: Array<MappingPart>) {
        val builder = state.classMapping ?: return

        val (leftName, rightName, methodDesc) = when (parts.size) {
            3 -> Triple(parts[1], parts[1], parts[2])
            4 -> Triple(parts[1], parts[2], parts[3])
            else -> return state.error("Unexpected number of entries in method mapping line", FileCoords(lineNum))
        }

        val parsed = methodDesc.value.asMethodDesc()
            ?: return state.error("Invalid method descriptor", FileCoords(lineNum, methodDesc))

        state.methodMapping = builder.method(lineNum.highPriority, parts[0].col) {
            desc = parsed

            with(leftName.asMethod().from)
            with(rightName.asMethod().to)
        }
    }

    private fun parseSubMember(state: State, lineNum: Int, parts: Array<MappingPart>) {
        val builder = state.methodMapping ?: return

        if (parts.isEmpty()) {
            return
        }

        if (parts[0].value == "COMMENT") {
            return
        }
        if (parts[0].value != "ARG") {
            return state.error("Unexpected mapping file entry", FileCoords(lineNum))
        }

        if (parts.size != 3) {
            return state.error("Unexpected number of entries in method parameter mapping line", FileCoords(lineNum))
        }

        val index = parts[1].value.toIntOrNull()
            ?: return state.error("Method parameter index is not a valid integer", FileCoords(lineNum, parts[1]))

        // TODO enigma actually uses lvt indices, not param. Enigma doesn't include metadata on if the method is static
        //  or not so we can't reliably work it out here
        builder.param(index.asParamIndex(), lineNum.highPriority) {
            with(parts[2].asParam().to)
        }
    }
}
