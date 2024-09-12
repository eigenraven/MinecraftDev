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

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.unnamedFrom
import io.mcdev.obfex.mappings.unnamedTo
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asMethodRef
import io.mcdev.obfex.ref.asTypeDef
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ProGuardMappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = ProGuardMappingsFormatParser()

    @Language("ProGuard")
    override val text: String = """
        com.mojang.math.Axis -> a:
            com.mojang.math.Axis XN -> a
            com.mojang.math.Axis XP -> b
            com.mojang.math.Axis YN -> c
            17:17:com.mojang.math.Axis of(org.joml.Vector3f) -> abcd
            23:23:org.joml.Quaternionf rotationDegrees(float) -> rotationDegrees
        com.mojang.math.Constants -> b:
            float PI -> a
            float RAD_TO_DEG -> b
            float DEG_TO_RAD -> c
            float EPSILON -> d
            3:3:void <init>() -> <init>
    """.trimIndent()

    @Test
    fun testCounts() {
        assertEquals(2, def.mappings.classes().size)
        assertEquals(7, def.mappings.classes().sumOf { it.fields().size })
        assertEquals(3, def.mappings.classes().sumOf { it.methods().size })
    }

    @Test
    fun testClass() {
        val mapping = def.mappings.clazz("a".asClass())!!

        assertEquals("com/mojang/math/Axis", mapping.unnamedFrom)
        assertEquals("a", mapping.unnamedTo)
    }

    @Test
    fun testFields() {
        val clazz = def.mappings.clazz("a".asClass())!!

        val axis = "Lcom/mojang/math/Axis;"
        val xn = clazz.field("XN".asFieldRef(axis.asTypeDef()))!!
        val xp = clazz.field("b".asFieldRef(axis.asTypeDef()))!!
        val yn = clazz.field("YN".asFieldRef(axis.asTypeDef()))!!

        assertEquals("XN", xn.unnamedFrom)
        assertEquals("a", xn.unnamedTo)
        assertEquals("XP", xp.unnamedFrom)
        assertEquals("b", xp.unnamedTo)
        assertEquals("YN", yn.unnamedFrom)
        assertEquals("c", yn.unnamedTo)
    }

    @Test
    fun testMethods() {
        val clazz = def.mappings.clazz("com.mojang.math.Axis".asClass())!!

        val mapping = clazz.method("of".asMethodRef("(Lorg/joml/Vector3f;)Lcom/mojang/math/Axis;"))!!

        assertEquals("of", mapping.unnamedFrom)
        assertEquals("abcd", mapping.unnamedTo)
    }

    @Test
    fun testInitSkipped() {
        val clazz = def.mappings.clazz("com.mojang.math.Constants".asClass())!!
        val mapping = clazz.method("<init>".asMethodRef("()V"))

        assertNull(mapping)
    }
}
