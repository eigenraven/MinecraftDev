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

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.unnamedFrom
import io.mcdev.obfex.mappings.unnamedTo
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.PrimitiveTypeDef
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asPackage
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.field
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class XSrgMappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = XSrgMappingsFormatParser()

    @Language("xsrg")
    override val text: String = """
        PK: ./ net/minecraft/server
        CL: ght net/minecraft/Test
        FD: ght/rft net/minecraft/Test/log
        FD: ght${'$'}ds/juh Ljava/lang/Object; net/minecraft/Test${'$'}Example/server Ljava/lang/Object;
        MD: ght/hyuip (I)Z net/minecraft/Test/isEven (I)Z
        MD: ght${'$'}ds/hyuip (I)Z net/minecraft/Test${'$'}Example/isOdd (I)Z
    """.trimIndent()

    @Test
    fun testPackage() {
        val mapping = def.mappings.pack("/".asPackage())!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("/", fromName)
        assertEquals("net/minecraft/server", toName)
    }

    @Test
    fun testPackageOpposite() {
        val mapping = def.mappings.pack("net/minecraft/server".asPackage())!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("/", fromName)
        assertEquals("net/minecraft/server", toName)
    }

    @Test
    fun testClass() {
        val from = def.mappings.clazz("ght".asClass())
        val to = def.mappings.clazz("net/minecraft/Test".asClass())

        assertSame(from, to)
    }

    @Test
    fun testField1() {
        val mapping = def.mappings.fieldMapping("ght".asClass().field("rft".asFieldRef()))!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("rft", fromName)
        assertEquals("log", toName)
    }

    @Test
    fun testField2() {
        val mapping =
            def.mappings.fieldMapping(
                "net/minecraft/Test\$Example".asClass()
                    .field("server".asFieldRef("Ljava/lang/Object;".asTypeDef()))
            )!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("juh", fromName)
        assertEquals("server", toName)
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
}
