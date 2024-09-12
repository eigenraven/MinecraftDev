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

package io.mcdev.obfex.formats.jam.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.jam.gen.psi.JamClassMapping
import io.mcdev.obfex.formats.jam.gen.psi.JamFieldMapping
import io.mcdev.obfex.formats.jam.gen.psi.JamMethodMapping
import io.mcdev.obfex.formats.jam.gen.psi.JamParamMapping
import io.mcdev.obfex.formats.util.registerHighlight

class JamAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is JamClassMapping -> {
                registerHighlight(element.obfName, JamSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.deobfName, JamSyntaxHighlighter.CLASS_NAME, holder, true)
            }

            is JamMethodMapping -> {
                registerHighlight(element.obfClassName, JamSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.obfMethodName, JamSyntaxHighlighter.METHOD, holder)
                registerHighlight(element.deobfName, JamSyntaxHighlighter.METHOD, holder, true)
            }

            is JamFieldMapping -> {
                registerHighlight(element.obfClassName, JamSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.obfFieldName, JamSyntaxHighlighter.FIELD, holder)
                registerHighlight(element.deobfName, JamSyntaxHighlighter.FIELD, holder, true)
            }

            is JamParamMapping -> {
                registerHighlight(element.obfClassName, JamSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.obfMethodName, JamSyntaxHighlighter.METHOD, holder)
                registerHighlight(element.parameterIndex, JamSyntaxHighlighter.PARAM_INDEX, holder)
                registerHighlight(element.deobfName, JamSyntaxHighlighter.PARAM, holder, true)
            }
        }
    }
}
