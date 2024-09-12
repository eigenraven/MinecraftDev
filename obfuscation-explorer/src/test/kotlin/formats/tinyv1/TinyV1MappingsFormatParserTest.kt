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

package io.mcdev.obfex.formats.tinyv1

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingNamespace
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asMethodRef
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TinyV1MappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = TinyV1MappingsFormatParser()

    @Language("TinyV1")
    override val text: String = """
        v1    official    intermediary    test
        CLASS    a    net/minecraft/class_7833    com/example/Test
        FIELD    a    La;    a    field_40713
        FIELD    a    La;    b        abc
        METHOD    a    (F)Lorg/joml/Quaternionf;    a    method_46349
        METHOD    a    (Lorg/joml/Vector3f;F)Lorg/joml/Quaternionf;    a    method_46350 xyz
        METHOD    a    (F)Lorg/joml/Quaternionf;        method_46351
    """.trimIndent()
        .replace("    ", "\t")

    private val official: MappingNamespace
        get() = def.mappings.namespaceOf("official")
    private val intermediary: MappingNamespace
        get() = def.mappings.namespaceOf("intermediary")
    private val test: MappingNamespace
        get() = def.mappings.namespaceOf("test")

    @Test
    fun testClass() {
        val mapping = def.mappings.clazz("a".asClass())!!

        assertEquals(3, mapping.names.size)
        assertEquals("a", mapping.name(official))
        assertEquals("net/minecraft/class_7833", mapping.name(intermediary))
        assertEquals("com/example/Test", mapping.name(test))
    }

    @Test
    fun testField() {
        val clazz = def.mappings.clazz("net/minecraft/class_7833".asClass())!!

        val field = clazz.field("field_40713".asFieldRef("Lnet/minecraft/class_7833;"))!!

        assertEquals(3, field.names.size)
        assertEquals("a", field.name(official))
        assertEquals("field_40713", field.name(intermediary))
        assertNull(field.name(test))
    }

    @Test
    fun testMethod() {
        val clazz = def.mappings.clazz("com/example/Test".asClass())!!

        val method = clazz.method("method_46351".asMethodRef())!!

        assertEquals(3, method.names.size)
        assertNull(method.name(official))
        assertEquals("method_46351", method.name(intermediary))
        assertNull(method.name(test))
    }
}
