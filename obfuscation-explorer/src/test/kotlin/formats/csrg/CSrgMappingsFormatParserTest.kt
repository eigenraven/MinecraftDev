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

package io.mcdev.obfex.formats.csrg

import io.mcdev.obfex.formats.ParserFixture
import io.mcdev.obfex.mappings.MappingsFormatParser
import io.mcdev.obfex.mappings.unnamedFrom
import io.mcdev.obfex.mappings.unnamedTo
import io.mcdev.obfex.ref.ClassTypeDef
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.PrimitiveTypeDef
import io.mcdev.obfex.ref.asClass
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asMethodRef
import io.mcdev.obfex.ref.field
import io.mcdev.obfex.ref.method
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CSrgMappingsFormatParserTest : ParserFixture() {

    override val parser: MappingsFormatParser = CSrgMappingsFormatParser()

    @Language("csrg")
    override val text: String = """
        net/minecraft/CommentTest uih
        net/minecraft/Test${'$'}Example ght${'$'}ds
        net/minecraft/tags/TagsItem af CANDLES
        net/minecraft/tags/TagsItem ag READ
        com/mojang/math/Divisor a (II)Ljava/lang/Iterable; asIterable
        net/minecraft/BlockUtil a (Ljava/util/function/Predicate;Lnet/minecraft/core/BlockPosition${'$'}MutableBlockPosition;Lnet/minecraft/core/EnumDirection;I)I getLimit
        net/minecraft/BlockUtil a (Lnet/minecraft/core/BlockPosition;Lnet/minecraft/core/EnumDirection${'$'}EnumAxis;ILnet/minecraft/core/EnumDirection${'$'}EnumAxis;ILjava/util/function/Predicate;)Lnet/minecraft/BlockUtil${'$'}Rectangle; getLargestRectangleAround
        net/minecraft/BlockUtil a (Lnet/minecraft/world/level/IBlockAccess;Lnet/minecraft/core/BlockPosition;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/EnumDirection;Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional; getTopConnectedBlock
    """.trimIndent()

    @Test
    fun testField1() {
        val mapping = def.mappings.fieldMapping(
            "net.minecraft.tags.TagsItem".asClass().field("af".asFieldRef()),
        )!!
        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("af", fromName)
        assertEquals("CANDLES", toName)
    }

    @Test
    fun testField2() {
        val mapping = def.mappings.fieldMapping(
            "net.minecraft.tags.TagsItem".asClass().field("ag".asFieldRef()),
        )!!
        val fromName = mapping.unnamedFrom
        val toName = mapping.unnamedTo

        assertEquals("ag", fromName)
        assertEquals("READ", toName)
    }

    @Test
    fun testClass() {
        val clazz = def.mappings.clazz(
            "net.minecraft.CommentTest".asClass(),
        )!!

        val fromName = clazz.unnamedFrom
        val toName = clazz.unnamedTo

        assertEquals("net/minecraft/CommentTest", fromName)
        assertEquals("uih", toName)
    }

    @Test
    fun testMethod() {
        val desc = MethodDescriptor(
            listOf(
                ClassTypeDef("net/minecraft/core/BlockPosition".asClass()),
                ClassTypeDef("net/minecraft/core/EnumDirection\$EnumAxis".asClass()),
                PrimitiveTypeDef.INT,
                ClassTypeDef("net/minecraft/core/EnumDirection\$EnumAxis".asClass()),
                PrimitiveTypeDef.INT,
                ClassTypeDef("java/util/function/Predicate".asClass()),
            ),
            ClassTypeDef("net.minecraft.BlockUtil\$Rectangle".asClass())
        )

        val method = def.mappings.methodMapping(
            "net.minecraft.BlockUtil".asClass().method("a".asMethodRef(desc)),
        )!!

        val fromName = method.unnamedFrom
        val toName = method.unnamedTo

        assertEquals("a", fromName)
        assertEquals("getLargestRectangleAround", toName)
    }

    @Test
    fun testMethodOtherSide() {
        val desc = MethodDescriptor(
            listOf(
                ClassTypeDef("net/minecraft/world/level/IBlockAccess".asClass()),
                ClassTypeDef("net/minecraft/core/BlockPosition".asClass()),
                ClassTypeDef("net/minecraft/world/level/block/Block".asClass()),
                ClassTypeDef("net/minecraft/core/EnumDirection".asClass()),
                ClassTypeDef("net/minecraft/world/level/block/Block".asClass()),
            ),
            ClassTypeDef("java/util/Optional".asClass())
        )

        val method = def.mappings.methodMapping(
            "net.minecraft.BlockUtil".asClass().method("getTopConnectedBlock".asMethodRef(desc)),
        )!!

        val fromName = method.unnamedFrom
        val toName = method.unnamedTo

        assertEquals("a", fromName)
        assertEquals("getTopConnectedBlock", toName)
    }

    @Test
    fun testCounts() {
        assertEquals(5, def.mappings.classes().size)
        assertEquals(2, def.mappings.classes().sumOf { it.fields().size })
        assertEquals(4, def.mappings.classes().sumOf { it.methods().size })
    }

    @Test
    fun testWarnings() {
        // Missing class mapping warnings
        assertEquals(3, def.source.warnings.size)
    }
}
