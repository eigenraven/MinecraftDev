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

package io.mcdev.obfex.formats.enigma.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaArgIndex
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaArgMapping
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaClassMappingPart
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaFieldMapping
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaMappingPart
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaMethodMapping
import io.mcdev.obfex.formats.util.registerHighlight

class EnigmaAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val key = when (element) {
            is EnigmaClassMappingPart -> EnigmaSyntaxHighlighter.CLASS_NAME
            is EnigmaArgIndex -> EnigmaSyntaxHighlighter.PARAM_INDEX
            is EnigmaMappingPart -> when (element.parent) {
                is EnigmaMethodMapping -> EnigmaSyntaxHighlighter.METHOD
                is EnigmaFieldMapping -> EnigmaSyntaxHighlighter.FIELD
                is EnigmaArgMapping -> EnigmaSyntaxHighlighter.PARAM
                else -> return
            }
            else -> return
        }

        registerHighlight(element, key, holder)
    }
}
