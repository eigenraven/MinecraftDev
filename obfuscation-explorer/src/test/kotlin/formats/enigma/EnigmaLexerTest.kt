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

import com.intellij.testFramework.LexerTestCase
import io.mcdev.obfex.filterCrlf
import io.mcdev.obfex.formats.enigma.lang.EnigmaLayoutLexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Enigma Lexing Test")
class EnigmaLexerTest {

    @Test
    @DisplayName("Enigma Lexing Test")
    fun test() {
        val caller = EnigmaLexerTest::class.java
        val basePath = "lexer/fixtures/test.mapping"
        val text = caller.getResource(basePath)?.readText()?.trim() ?: Assertions.fail("no test data found")

        val expected = caller.getResource("${basePath.substringBeforeLast('.')}.txt")?.readText()?.trim()
            ?: Assertions.fail("no expected data found")
        val actual = LexerTestCase.printTokens(text.filter { it != '\r' }, 0, EnigmaLayoutLexer()).trim()

            Assertions.assertEquals(expected.filterCrlf(), actual.filterCrlf())
        }
    }
    