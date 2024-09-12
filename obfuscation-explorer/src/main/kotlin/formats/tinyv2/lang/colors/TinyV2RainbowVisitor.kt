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

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2ClassMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2FieldMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2FileElement
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2LocalVarMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2MappingPart
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2MethodMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2MethodSignature
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2Namespace
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2ParamMapping
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2TypeDesc
import io.mcdev.obfex.formats.tinyv2.lang.TinyV2Language
import io.mcdev.obfex.formats.util.NamespaceRainbowVisitor

class TinyV2RainbowVisitor : NamespaceRainbowVisitor(TinyV2Language) {

    override fun visit(element: PsiElement) {
        if (element is TinyV2Namespace) {
            highlightNamespaceDecl(element)
            return
        }

        val file = element.findParentOfType<TinyV2FileElement>() ?: return
        val namespaces = file.header.namespaceList

        if (element is TinyV2MappingPart) {
            handleMappingPart(element, namespaces)
        } else if (element is TinyV2TypeDesc && element.parent is TinyV2FieldMapping) {
            highlightTypeSignature(element)
        } else if (element is TinyV2MethodSignature) {
            highlightTypeSignature(element)
        }
    }

    private fun handleMappingPart(element: TinyV2MappingPart, namespaces: List<TinyV2Namespace>) {
        val index = indexOf(element.parent)
        if (namespaces.size <= index) {
            return
        }

        when (element.parent.parent) {
            is TinyV2ClassMapping -> highlightElement(element, index, HighlightType.CLASS)
            is TinyV2FieldMapping -> highlightElement(element, index, HighlightType.FIELD)
            is TinyV2MethodMapping -> highlightElement(element, index, HighlightType.METHOD)
            is TinyV2ParamMapping -> highlightElement(element, index, HighlightType.PARAM)
            is TinyV2LocalVarMapping -> highlightElement(element, index, HighlightType.LOCAL_VAR)
        }
    }

    override fun clone(): HighlightVisitor = TinyV2RainbowVisitor()
}
