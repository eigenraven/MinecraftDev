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

package com.demonwav.mcdev.platform.forge

import com.demonwav.mcdev.framework.BaseMinecraftTest
import com.demonwav.mcdev.toml.platform.forge.ModsTomlDocumentationProvider
import com.intellij.testFramework.UsefulTestCase.assertSameLines
import com.intellij.util.application
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Mods Toml Documentation Tests")
class ModsTomlDocumentationTest : BaseMinecraftTest() {

    private fun doTestDoc(
        @Language("TOML") modsToml: String,
        expectedDoc: String
    ) {
        fixture.configureByText("mods.toml", modsToml)

        application.runReadAction {
            val originalElement = fixture.elementAtCaret
            val provider = ModsTomlDocumentationProvider()
            val element = provider.getCustomDocumentationElement(
                fixture.editor,
                fixture.file,
                originalElement,
                fixture.caretOffset
            ) ?: originalElement

            val doc = provider.generateDoc(element, originalElement)
            assertNotNull(doc)
            assertSameLines(expectedDoc, doc!!)
        }
    }

    private fun doTestCompletionDoc(
        @Language("TOML") modsToml: String,
        lookupString: String,
        expectedDoc: String
    ) {
        fixture.configureByText("mods.toml", modsToml)

        val lookupElements = fixture.completeBasic()
        val licenseElement = lookupElements.find { it.lookupString == lookupString }
        assertNotNull(licenseElement)

        val obj = licenseElement!!.`object`
        assertNotNull(obj)

        val documentationProvider = ModsTomlDocumentationProvider()
        val docElement = documentationProvider.getDocumentationElementForLookupItem(fixture.psiManager, obj, null)
        assertNotNull(docElement)

        val doc = documentationProvider.generateDoc(docElement!!, null)
        assertNotNull(doc)
        assertSameLines(expectedDoc, doc!!)
    }

    @Test
    @DisplayName("Root Key Documentation")
    fun rootKeyDocumentation() {
        doTestDoc(
            "licen<caret>se='MIT'",
            "<div class='content'>The license for you mod. This is mandatory metadata and allows for" +
                " easier comprehension of your redistributive properties.<br>Review your options at" +
                " <a href=\"https://choosealicense.com\">choosealicense.com</a>. All rights reserved is the default" +
                " copyright stance, and is thus the default here.</div>"
        )
    }

    @Test
    @DisplayName("Mods Header Documentation")
    fun modsHeaderDocumentation() {
        doTestDoc(
            "[[mo<caret>ds]]",
            "<div class='content'>A list of mods - how many allowed here is determined by the individual" +
                " mod loader</div>"
        )
    }

    @Test
    @DisplayName("Mod Key Documentation")
    fun modKeyDocumentation() {
        doTestDoc(
            """
            [[mods]]
            ver<caret>sion="1.0"
            """.trimIndent(),
            "<div class='content'>The version number of the mod - there's a few well known variables usable here or" +
                " just hardcode it.<br>\${file.jarVersion} will substitute the value of the Implementation-Version" +
                " as read from the mod's JAR file metadata</div>"
        )
    }

    @Test
    @DisplayName("Dependency Key Documentation")
    fun dependencyKeyDocumentation() {
        doTestDoc(
            """
            [[dependencies."${'$'}{mod_id}"]]
            order<caret>ing="NONE"
            """.trimIndent(),
            "<div class='content'>An ordering relationship for the dependency - BEFORE or AFTER required if" +
                " the relationship is not mandatory.</div>"
        )
    }

    @Test
    @DisplayName("Root Key Lookup Element Documentation")
    fun rootKeyLookupElementDocumentation() {
        doTestCompletionDoc(
            "<caret>",
            "license",
            "<div class='content'>The license for you mod. This is mandatory metadata and allows for" +
                " easier comprehension of your redistributive properties.<br>Review your options at" +
                " <a href=\"https://choosealicense.com\">choosealicense.com</a>. All rights reserved is the default" +
                " copyright stance, and is thus the default here.</div>"
        )
    }
}
