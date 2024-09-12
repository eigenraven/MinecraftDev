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

package io.mcdev.obfex.formats.srg

import com.intellij.lexer.FlexAdapter
import com.intellij.testFramework.LexerTestCase
import io.mcdev.obfex.filterCrlf
import io.mcdev.obfex.formats.srg.gen.SrgLexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("SRG Lexing Tests")
class SrgLexerTest {

    @Test
    @DisplayName("SRG Lexing Test")
    fun test() {
        val caller = SrgLexerTest::class.java
        val basePath = "lexer/fixtures/test.srg"
        val text = caller.getResource(basePath)?.readText()?.trim() ?: Assertions.fail("no test data found")

        val expected = caller.getResource("${basePath.substringBeforeLast('.')}.txt")?.readText()?.trim()
            ?: Assertions.fail("no expected data found")
        val actual = LexerTestCase.printTokens(text.filter { it != '\r' }, 0, FlexAdapter(SrgLexer())).trim()

            Assertions.assertEquals(expected.filterCrlf(), actual.filterCrlf())
        }
    }
    