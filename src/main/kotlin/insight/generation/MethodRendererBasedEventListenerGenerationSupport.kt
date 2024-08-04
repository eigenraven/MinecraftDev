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

package com.demonwav.mcdev.insight.generation

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UDeclaration
import org.jetbrains.uast.getUastParentOfType

abstract class MethodRendererBasedEventListenerGenerationSupport : EventListenerGenerationSupport {

    override fun canGenerate(context: PsiElement, editor: Editor): Boolean {
        if (context.language.id !in MethodRenderer.byLanguage) {
            return false
        }

        return adjustOffset(context, editor) != null
    }

    override fun generateEventListener(
        context: PsiElement,
        listenerName: String,
        eventClass: PsiClass,
        data: GenerationData?,
        editor: Editor
    ) = runWriteAction {
        val document = editor.document

        preGenerationProcess(context, data)
        PsiDocumentManager.getInstance(context.project).doPostponedOperationsAndUnblockDocument(document)

        val renderer = MethodRenderer.byLanguage[context.language.id]!!
        val offset = adjustOffset(context, editor) ?: return@runWriteAction
        val text = invokeRenderer(renderer, context, listenerName, eventClass, data, editor)

        document.insertString(offset, text)
        PsiDocumentManager.getInstance(context.project).commitDocument(document)

        val file = context.containingFile
        editor.caretModel.moveToOffset(offset + text.length - 2)

        EventGenHelper.COLLECTOR.forLanguage(file.language)
            .reformatAndShortenRefs(file, offset, offset + text.length)
    }

    private fun adjustOffset(context: PsiElement, editor: Editor): Int? {
        val declaration = context.getUastParentOfType<UDeclaration>()
        if (declaration == null) {
            return null
        }

        if (declaration is UClass) {
            return editor.caretModel.offset
        }

        return declaration.sourcePsi?.textRange?.endOffset
    }

    protected open fun preGenerationProcess(
        context: PsiElement,
        data: GenerationData?,
    ) = Unit

    protected abstract fun invokeRenderer(
        renderer: MethodRenderer,
        context: PsiElement,
        listenerName: String,
        eventClass: PsiClass,
        data: GenerationData?,
        editor: Editor
    ): String
}
