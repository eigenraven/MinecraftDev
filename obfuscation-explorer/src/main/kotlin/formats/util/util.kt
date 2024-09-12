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

package io.mcdev.obfex.formats.util

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font
import kotlin.reflect.KClass

fun TextAttributesKey.highlight(): TextAttributes = defaultAttributes.highlight()
fun TextAttributes.highlight(): TextAttributes =
    TextAttributes.merge(
        this,
        TextAttributes(
            null,
            foregroundColor.toBackground(),
            foregroundColor,
            null,
            Font.ITALIC
        )

    )

fun registerHighlight(
    element: PsiElement?,
    key: TextAttributesKey,
    holder: AnnotationHolder,
    highlight: Boolean = false
) {
    if (element == null) {
        return
    }
    val builder = holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(key)
    if (highlight) {
        builder.enforcedTextAttributes(key.highlight())
    }
    builder.create()
}

private fun Color.toBackground(): Color {
    val darkerBase = darker().darker().darker().darker()
    val brighterBase = brighter().brighter().brighter().brighter()
    return JBColor(
        Color(brighterBase.red, brighterBase.green, brighterBase.blue, 128),
        Color(darkerBase.red, darkerBase.green, darkerBase.blue, 128)
    )
}

fun <T : PsiElement> PsiElement.childrenOfType(type: KClass<out T>): List<T> {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, type.java)
}

fun Array<*>.indicesFrom(first: Int): IntRange {
    if (first >= size) {
        return IntRange.EMPTY
    }
    return first until size
}
