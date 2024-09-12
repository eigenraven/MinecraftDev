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

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingNamespace
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.unnamedFrom
import io.mcdev.obfex.mappings.unnamedTo
import io.mcdev.obfex.ref.ClassTypeDef
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asMethodDesc
import io.mcdev.obfex.ref.asMethodRef
import io.mcdev.obfex.ref.asTypeDef
import io.mcdev.obfex.ref.field
import io.mcdev.obfex.ref.method
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TSrgMappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = TSrgMappingsFormatParser()

    @Language("tsrg")
    override val text: String = """
        a net/minecraft/util/text/TextFormatting
            a BLACK
            b DARK_BLUE
            c DARK_GREEN
            d DARK_AQUA
            e DARK_RED
            a (C)La; func_211165_a
            a (I)La; func_175744_a
            a (La;)La; func_199747_a
        b net/minecraft/crash/CrashReport
            a field_147150_a
            b field_71513_a
            a ()Ljava/lang/String; func_71501_a
            a (Ljava/io/File;)Z func_147149_a
    """.trimIndent()
        .replace("    ", "\t")

    @Test
    fun testCounts() {
        assertEquals(2, def.mappings.classes().size)
        val clazzA = def.mappings.clazz("a".asClass())!!
        val clazzB = def.mappings.clazz("b".asClass())!!

        assertEquals(5, clazzA.fields().size)
        assertEquals(3, clazzA.methods().size)
        assertEquals(2, clazzB.fields().size)
        assertEquals(2, clazzB.methods().size)
    }

    @Test
    fun testTypeMapping() {
        val mapping = def.mappings.clazz("a".asClass())!!

        val aMethods = mapping.methods().filter { it.unnamedFrom == "a" }

        val returnType = "Lnet/minecraft/util/text/TextFormatting;".asTypeDef()

        for (aMethod in aMethods) {
            assertEquals(
                returnType,
                def.mappings.mapTypeTo(MappingNamespace.unnamedTo(def.mappings), aMethod.descriptor.returnType)
            )
        }
    }

    @Test
    fun testByMappedType() {
        val ref = "net/minecraft/util/text/TextFormatting".asClass()
            .method("func_211165_a".asMethodRef("(C)Lnet/minecraft/util/text/TextFormatting;".asMethodDesc()))
        val mapping = def.mappings.methodMapping(ref)

        assertNotNull(mapping)
    }

    @Test
    fun testField() {
        val mapping = def.mappings.fieldMapping("a".asClass().field("a".asFieldRef()))!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("a", fromName)
        assertEquals("BLACK", toName)
    }

    @Test
    fun testMethod() {
        val ref = "b".asClass()
            .method(
                "a".asMethodRef(
                    MethodDescriptor(
                        listOf(),
                        ClassTypeDef("java/lang/String".asClass())
                    )
                )
            )

        val mapping = def.mappings.methodMapping(ref)!!

        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("a", fromName)
        assertEquals("func_71501_a", toName)
    }
}
