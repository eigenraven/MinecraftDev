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

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2ClassMapping
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2FieldMapping
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2FileElement
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2MappingPart
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2MethodMapping
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2MethodSignature
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2Namespace
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2ParamMapping
import io.mcdev.obfex.formats.tsrg2.lang.TSrg2Language
import io.mcdev.obfex.formats.util.NamespaceRainbowVisitor

class TSrg2RainbowVisitor : NamespaceRainbowVisitor(TSrg2Language) {

    override fun visit(element: PsiElement) {
        if (element is TSrg2Namespace) {
            highlightNamespaceDecl(element)
            return
        }

        val file = element.findParentOfType<TSrg2FileElement>() ?: return
        val namespaces = file.header.namespaceList

        if (element is TSrg2MappingPart) {
            handleMappingPart(element, namespaces)
        } else if (element is TSrg2MethodSignature) {
            handleMethodSignature(element, namespaces)
        }
    }

    private fun handleMappingPart(element: TSrg2MappingPart, namespaces: List<TSrg2Namespace>) {
        val index = indexOf(element)
        if (namespaces.size <= index) {
            return
        }

        when (element.parent) {
            is TSrg2ClassMapping -> highlightElement(element, index, HighlightType.CLASS)
            is TSrg2MethodMapping -> highlightElement(element, index, HighlightType.METHOD)
            is TSrg2FieldMapping -> highlightElement(element, index, HighlightType.FIELD)
            is TSrg2ParamMapping -> highlightElement(element, index, HighlightType.PARAM)
        }
    }

    private fun handleMethodSignature(element: TSrg2MethodSignature, namespaces: List<TSrg2Namespace>) {
        if (namespaces.isEmpty()) {
            return
        }

        highlightTypeSignature(element)
    }

    override fun clone(): HighlightVisitor = TSrg2RainbowVisitor()
}
