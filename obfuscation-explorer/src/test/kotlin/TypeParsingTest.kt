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

package io.mcdev.obfex

import io.mcdev.obfex.ref.ArrayTypeDef
import io.mcdev.obfex.ref.ClassTypeDef
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.PrimitiveTypeDef
import io.mcdev.obfex.ref.ReturnTypeDef
import io.mcdev.obfex.ref.VoidTypeDef
import io.mcdev.obfex.ref.asClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TypeParsingTest {

    @Test
    fun testPrimitiveTypes() {
        assertEquals(PrimitiveTypeDef.BOOLEAN, ReturnTypeDef.parse("Z"))
        assertEquals(PrimitiveTypeDef.CHAR, ReturnTypeDef.parse("C"))
        assertEquals(PrimitiveTypeDef.BYTE, ReturnTypeDef.parse("B"))
        assertEquals(PrimitiveTypeDef.SHORT, ReturnTypeDef.parse("S"))
        assertEquals(PrimitiveTypeDef.INT, ReturnTypeDef.parse("I"))
        assertEquals(PrimitiveTypeDef.LONG, ReturnTypeDef.parse("J"))
        assertEquals(PrimitiveTypeDef.FLOAT, ReturnTypeDef.parse("F"))
        assertEquals(PrimitiveTypeDef.DOUBLE, ReturnTypeDef.parse("D"))
        assertEquals(VoidTypeDef, ReturnTypeDef.parse("V"))
    }

    @Test
    fun testArrayPrimitiveTypes() {
        repeat(10) { i ->
            val dim = i + 1
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.BOOLEAN, dim), ReturnTypeDef.parse("[".repeat(dim) + 'Z'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.CHAR, dim), ReturnTypeDef.parse("[".repeat(dim) + 'C'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.BYTE, dim), ReturnTypeDef.parse("[".repeat(dim) + 'B'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.SHORT, dim), ReturnTypeDef.parse("[".repeat(dim) + 'S'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.INT, dim), ReturnTypeDef.parse("[".repeat(dim) + 'I'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.LONG, dim), ReturnTypeDef.parse("[".repeat(dim) + 'J'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.FLOAT, dim), ReturnTypeDef.parse("[".repeat(dim) + 'F'))
            assertEquals(ArrayTypeDef(PrimitiveTypeDef.DOUBLE, dim), ReturnTypeDef.parse("[".repeat(dim) + 'D'))
        }
    }

    @Test
    fun testVoidArray() {
        assertNull(ReturnTypeDef.parse("[V"))
    }

    @Test
    fun testClassType() {
        assertEquals(ClassTypeDef("java/lang/String".asClass()), ReturnTypeDef.parse("Ljava/lang/String;"))
        assertEquals(ClassTypeDef("a".asClass()), ReturnTypeDef.parse("La;"))
    }

    @Test
    fun testClassArray() {
        assertEquals(
            ArrayTypeDef(ClassTypeDef("java/lang/String".asClass()), 1),
            ReturnTypeDef.parse("[Ljava/lang/String;")
        )
        assertEquals(
            ArrayTypeDef(ClassTypeDef("java/lang/String".asClass()), 3),
            ReturnTypeDef.parse("[[[Ljava/lang/String;")
        )
        assertEquals(ArrayTypeDef(ClassTypeDef("a".asClass()), 2), ReturnTypeDef.parse("[[La;"))
    }

    @Test
    fun testEmptyMethodDesc() {
        assertEquals(MethodDescriptor(emptyList(), VoidTypeDef), MethodDescriptor.parse("()V"))
    }

    @Test
    fun testPrimitiveMethodDesc() {
        assertEquals(MethodDescriptor(listOf(PrimitiveTypeDef.INT), VoidTypeDef), MethodDescriptor.parse("(I)V"))
        assertEquals(
            MethodDescriptor(listOf(PrimitiveTypeDef.INT, PrimitiveTypeDef.INT, PrimitiveTypeDef.INT), VoidTypeDef),
            MethodDescriptor.parse("(III)V")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    PrimitiveTypeDef.CHAR,
                    PrimitiveTypeDef.BOOLEAN,
                    PrimitiveTypeDef.BYTE,
                    PrimitiveTypeDef.SHORT,
                    PrimitiveTypeDef.INT,
                    PrimitiveTypeDef.LONG,
                    PrimitiveTypeDef.FLOAT,
                    PrimitiveTypeDef.DOUBLE,
                ),
                VoidTypeDef
            ),
            MethodDescriptor.parse("(CZBSIJFD)V")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    PrimitiveTypeDef.CHAR,
                    PrimitiveTypeDef.BOOLEAN,
                    PrimitiveTypeDef.BYTE,
                    PrimitiveTypeDef.SHORT,
                    PrimitiveTypeDef.INT,
                    PrimitiveTypeDef.LONG,
                    PrimitiveTypeDef.FLOAT,
                    PrimitiveTypeDef.DOUBLE,
                ),
                PrimitiveTypeDef.CHAR
            ),
            MethodDescriptor.parse("(CZBSIJFD)C")
        )
    }

    @Test
    fun testArrayMethodDesc() {
        assertEquals(
            MethodDescriptor(listOf(ArrayTypeDef(PrimitiveTypeDef.LONG, 2)), ArrayTypeDef(PrimitiveTypeDef.FLOAT, 1)),
            MethodDescriptor.parse("([[J)[F")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    ArrayTypeDef(PrimitiveTypeDef.LONG, 2),
                    ArrayTypeDef(PrimitiveTypeDef.INT, 1),
                    ArrayTypeDef(PrimitiveTypeDef.BOOLEAN, 3),
                ),
                ArrayTypeDef(PrimitiveTypeDef.FLOAT, 1)
            ),
            MethodDescriptor.parse("([[J[I[[[Z)[F")
        )
    }

    @Test
    fun testClassMethodDesc() {
        assertEquals(
            MethodDescriptor(
                listOf(
                    ClassTypeDef("a".asClass()),
                    ClassTypeDef("b".asClass()),
                    ClassTypeDef("c".asClass()),
                ),
                ClassTypeDef("d".asClass())
            ),
            MethodDescriptor.parse("(La;Lb;Lc;)Ld;")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    ClassTypeDef("java.lang.String".asClass()),
                    ClassTypeDef("java.lang.Object".asClass()),
                ),
                ClassTypeDef("d".asClass())
            ),
            MethodDescriptor.parse("(Ljava/lang/String;Ljava/lang/Object;)Ld;")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    ClassTypeDef("java.lang.String".asClass()),
                    ArrayTypeDef(ClassTypeDef("java.lang.Object".asClass()), 2),
                ),
                ArrayTypeDef(ClassTypeDef("d".asClass()), 1)
            ),
            MethodDescriptor.parse("(Ljava/lang/String;[[Ljava/lang/Object;)[Ld;")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    ClassTypeDef("java.lang.String".asClass()),
                    ArrayTypeDef(ClassTypeDef("java.lang.Object".asClass()), 2),
                    PrimitiveTypeDef.INT,
                    PrimitiveTypeDef.INT,
                    PrimitiveTypeDef.INT,
                ),
                VoidTypeDef
            ),
            MethodDescriptor.parse("(Ljava/lang/String;[[Ljava/lang/Object;III)V")
        )

        assertEquals(
            MethodDescriptor(
                listOf(
                    ClassTypeDef("java.lang.String".asClass()),
                    PrimitiveTypeDef.LONG,
                    ArrayTypeDef(ClassTypeDef("java.lang.Object".asClass()), 2),
                    PrimitiveTypeDef.INT,
                    ArrayTypeDef(PrimitiveTypeDef.INT, 1),
                    PrimitiveTypeDef.INT,
                ),
                VoidTypeDef
            ),
            MethodDescriptor.parse("(Ljava/lang/String;J[[Ljava/lang/Object;I[II)V")
        )
    }
}
