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

package io.mcdev.obfex.formats

import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.testFramework.ReadOnlyLightVirtualFile
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import io.mcdev.obfex.mappings.MappingsDefinition
import io.mcdev.obfex.mappings.MappingsFormatParser
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ParserFixture {

    private val fixture = IdeaTestFixtureFactory.getFixtureFactory().createBareFixture()

    lateinit var def: MappingsDefinition

    abstract val parser: MappingsFormatParser

    abstract val text: String

    @BeforeAll
    fun setup() {
        fixture.setUp()

        val file = ReadOnlyLightVirtualFile(
            "test.${parser.expectedFileExtensions.first()}",
            PlainTextLanguage.INSTANCE,
            text
        )

        def = parser.parse(file)!!
    }

    @AfterAll
    fun teardown() {
        fixture.tearDown()
    }
}
