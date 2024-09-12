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

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1ClassMapping
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1FieldMapping
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1FileElement
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1MappingPart
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1MethodMapping
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1MethodSignature
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1Namespace
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1TypeDesc
import io.mcdev.obfex.formats.tinyv1.lang.TinyV1Language
import io.mcdev.obfex.formats.util.NamespaceRainbowVisitor

class TinyV1RainbowVisitor : NamespaceRainbowVisitor(TinyV1Language) {

    override fun visit(element: PsiElement) {
        if (element is TinyV1Namespace) {
            highlightNamespaceDecl(element)
            return
        }

        val file = element.findParentOfType<TinyV1FileElement>() ?: return
        val namespaces = file.header.namespaceList

        if (element is TinyV1MappingPart) {
            handleMappingPart(element, namespaces)
        } else if (element is TinyV1TypeDesc && element.parent is TinyV1FieldMapping) {
            handleTypeSignature(element, namespaces)
        } else if (element is TinyV1MethodSignature) {
            handleTypeSignature(element, namespaces)
        }
    }

    private fun handleMappingPart(element: TinyV1MappingPart, namespaces: List<TinyV1Namespace>) {
        var index = indexOf(element)

        if (element.parent is TinyV1ClassMapping) {
            if (namespaces.size > index) {
                highlightElement(element, index, HighlightType.CLASS)
            }
            return
        }

        // fields and methods
        // first part is always class name
        if (index == 0 && namespaces.isNotEmpty()) {
            highlightElement(element, index, HighlightType.CLASS)
            return
        }

        index--
        if (namespaces.size <= index) {
            return
        }

        when (element.parent) {
            is TinyV1MethodMapping -> highlightElement(element, index, HighlightType.METHOD)
            is TinyV1FieldMapping -> highlightElement(element, index, HighlightType.FIELD)
        }
    }

    private fun handleTypeSignature(element: PsiElement, namespaces: List<TinyV1Namespace>) {
        if (namespaces.isEmpty()) {
            return
        }

        highlightTypeSignature(element)
    }

    override fun clone(): HighlightVisitor = TinyV1RainbowVisitor()
}
