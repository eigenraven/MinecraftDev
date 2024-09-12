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

package io.mcdev.obfex.formats.tsrg.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgDeobfClassMappingPart
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgDeobfFieldMappingPart
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgDeobfMethodMappingPart
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgObfClassMappingPart
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgObfFieldMappingPart
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgObfMethodMappingPart
import io.mcdev.obfex.formats.util.registerHighlight

class TSrgAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val key = when (element) {
            is TSrgObfClassMappingPart, is TSrgDeobfClassMappingPart -> TSrgSyntaxHighlighter.CLASS_NAME
            is TSrgObfMethodMappingPart, is TSrgDeobfMethodMappingPart -> TSrgSyntaxHighlighter.METHOD
            is TSrgObfFieldMappingPart, is TSrgDeobfFieldMappingPart -> TSrgSyntaxHighlighter.FIELD
            else -> return
        }

        val highlight = when (element) {
            is TSrgDeobfClassMappingPart, is TSrgDeobfMethodMappingPart, is TSrgDeobfFieldMappingPart -> true
            else -> false
        }

        registerHighlight(element, key, holder, highlight)
    }
}
