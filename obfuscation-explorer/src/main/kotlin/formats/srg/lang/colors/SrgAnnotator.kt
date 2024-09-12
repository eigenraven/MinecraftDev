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

package io.mcdev.obfex.formats.srg.lang.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.srg.gen.psi.SrgClassMapping
import io.mcdev.obfex.formats.srg.gen.psi.SrgFieldMapping
import io.mcdev.obfex.formats.srg.gen.psi.SrgMethodMapping
import io.mcdev.obfex.formats.srg.lang.SrgLanguage
import io.mcdev.obfex.formats.util.registerHighlight

class SrgAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is SrgClassMapping -> {
                registerHighlight(element.obfName, SrgSyntaxHighlighter.CLASS_NAME, holder)
                registerHighlight(element.deobfName, SrgSyntaxHighlighter.CLASS_NAME, holder, true)
            }

            is SrgMethodMapping -> {
                registerHighlight(element.obfName, SrgSyntaxHighlighter.CLASS_NAME, holder)
                element.obfName?.identifier?.namePartList?.last()?.let { name ->
                    registerHighlight(name, SrgSyntaxHighlighter.METHOD, holder)
                }
                registerHighlight(element.deobfName, SrgSyntaxHighlighter.CLASS_NAME, holder, true)
                element.deobfName?.identifier?.namePartList?.last()?.let { name ->
                    registerHighlight(name, SrgSyntaxHighlighter.METHOD, holder, true)
                }
            }

            is SrgFieldMapping -> {
                registerHighlight(element.obfName, SrgSyntaxHighlighter.CLASS_NAME, holder)
                element.obfName?.identifier?.namePartList?.last()?.let { name ->
                    registerHighlight(name, SrgSyntaxHighlighter.FIELD, holder)
                }
                registerHighlight(element.deobfName, SrgSyntaxHighlighter.CLASS_NAME, holder, true)
                element.deobfName?.identifier?.namePartList?.last()?.let { name ->
                    registerHighlight(name, SrgSyntaxHighlighter.FIELD, holder, true)
                }

                if (element.getExtendedFieldMapping() != null && element.containingFile.language == SrgLanguage) {
                    val msg = "Extended field mappings are not allowed in standard SRG mapping files"
                    holder.newAnnotation(HighlightSeverity.ERROR, msg)
                        .range(element)
                        .create()
                }
            }
        }
    }
}
