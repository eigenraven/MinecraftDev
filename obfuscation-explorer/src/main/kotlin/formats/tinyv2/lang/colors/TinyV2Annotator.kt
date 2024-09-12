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

package io.mcdev.obfex.formats.tinyv2.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2ClassMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2FieldMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2LocalVarIndex
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2LocalVarMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2LocalVarStart
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2LvtIndex
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2MappingPart
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2MethodMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2ParamIndex
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2ParamMapping
import io.mcdev.obfex.formats.util.registerHighlight

class TinyV2Annotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is TinyV2ParamIndex -> {
                registerHighlight(element, TinyV2SyntaxHighlighter.PARAM_INDEX, holder)
                return
            }
            is TinyV2LocalVarIndex, is TinyV2LocalVarStart, is TinyV2LvtIndex -> {
                registerHighlight(element, TinyV2SyntaxHighlighter.LOCAL_VAR_INDEX, holder)
                return
            }
        }

        val key = when (element) {
            is TinyV2ClassMapping -> TinyV2SyntaxHighlighter.CLASS_NAME
            is TinyV2MethodMapping -> TinyV2SyntaxHighlighter.METHOD
            is TinyV2FieldMapping -> TinyV2SyntaxHighlighter.FIELD
            is TinyV2ParamMapping -> TinyV2SyntaxHighlighter.PARAM
            is TinyV2LocalVarMapping -> TinyV2SyntaxHighlighter.LOCAL_VAR
            else -> return
        }

        for (part in element.childrenOfType<TinyV2MappingPart>()) {
            registerHighlight(part, key, holder)
        }
    }
}
