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

package io.mcdev.obfex.formats.tsrg2.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2ClassMapping
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2FieldMapping
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2MappingPart
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2MethodMapping
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2ParamIndex
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2ParamMapping
import io.mcdev.obfex.formats.util.registerHighlight

class TSrg2Annotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is TSrg2ParamIndex) {
            registerHighlight(element, TSrg2SyntaxHighlighter.PARAM_INDEX, holder)
            return
        }

        if (element !is TSrg2MappingPart) {
            return
        }

        val key = when (element.parent) {
            is TSrg2ClassMapping -> TSrg2SyntaxHighlighter.CLASS_NAME
            is TSrg2MethodMapping -> TSrg2SyntaxHighlighter.METHOD
            is TSrg2FieldMapping -> TSrg2SyntaxHighlighter.FIELD
            is TSrg2ParamMapping -> TSrg2SyntaxHighlighter.PARAM
            else -> return
        }

        registerHighlight(element, key, holder)
    }
}
