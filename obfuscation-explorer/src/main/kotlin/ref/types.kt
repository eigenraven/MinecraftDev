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

package io.mcdev.obfex.ref

import io.mcdev.obfex.MappingPart
import java.lang.IllegalStateException

sealed interface ReturnTypeDef {
    val descriptor: String

    fun isMappable(): Boolean

    companion object {
        fun parse(desc: String) = desc.asReturnTypeDef()
    }
}

object VoidTypeDef : ReturnTypeDef {
    override val descriptor = "V"

    override fun isMappable(): Boolean = false

    override fun toString() = descriptor
}

sealed interface TypeDef : ReturnTypeDef {
    override fun isMappable(): Boolean = when (this) {
        is PrimitiveTypeDef -> false
        is ClassTypeDef -> true
        is ArrayTypeDef -> componentType.isMappable()
    }

    companion object {
        fun parse(desc: String) = desc.asTypeDef()
    }
}

enum class PrimitiveTypeDef(override val descriptor: String) : TypeDef {
    BOOLEAN("Z"),
    CHAR("C"),
    BYTE("B"),
    SHORT("S"),
    INT("I"),
    LONG("J"),
    FLOAT("F"),
    DOUBLE("D");

    override fun toString() = descriptor

    companion object {
        fun fromChar(c: Char): PrimitiveTypeDef? {
            return when (c) {
                'Z' -> BOOLEAN
                'C' -> CHAR
                'B' -> BYTE
                'S' -> SHORT
                'I' -> INT
                'J' -> LONG
                'F' -> FLOAT
                'D' -> DOUBLE
                else -> null
            }
        }
    }
}

data class ClassTypeDef(val className: ClassName) : TypeDef {
    override val descriptor: String
        get() = "L${className.name};"

    override fun toString(): String = descriptor
}

data class ArrayTypeDef(val componentType: TypeDef, val dimension: Int) : TypeDef {
    override val descriptor: String
        get() = "[".repeat(dimension) + componentType.descriptor

    override fun toString() = descriptor
}

fun String.asReturnTypeDef(): ReturnTypeDef? = parseType(this, 0)?.type
fun String.asTypeDef(): TypeDef? = parseType(this, 0)?.type as? TypeDef

private data class TypeReturn(val index: Int, val type: ReturnTypeDef)

private fun parseType(text: String, index: Int): TypeReturn? {
    return when (val c = text[index]) {
        'L' -> {
            val endIndex = text.indexOf(';', index + 1)
            if (endIndex == -1) {
                return null
            }
            TypeReturn(endIndex + 1, ClassTypeDef(ClassName(text.substring(index + 1, endIndex))))
        }
        '[' -> {
            var endIndex: Int = -1
            for (i in index + 1 until text.length) {
                val next = text[i]
                if (next != '[') {
                    endIndex = i
                    break
                }
            }
            if (endIndex == -1) {
                return null
            }

            val componentType = parseType(text, endIndex) ?: return null
            if (componentType.type is ArrayTypeDef) {
                // should be impossible
                throw IllegalStateException()
            } else if (componentType.type == VoidTypeDef) {
                return null
            }

            return TypeReturn(
                componentType.index,
                ArrayTypeDef(componentType.type as TypeDef, endIndex - index)
            )
        }
        'V' -> TypeReturn(index + 1, VoidTypeDef)
        else -> PrimitiveTypeDef.fromChar(c)?.let { TypeReturn(index + 1, it) }
    }
}

data class MethodDescriptor(val params: List<TypeDef>, val returnType: ReturnTypeDef) {

    fun isMappable(): Boolean = params.any { it.isMappable() } || returnType.isMappable()

    override fun toString(): String = buildString {
        append('(')
        for (param in params) {
            append(param.descriptor)
        }
        append(')')
        append(returnType.descriptor)
    }

    companion object {
        fun parse(text: MappingPart?, startIndex: Int = 0) = parse(text?.value, startIndex)
        fun parse(text: String?, startIndex: Int = 0): MethodDescriptor? {
            if (text.isNullOrBlank()) {
                return null
            }

            if (text.length <= startIndex || text[startIndex] != '(') {
                return null
            }

            val paramTypes = mutableListOf<TypeDef>()

            var index = startIndex + 1
            while (true) {
                if (text.length <= index) {
                    return null
                }
                if (text[index] == ')') {
                    index++
                    break
                }

                val type = parseType(text, index) ?: return null
                if (type.type == VoidTypeDef) {
                    // void not allowed as a param type
                    return null
                }
                paramTypes.add(type.type as TypeDef)
                index = type.index
            }

            val returnType = parseType(text, index) ?: return null
            if (text.length > returnType.index) {
                return null
            }

            return MethodDescriptor(paramTypes, returnType.type)
        }
    }
}

fun String.asMethodDesc(): MethodDescriptor? = MethodDescriptor.parse(this)
