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

package io.mcdev.obfex.formats.tsrg2

import io.mcdev.obfex.Tristate
import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingNamespace
import io.mcdev.obfex.mappings.MappingsFormatParser
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TSrg2MappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = TSrg2MappingsFormatParser()

    @Language("tsrg2")
    override val text: String = """
        tsrg2 left right
        com/mojang/blaze3d/Blaze3D com/mojang/blaze3d/Blaze3D
            <init> ()V <init>
            getTime ()D m_83640_
                static
            process (Lcom/mojang/blaze3d/pipeline/RenderPipeline;F)V m_166118_
                static
                0 p_166119_ p_166119_
                1 p_166120_ p_166120_
        com/mojang/blaze3d/audio/Channel com/mojang/blaze3d/audio/ChannelTest
            BUFFER_DURATION_SECONDS f_166124_
            LOGGER f_83641_
            QUEUED_BUFFER_COUNT f_166125_
            <clinit> ()V <clinit>
                static
            <init> (I)V <init>
                0 p_83648_ p_83648_
            attachBufferStream (Lnet/minecraft/client/sounds/AudioStream;)V m_83658_
                0 p_83659_ p_83659_
            initialized f_83643_
            attachStaticBuffer (Lcom/mojang/blaze3d/audio/SoundBuffer;)V m_83656_
                0 p_83657_ TEST
            calculateBufferSize (Ljavax/sound/sampled/AudioFormat;I)I m_83660_
                static
                0 p_83661_ p_83661_test
                1 p_83662_ p_83662_test
    """.trimIndent()
        .replace("    ", "\t")

    private val left: MappingNamespace
        get() = def.mappings.namespaceOf("left")
    private val right: MappingNamespace
        get() = def.mappings.namespaceOf("right")

    @Test
    fun testNamespaces() {
        assertEquals(2, def.mappings.namespaces.size)
        assertEquals("left", def.mappings.namespaces[0].name)
        assertEquals("right", def.mappings.namespaces[1].name)
    }

    @Test
    fun testClasses() {
        assertEquals(2, def.mappings.classes().size)
        val blaze3d = def.mappings.clazz("com.mojang.blaze3d.Blaze3D")
        val channel = def.mappings.clazz("com.mojang.blaze3d.audio.Channel")

        assertNotNull(blaze3d, "blaze3d")
        assertNotNull(channel, "channel")

        assertEquals("com/mojang/blaze3d/audio/ChannelTest", channel!!.name(right))
    }

    @Test
    fun testMethods() {
        val method1 = def.mappings.clazz("com.mojang.blaze3d.Blaze3D")!!
            .method("getTime")!!

        assertEquals("getTime", method1.name(left))
        assertEquals("m_83640_", method1.name(right))

        val method2 = def.mappings.clazz("com.mojang.blaze3d.Blaze3D")!!
            .method("process")!!

        assertEquals("process", method2.name(left))
        assertEquals("m_166118_", method2.name(right))

        val method3 = def.mappings.clazz("com/mojang/blaze3d/audio/Channel")!!
            .method("m_83658_")!!

        assertEquals("attachBufferStream", method3.name(left))
        assertEquals("m_83658_", method3.name(right))
    }

    @Test
    fun testStatics() {
        val method1 = def.mappings.clazz("com.mojang.blaze3d.Blaze3D")!!
            .method("getTime")!!
        assertEquals(Tristate.TRUE, method1.metadata.isStatic)

        val method2 = def.mappings.clazz("com/mojang/blaze3d/audio/Channel")!!
            .method("<clinit>")!!
        assertEquals(Tristate.TRUE, method2.metadata.isStatic)

        val method3 = def.mappings.clazz("com/mojang/blaze3d/audio/Channel")!!
            .method("<init>")!!
        assertEquals(Tristate.FALSE, method3.metadata.isStatic)
    }

    @Test
    fun testParams() {
        val method1 = def.mappings.clazz("com.mojang.blaze3d.Blaze3D")!!
            .method("getTime")!!

        assertEquals(0, method1.params().size)

        val method2 = def.mappings.clazz("com/mojang/blaze3d/audio/Channel")!!
            .method("attachStaticBuffer")!!

        assertEquals(1, method2.params().size)
        assertEquals("p_83657_", method2.param(0)!!.name(left))
        assertEquals("TEST", method2.param(0)!!.name(right))

        val method3 = def.mappings.clazz("com/mojang/blaze3d/audio/ChannelTest")!!
            .method("calculateBufferSize")!!

        assertEquals(Tristate.TRUE, method3.metadata.isStatic)
        assertEquals(2, method3.params().size)
        assertEquals("p_83661_", method3.param(0)!!.name(left))
        assertEquals("p_83661_test", method3.param(0)!!.name(right))
        assertEquals("p_83662_", method3.param(1)!!.name(left))
        assertEquals("p_83662_test", method3.param(1)!!.name(right))
    }

    @Test
    fun testFields() {
        val c = def.mappings.clazz("com/mojang/blaze3d/audio/Channel")!!

        assertEquals(4, c.fields().size)
        assertEquals("BUFFER_DURATION_SECONDS", c.field("f_166124_")!!.name(left))
        assertEquals("f_166125_", c.field("QUEUED_BUFFER_COUNT")!!.name(right))
        assertEquals("initialized", c.field("f_83643_")!!.name(left))
    }
}
