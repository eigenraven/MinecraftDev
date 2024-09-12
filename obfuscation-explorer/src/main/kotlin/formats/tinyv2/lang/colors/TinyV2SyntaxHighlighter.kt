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

import com.intellij.ide.highlighter.custom.CustomHighlighterColors
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2Types
import io.mcdev.obfex.formats.tinyv2.lang.psi.TinyV2LexerAdapter

class TinyV2SyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TinyV2LexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            TinyV2Types.TINY_KEY, TinyV2Types.VERSION_NUM -> KEYWORD_KEYS

            TinyV2Types.CLASS_KEY, TinyV2Types.FIELD_KEY, TinyV2Types.METHOD_KEY, TinyV2Types.PARAM_KEY,
            TinyV2Types.VAR_KEY -> KEYWORD_KEYS

            TinyV2Types.COMMENT_KEY -> KEYWORD_KEYS
            TinyV2Types.DOC_TEXT -> DOC_COMMENT_KEYS
            TinyV2Types.NAMESPACE_KEY -> NAMESPACE_KEYS
            TinyV2Types.PROPERTY_KEY -> PROPERTY_KEY_KEYS
            TinyV2Types.PROPERTY_VALUE -> PROPERTY_VALUE_KEYS
            TinyV2Types.CLASS_TYPE -> CLASS_TYPE_KEYS
            TinyV2Types.PRIMITIVE -> PRIMITIVE_KEYS
            TinyV2Types.COMMENT -> COMMENT_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "TINYV2_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val NAMESPACE = TextAttributesKey.createTextAttributesKey(
            "TINYV2_NAMESPACE",
            DefaultLanguageHighlighterColors.METADATA
        )
        val PROPERTY_KEY = TextAttributesKey.createTextAttributesKey(
            "TINYV2_PROPERTY_KEY",
            CustomHighlighterColors.CUSTOM_KEYWORD2_ATTRIBUTES
        )
        val PROPERTY_VALUE = TextAttributesKey.createTextAttributesKey(
            "TINYV2_PROPERTY_VALUE",
            CustomHighlighterColors.CUSTOM_KEYWORD3_ATTRIBUTES
        )
        val DOC_COMMENT = TextAttributesKey.createTextAttributesKey(
            "TINYV2_DOC_COMMENT",
            DefaultLanguageHighlighterColors.DOC_COMMENT
        )
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "TINYV2_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "TINYV2_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "TINYV2_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PARAM_INDEX = TextAttributesKey.createTextAttributesKey(
            "TINYV2_PARAM_INDEX",
            DefaultLanguageHighlighterColors.INLAY_DEFAULT
        )
        val PARAM = TextAttributesKey.createTextAttributesKey(
            "TINYV2_PARAM",
            DefaultLanguageHighlighterColors.PARAMETER
        )
        val LOCAL_VAR_INDEX = TextAttributesKey.createTextAttributesKey(
            "TINYV2_LOCAL_VAR_INDEX",
            DefaultLanguageHighlighterColors.INLAY_DEFAULT
        )
        val LOCAL_VAR = TextAttributesKey.createTextAttributesKey(
            "TINYV2_LOCAL_VAR",
            DefaultLanguageHighlighterColors.PARAMETER
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "TINYV2_PRIMITIVE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "TINYV2_CLASS_TYPE",
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "TINYV2_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val DOC_COMMENT_KEYS = arrayOf(DOC_COMMENT)
        private val NAMESPACE_KEYS = arrayOf(NAMESPACE)
        private val PROPERTY_KEY_KEYS = arrayOf(PROPERTY_KEY)
        private val PROPERTY_VALUE_KEYS = arrayOf(PROPERTY_VALUE)
        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val CLASS_TYPE_KEYS = arrayOf(CLASS_TYPE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
    }
}
