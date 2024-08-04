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

package com.demonwav.mcdev.toml.platform.forge

import com.demonwav.mcdev.toml.TomlSchemaEntry
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.impl.source.DummyHolderFactory
import com.intellij.psi.util.parentOfType
import javax.swing.Icon
import org.toml.lang.psi.TomlHeaderOwner
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.TomlKeyValueOwner
import org.toml.lang.psi.TomlTableHeader

class ModsTomlDocumentationProvider : DocumentationProvider {

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int,
    ): PsiElement? {
        if (!isModsToml(contextElement)) {
            return null
        }
        // If this is a header get the first key as TomlSchema only remembers the first one
        return contextElement?.parentOfType<TomlTableHeader>()?.key?.segments?.firstOrNull()
            ?: contextElement?.parentOfType<TomlKeySegment>()
    }

    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager,
        entry: Any?,
        element: PsiElement?
    ): PsiElement? {
        if (entry !is TomlSchemaEntry) {
            return null
        }

        val description = entry.description.joinToString("\n")
        return if (description.isNotBlank()) {
            TomlSchemaKeyElement(entry.key, description, psiManager)
        } else {
            null
        }
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (element is TomlSchemaKeyElement) {
            return element.description
        }

        if (element !is TomlKeySegment || !isModsToml(originalElement)) {
            return null
        }

        val key = element.parentOfType<TomlKey>() ?: return null
        val schema = ModsTomlSchema.get(element.project)
        val table = element.parentOfType<TomlKeyValueOwner>()
        val description = when (val parent = key.parent) {
            is TomlTableHeader -> {
                if (element != parent.key?.segments?.firstOrNull()) {
                    return null
                }
                schema.tableSchema(element.text)?.description
            }
            is TomlKeyValue -> when (table) {
                is TomlHeaderOwner -> {
                    val tableName = table.header.key?.segments?.firstOrNull()?.text ?: return null
                    schema.tableEntry(tableName, key.text)?.description
                }
                null -> schema.topLevelEntries.find { it.key == key.text }?.description
                else -> null
            }
            else -> null
        }?.takeUnless { it.isEmpty() } ?: return null
        return DocumentationMarkup.CONTENT_START + description.joinToString("<br>") + DocumentationMarkup.CONTENT_END
    }

    private fun isModsToml(element: PsiElement?): Boolean =
        element?.containingFile?.virtualFile?.name in ForgeTomlConstants.FILE_NAMES

    private class TomlSchemaKeyElement(
        val key: String,
        val description: String,
        val psiManager: PsiManager
    ) : FakePsiElement() {

        private val dummyHolder = DummyHolderFactory.createHolder(psiManager, null)

        override fun getParent(): PsiElement? = dummyHolder

        override fun getManager(): PsiManager? = psiManager

        override fun getPresentation(): ItemPresentation? = object : ItemPresentation {
            override fun getPresentableText(): @NlsSafe String? = key

            override fun getIcon(unused: Boolean): Icon? = null
        }
    }
}
