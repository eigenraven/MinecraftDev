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

package io.mcdev.obfex.formats.tinyv1.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1ClassMapping
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1FieldMapping
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1MappingPart
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1MethodMapping
import io.mcdev.obfex.formats.util.registerHighlight

class TinyV1Annotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val key = when (element) {
            is TinyV1ClassMapping -> TinyV1SyntaxHighlighter.CLASS_NAME
            is TinyV1MethodMapping -> TinyV1SyntaxHighlighter.METHOD
            is TinyV1FieldMapping -> TinyV1SyntaxHighlighter.FIELD
            else -> return
        }

        for ((i, part) in element.childrenOfType<TinyV1MappingPart>().withIndex()) {
            if (i == 0) {
                registerHighlight(part, TinyV1SyntaxHighlighter.CLASS_NAME, holder)
            } else {
                registerHighlight(part, key, holder)
            }
        }
    }
}
