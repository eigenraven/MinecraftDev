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

package io.mcdev.obfex.formats.csrg.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.csrg.gen.psi.CSrgClassMapping
import io.mcdev.obfex.formats.csrg.gen.psi.CSrgFieldMapping
import io.mcdev.obfex.formats.csrg.gen.psi.CSrgMethodMapping
import io.mcdev.obfex.formats.util.registerHighlight

class CSrgAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is CSrgClassMapping -> {
                registerHighlight(element.obfName, CSrgSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.deobfName, CSrgSyntaxHighlighter.CLASS_NAME, holder, true)
            }
            is CSrgMethodMapping -> {
                registerHighlight(element.obfClassName, CSrgSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.obfMethodName, CSrgSyntaxHighlighter.METHOD, holder)
                registerHighlight(element.deobfMethodName, CSrgSyntaxHighlighter.METHOD, holder, true)
            }
            is CSrgFieldMapping -> {
                registerHighlight(element.obfClassName, CSrgSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.obfFieldName, CSrgSyntaxHighlighter.FIELD, holder)
                registerHighlight(element.deobfName, CSrgSyntaxHighlighter.FIELD, holder, true)
            }
        }
    }
}
