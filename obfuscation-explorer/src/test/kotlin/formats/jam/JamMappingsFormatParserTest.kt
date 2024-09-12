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

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.unnamedFrom
import io.mcdev.obfex.mappings.unnamedTo
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.PrimitiveTypeDef
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asMethodRef
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.field
import io.mcdev.obfex.ref.method
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class JamMappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = JamMappingsFormatParser()

    @Language("jam")
    override val text: String = """
        CL ght net/minecraft/Test
        CL ght${'$'}a net/minecraft/Test${'$'}Inner
        FD ght rft Ljava/util/logging/Logger; log
        MD ght hyuip (I)Z isEven
        MP ght hyuip (I)Z 0 num
    """.trimIndent()

    @Test
    fun testClass() {
        val from = def.mappings.clazz("ght".asClass())
        val to = def.mappings.clazz("net/minecraft/Test".asClass())

        assertSame(from, to)
    }

    @Test
    fun testField() {
        val mapping = def.mappings.fieldMapping(
            "ght".asClass()
                .field("rft".asFieldRef("Ljava/util/logging/Logger;".asTypeDef()))
        )!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("rft", fromName)
        assertEquals("log", toName)
    }

    @Test
    fun testMethod() {
        val clazz = def.mappings.clazz("ght".asClass())!!
        val mapping = clazz.methods().first { it.unnamedFrom == "hyuip" }

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("hyuip", fromName)
        assertEquals("isEven", toName)

        assertEquals(
            MethodDescriptor(
                listOf(PrimitiveTypeDef.INT),
                PrimitiveTypeDef.BOOLEAN
            ),
            mapping.descriptor
        )
    }

    @Test
    fun testParam() {
        val ref = "net/minecraft/Test".asClass()
            .method("isEven".asMethodRef("(I)Z".asMethodDesc()))

        val method = def.mappings.methodMapping(ref)!!
        val mapping = method.param(0.asParamIndex())!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertNull(fromName)
        assertEquals("num", toName)
    }
}
