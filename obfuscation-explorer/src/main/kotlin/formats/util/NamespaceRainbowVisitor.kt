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

import com.intellij.codeHighlighting.ColorGenerator
import com.intellij.codeHighlighting.RainbowHighlighter
import com.intellij.codeInsight.daemon.RainbowVisitor
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.lang.Language
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.awt.Color
import java.awt.Font

abstract class NamespaceRainbowVisitor(private val language: Language) : RainbowVisitor() {

    private var rainbowColors: Array<Color>? = null

    override fun suitableForFile(file: PsiFile): Boolean = file.language == language

    override fun analyze(
        file: PsiFile,
        updateWholeFile: Boolean,
        holder: HighlightInfoHolder,
        action: Runnable,
    ): Boolean {
        computeColors(holder)
        val res = super.analyze(file, updateWholeFile, holder, action)
        rainbowColors = null
        return res
    }

    protected fun highlightNamespaceDecl(element: PsiElement) {
        val index = indexOf(element)
        val color = getColorFromIndex(index)

        val attribute = TextAttributes(color, null, null, null, Font.PLAIN)
        val highlightInfo = HighlightInfo
            .newHighlightInfo(RainbowHighlighter.RAINBOW_ELEMENT)
            .textAttributes(attribute)
            .range(element)
            .create()
        addInfo(highlightInfo)
    }

    protected fun highlightElement(
        element: PsiElement,
        index: Int,
        type: HighlightType
    ) {
        val color = getColorFromIndex(index)

        val fontType = when (type) {
            HighlightType.CLASS -> Font.ITALIC
            HighlightType.METHOD -> Font.BOLD
            HighlightType.FIELD, HighlightType.PARAM, HighlightType.LOCAL_VAR -> Font.PLAIN
        }

        var attr = TextAttributes(color, null, null, null, fontType)
        if (type == HighlightType.FIELD) {
            attr = attr.highlight()
        }

        val info = HighlightInfo
            .newHighlightInfo(RainbowHighlighter.RAINBOW_ELEMENT)
            .textAttributes(attr)
            .range(element)
            .create()
        addInfo(info)
    }

    protected fun highlightTypeSignature(element: PsiElement) {
        val color = getColorFromIndex(0)

        val textAttributes = TextAttributes(null, null, color, EffectType.SLIGHTLY_WIDER_BOX, Font.PLAIN)
        val highlightInfo = HighlightInfo
            .newHighlightInfo(RainbowHighlighter.RAINBOW_ELEMENT)
            .textAttributes(textAttributes)
            .range(element)
            .create()
        addInfo(highlightInfo)
    }

    protected fun indexOf(element: PsiElement): Int {
        return element.parent.childrenOfType(element::class).indexOf(element)
    }

    protected enum class HighlightType {
        CLASS, METHOD, FIELD, PARAM, LOCAL_VAR
    }

    protected fun getColorFromIndex(index: Int): Color {
        val colors = rainbowColors ?: error("rainbowColors is not initialized")
        return colors[index % colors.size]
    }

    private fun computeColors(holder: HighlightInfoHolder) {
        val colorScheme = holder.colorsScheme
        val stopRainbowColors = RainbowHighlighter.RAINBOW_COLOR_KEYS.map {
            colorScheme.getAttributes(it).foregroundColor
        }

        val colors = ColorGenerator.generateLinearColorSequence(stopRainbowColors, 4)

        var startingIndex = 0
        var index = 0
        rainbowColors = Array(colors.size) {
            val color = colors[index]
            index += 5
            if (index >= colors.size) {
                index = ++startingIndex
            }
            color
        }
    }
}
