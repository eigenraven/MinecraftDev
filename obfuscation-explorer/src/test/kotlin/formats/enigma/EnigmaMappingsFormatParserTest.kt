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

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingNamespace
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.asTypeDef
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnigmaMappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = EnigmaMappingsFormatParser()
    @Language("Enigma")
    override val text: String = """
        CLASS net/minecraft/class_2019 net/minecraft/predicate/DamagePredicate
            COMMENT asdfasdf
            COMMENT asdfasdfasdfa
            FIELD field_9520 ANY Lnet/minecraft/class_2019;
            FIELD field_9521 sourceEntity Lnet/minecraft/class_2048;
                COMMENT sdafasdfasdf
            METHOD <init> (Lnet/minecraft/class_2096${'$'}class_2099;Lnet/minecraft/class_2096${'$'}class_2099;Lnet/minecraft/class_2048;Ljava/lang/Boolean;Lnet/minecraft/class_2022;)V
                COMMENT asdfasfasdf
                ARG 1 dealt
                ARG 2 taken
                ARG 3 sourceEntity
                ARG 4 blocked
                ARG 5 type
            METHOD method_8839 fromJson (Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2019;
                ARG 0 json
            CLASS class_2020 Builder
                COMMENT asdfasdfa
                FIELD field_9526 blocked Ljava/lang/Boolean;
                FIELD field_9527 taken Lnet/minecraft/class_2096${'$'}class_2099;
                    COMMENT asdfasdfadsf
                FIELD field_9530 dealt Lnet/minecraft/class_2096${'$'}class_2099;
                METHOD method_8841 blocked (Ljava/lang/Boolean;)Lnet/minecraft/class_2019${'$'}class_2020;
                    COMMENT asfasdf
                    COMMENT asfasdfasdfasdfsad
                    ARG 1 blocked
                METHOD method_8842 type (Lnet/minecraft/class_2022${'$'}class_2023;)Lnet/minecraft/class_2019${'$'}class_2020;
                    ARG 1 builder
                METHOD method_8843 build ()Lnet/minecraft/class_2019;
        CLASS net/minecraft/class_9323 net/minecraft/component/ComponentMap
            FIELD field_49584 EMPTY Lnet/minecraft/class_9323;
            METHOD method_57828 filtered (Ljava/util/function/Predicate;)Lnet/minecraft/class_9323;
                ARG 1 predicate
            METHOD method_57833 stream ()Ljava/util/stream/Stream;
            CLASS class_9324 Builder
                FIELD field_49587 components Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;
                METHOD method_57838 build ()Lnet/minecraft/class_9323;
                METHOD method_57840 add (Lnet/minecraft/class_9331;Ljava/lang/Object;)Lnet/minecraft/class_9323${'$'}class_9324;
                    ARG 1 type
                    ARG 2 value
                METHOD method_58756 put (Lnet/minecraft/class_9331;Ljava/lang/Object;)V
                    ARG 1 type
                    ARG 2 value
                CLASS class_9325 SimpleComponentMap
    """.trimIndent()
        .replace("    ", "\t")

    private val from: MappingNamespace
        get() = MappingNamespace.unnamedFrom(def.mappings)
    private val to: MappingNamespace
        get() = MappingNamespace.unnamedTo(def.mappings)

    @Test
    fun testClasses() {
        assertEquals(5, def.mappings.classes().size)

        val class1 = def.mappings.clazz("net/minecraft/class_2019")!!
        val class2 = def.mappings.clazz("net/minecraft/class_2019\$class_2020")!!
        val class3 = def.mappings.clazz("net/minecraft/class_9323")!!
        val class4 = def.mappings.clazz("net/minecraft/class_9323\$class_9324")!!
        val class5 = def.mappings.clazz("net/minecraft/class_9323\$class_9324\$class_9325")!!

        assertEquals("net/minecraft/class_2019", class1.name(from))
        assertEquals("net/minecraft/predicate/DamagePredicate", class1.name(to))

        assertEquals("net/minecraft/class_2019\$class_2020", class2.name(from))
        assertEquals("net/minecraft/predicate/DamagePredicate\$Builder", class2.name(to))

        assertEquals("net/minecraft/class_9323", class3.name(from))
        assertEquals("net/minecraft/component/ComponentMap", class3.name(to))

        assertEquals("net/minecraft/class_9323\$class_9324", class4.name(from))
        assertEquals("net/minecraft/component/ComponentMap\$Builder", class4.name(to))

        assertEquals("net/minecraft/class_9323\$class_9324\$class_9325", class5.name(from))
        assertEquals("net/minecraft/component/ComponentMap\$Builder\$SimpleComponentMap", class5.name(to))
    }

    @Test
    fun testMethods() {
        assertEquals(2, def.mappings.clazz("net/minecraft/class_2019")?.methods()?.size)
        assertEquals(3, def.mappings.clazz("net/minecraft/class_2019\$class_2020")?.methods()?.size)
        assertEquals(2, def.mappings.clazz("net/minecraft/class_9323")?.methods()?.size)
        assertEquals(3, def.mappings.clazz("net/minecraft/class_9323\$class_9324")?.methods()?.size)
        assertEquals(0, def.mappings.clazz("net/minecraft/class_9323\$class_9324\$class_9325")?.methods()?.size)

        assertEquals("<init>", def.mappings.clazz("net/minecraft/class_2019")?.method("<init>")?.name(to))
        assertEquals("fromJson", def.mappings.clazz("net/minecraft/class_2019")?.method("method_8839")?.name(to))

        assertEquals(
            "blocked",
            def.mappings.clazz("net/minecraft/class_2019\$class_2020")?.method("method_8841")?.name(to)
        )
        assertEquals(
            "type",
            def.mappings.clazz("net/minecraft/class_2019\$class_2020")?.method("method_8842")?.name(to)
        )
        assertEquals(
            "build",
            def.mappings.clazz("net/minecraft/class_2019\$class_2020")?.method("method_8843")?.name(to)
        )

        assertEquals("filtered", def.mappings.clazz("net/minecraft/class_9323")?.method("method_57828")?.name(to))
        assertEquals("stream", def.mappings.clazz("net/minecraft/class_9323")?.method("method_57833")?.name(to))

        assertEquals(
            "build",
            def.mappings.clazz("net/minecraft/class_9323\$class_9324")?.method("method_57838")?.name(to)
        )
        assertEquals(
            "add",
            def.mappings.clazz("net/minecraft/class_9323\$class_9324")?.method("method_57840")?.name(to)
        )
        assertEquals(
            "put",
            def.mappings.clazz("net/minecraft/class_9323\$class_9324")?.method("method_58756")?.name(to)
        )
    }

    @Test
    fun testParams() {
        val class1 = def.mappings.clazz("net/minecraft/class_2019")!!
        val class4 = def.mappings.clazz("net/minecraft/class_9323\$class_9324")!!

        val count = def.mappings.classes()
            .flatMap { it.methods() }
            .flatMap { it.params() }
            .count()
        assertEquals(13, count)

        val names = listOf("dealt", "taken", "sourceEntity", "blocked", "type")
        val params = class1.method("<init>")!!.params()
        for ((i, name) in names.withIndex()) {
            val p = params.first { it.index == (i + 1).asParamIndex() }
            assertEquals(name, p.name(to))
        }

        val names2 = listOf("type", "value")
        val params2 = class4.method("method_57840")!!.params()
        for ((i, name) in names2.withIndex()) {
            val p = params2.first { it.index == (i + 1).asParamIndex() }
            assertEquals(name, p.name(to))
        }
    }

    @Test
    fun testFields() {
        val count = def.mappings.classes()
            .flatMap { it.fields() }
            .count()
        assertEquals(7, count)

        val class1 = def.mappings.clazz("net/minecraft/class_2019")!!

        assertEquals("ANY", class1.field("field_9520")!!.name(to))
        assertEquals("Lnet/minecraft/class_2019;".asTypeDef(), class1.field("field_9520")!!.type)
        assertEquals("field_9521", class1.field("sourceEntity")!!.name(from))
        assertEquals("Lnet/minecraft/class_2048;".asTypeDef(), class1.field("sourceEntity")!!.type)
    }
}
