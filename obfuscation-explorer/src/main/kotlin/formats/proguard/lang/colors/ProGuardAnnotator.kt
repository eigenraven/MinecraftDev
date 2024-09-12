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

package io.mcdev.obfex.formats.proguard.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.proguard.gen.psi.ProGuardClassMapping
import io.mcdev.obfex.formats.proguard.gen.psi.ProGuardFieldName
import io.mcdev.obfex.formats.proguard.gen.psi.ProGuardMethodName
import io.mcdev.obfex.formats.proguard.gen.psi.ProGuardTypeDesc
import io.mcdev.obfex.formats.util.registerHighlight

class ProGuardAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is ProGuardClassMapping) {
            for (identifier in element.identifierList) {
                registerHighlight(identifier, ProGuardSyntaxHighlighter.CLASS_NAME, holder)
            }
            return
        }
        if (element is ProGuardTypeDesc) {
            element.identifier?.let { ident ->
                registerHighlight(ident, ProGuardSyntaxHighlighter.CLASS_NAME, holder)
            }
        }

        val key = when (element) {
            is ProGuardMethodName -> ProGuardSyntaxHighlighter.METHOD
            is ProGuardFieldName -> ProGuardSyntaxHighlighter.FIELD
            else -> return
        }

        registerHighlight(element, key, holder)
    }
}
