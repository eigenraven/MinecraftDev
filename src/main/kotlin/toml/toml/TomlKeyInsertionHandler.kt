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

package com.demonwav.mcdev.toml.toml

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiDocumentManager
import org.toml.lang.psi.TomlElementTypes
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.ext.elementType

/** Inserts `=` after the completed key if missing and invokes the completion popup for the value automatically */
class TomlKeyInsertionHandler(private val keyValue: TomlKeyValue) : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val hasEq = keyValue.children.any { it.elementType == TomlElementTypes.EQ }
        if (!hasEq) {
            context.document.insertString(context.tailOffset, " = ")
            PsiDocumentManager.getInstance(context.project).commitDocument(context.document)
            context.editor.caretModel.moveToOffset(context.tailOffset) // The tail offset is tracked automatically
            AutoPopupController.getInstance(context.project).scheduleAutoPopup(context.editor)
        }
    }
}
