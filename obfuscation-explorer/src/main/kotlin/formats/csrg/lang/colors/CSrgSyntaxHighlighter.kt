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

package io.mcdev.obfex.formats.csrg.lang.colors

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.csrg.gen.psi.CSrgTypes
import io.mcdev.obfex.formats.csrg.lang.psi.CSrgLexerAdapter

class CSrgSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer() = CSrgLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        when (tokenType) {
            CSrgTypes.CLASS_TYPE -> CLASS_TYPE_KEYS
            CSrgTypes.PRIMITIVE -> PRIMITIVE_KEYS
            CSrgTypes.COMMENT -> COMMENT_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }

    companion object {
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "CSRG_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "CSRG_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "CSRG_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "CSRG_PRIMITIVE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "CSRG_CLASS_TYPE",
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "CSRG_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val CLASS_TYPE_KEYS = arrayOf(CLASS_TYPE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
    }
}
