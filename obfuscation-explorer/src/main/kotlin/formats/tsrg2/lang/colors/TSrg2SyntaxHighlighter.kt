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

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.tsrg.lang.colors.TSrgSyntaxHighlighter
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2Types
import io.mcdev.obfex.formats.tsrg2.lang.psi.TSrg2LexerAdapter

class TSrg2SyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TSrg2LexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        when (tokenType) {
            TSrg2Types.TSRG2_KEY, TSrg2Types.STATIC -> KEYWORD_KEYS
            TSrg2Types.NAMESPACE_KEY -> NAMESPACE_KEYS
            TSrg2Types.PRIMITIVE -> PRIMITIVE_KEYS
            TSrg2Types.COMMENT -> COMMENT_KEYS
            TSrg2Types.CLASS_TYPE -> CLASS_TYPE_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "TSRG2_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val NAMESPACE = TextAttributesKey.createTextAttributesKey(
            "TSRG2_NAMESPACE",
            DefaultLanguageHighlighterColors.METADATA
        )
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "TSRG2_CLASS_NAME",
            TSrgSyntaxHighlighter.CLASS_NAME
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "TSRG2_METHOD",
            TSrgSyntaxHighlighter.METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "TSRG2_FIELD",
            TSrgSyntaxHighlighter.FIELD
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "TSRG2_PRIMITIVE",
            TSrgSyntaxHighlighter.PRIMITIVE
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "TSRG2_CLASS_TYPE",
            TSrgSyntaxHighlighter.CLASS_TYPE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "TSRG2_COMMENT",
            TSrgSyntaxHighlighter.COMMENT
        )
        val PARAM_INDEX = TextAttributesKey.createTextAttributesKey(
            "TSRG2_PARAM_INDEX",
            DefaultLanguageHighlighterColors.INLAY_DEFAULT
        )
        val PARAM = TextAttributesKey.createTextAttributesKey(
            "TSRG2_PARAM",
            DefaultLanguageHighlighterColors.PARAMETER
        )

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val NAMESPACE_KEYS = arrayOf(NAMESPACE)
        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val CLASS_TYPE_KEYS = arrayOf(CLASS_TYPE)
    }
}
