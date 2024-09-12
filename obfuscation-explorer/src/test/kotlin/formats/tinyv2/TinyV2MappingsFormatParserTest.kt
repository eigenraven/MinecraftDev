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

package io.mcdev.obfex.formats.tinyv2

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingNamespace
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.ref.LvtIndex
import io.mcdev.obfex.ref.PrimitiveTypeDef
import io.mcdev.obfex.ref.asLocal
import io.mcdev.obfex.ref.asLvtIndex
import io.mcdev.obfex.ref.asParamIndex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TinyV2MappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = TinyV2MappingsFormatParser()

    override val text: String = """
        tiny    2    0    source    target    target2
        c    class_1    class1Ns0Rename    class1Ns1Rename
            f    I    field_1    field1Ns0Rename    field1Ns1Rename
            m    ()I    method_1    method1Ns0Rename    method1Ns1Rename
                p    1    param_1    param1Ns0Rename    param1Ns1Rename
                v    2    3    4    var_1    var1Ns0Rename    var1Ns1Rename
                v    5    6    var_2    var2Ns0Rename    var2Ns1Rename
        c    class_1${'$'}class_2    class1Ns0Rename${'$'}class2Ns0Rename    class1Ns1Rename${'$'}class2Ns1Rename
            f    I    field_2    field2Ns0Rename    field2Ns1Rename
        c    class_3    class3Ns0Rename    class3Ns1Rename
    """.trimIndent()
        .replace("    ", "\t")

    private val source: MappingNamespace
        get() = def.mappings.namespaceOf("source")
    private val target: MappingNamespace
        get() = def.mappings.namespaceOf("target")
    private val target2: MappingNamespace
        get() = def.mappings.namespaceOf("target2")

    @Test
    fun testClasses() {
        assertEquals(3, def.mappings.classes().count())

        val class1 = def.mappings.clazz("class1Ns0Rename")!!
        assertEquals("class_1", class1.name(source))
        assertEquals("class1Ns0Rename", class1.name(target))
        assertEquals("class1Ns1Rename", class1.name(target2))

        val class2 = def.mappings.clazz("class3Ns1Rename")!!
        assertEquals("class_3", class2.name(source))
        assertEquals("class3Ns0Rename", class2.name(target))
        assertEquals("class3Ns1Rename", class2.name(target2))
    }

    @Test
    fun testField() {
        val class1 = def.mappings.clazz("class_1")!!
        val field = class1.fields().single()

        assertEquals("field_1", field.name(source))
        assertEquals("field1Ns0Rename", field.name(target))
        assertEquals("field1Ns1Rename", field.name(target2))
        assertEquals(PrimitiveTypeDef.INT, field.type)
    }

    @Test
    fun testMethod() {
        val class1 = def.mappings.clazz("class1Ns1Rename")!!
        val method = class1.methods().single()

        assertEquals("method_1", method.name(source))
        assertEquals("method1Ns0Rename", method.name(target))
        assertEquals("method1Ns1Rename", method.name(target2))
    }

    @Test
    fun testParam() {
        val method = def.mappings.clazz("class1Ns1Rename")!!.methods().single()
        val param = method.params().single()

        assertEquals(1.asParamIndex(), param.index)
        assertEquals("param_1", param.name(source))
        assertEquals("param1Ns0Rename", param.name(target))
        assertEquals("param1Ns1Rename", param.name(target2))
    }

    @Test
    fun testLocalVar() {
        val method = def.mappings.clazz("class1Ns1Rename")!!.methods().single()
        val firstLocal = method.localVars().single { it.lvtIndex == 4.asLvtIndex() }
        val secondLocal = method.localVars().single { it.localVarIndex.index == 5.asLocal() }

        assertEquals(2.asLocal(startIndex = 3.asLocal()), firstLocal.localVarIndex)
        assertEquals(4.asLvtIndex(), firstLocal.lvtIndex)
        assertEquals("var_1", firstLocal.name(source))
        assertEquals("var1Ns0Rename", firstLocal.name(target))
        assertEquals("var1Ns1Rename", firstLocal.name(target2))

        assertEquals(5.asLocal(startIndex = 6.asLocal()), secondLocal.localVarIndex)
        assertEquals(LvtIndex.UNKNOWN, secondLocal.lvtIndex)
        assertEquals("var_2", secondLocal.name(source))
        assertEquals("var2Ns0Rename", secondLocal.name(target))
        assertEquals("var2Ns1Rename", secondLocal.name(target2))
    }
}
