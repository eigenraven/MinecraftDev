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

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1Types
import io.mcdev.obfex.formats.tinyv1.lang.psi.TinyV1LexerAdapter

class TinyV1SyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TinyV1LexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            TinyV1Types.V1_KEY, TinyV1Types.CLASS_KEY, TinyV1Types.FIELD_KEY, TinyV1Types.METHOD_KEY -> KEYWORD_KEYS
            TinyV1Types.NAMESPACE_KEY -> NAMESPACE_KEYS
            TinyV1Types.CLASS_TYPE -> CLASS_TYPE_KEYS
            TinyV1Types.PRIMITIVE -> PRIMITIVE_KEYS
            TinyV1Types.COMMENT -> COMMENT_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "TINYV1_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val NAMESPACE = TextAttributesKey.createTextAttributesKey(
            "TINYV1_NAMESPACE",
            DefaultLanguageHighlighterColors.METADATA
        )
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "TINYV1_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "TINYV1_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "TINYV1_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "TINYV1_PRIMITIVE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "TINYV1_CLASS_TYPE",
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "TINYV1_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val NAMESPACE_KEYS = arrayOf(NAMESPACE)
        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val CLASS_TYPE_KEYS = arrayOf(CLASS_TYPE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
    }
}
