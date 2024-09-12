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

package io.mcdev.obfex.formats.jam.lang.colors

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.jam.gen.psi.JamTypes
import io.mcdev.obfex.formats.jam.lang.psi.JamLexerAdapter
import io.mcdev.obfex.formats.srg.lang.colors.SrgSyntaxHighlighter

class JamSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = JamLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            JamTypes.CLASS_KEY, JamTypes.FIELD_KEY, JamTypes.METHOD_KEY, JamTypes.PARAM_KEY -> KEYWORD_KEYS
            JamTypes.CLASS_TYPE -> CLASS_TYPE_KEYS
            JamTypes.PRIMITIVE -> PRIMITIVE_KEYS
            JamTypes.COMMENT -> COMMENT_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }
    }

    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey("JAM_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "JAM_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "JAM_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "JAM_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PARAM = TextAttributesKey.createTextAttributesKey(
            "JAM_PARAM",
            DefaultLanguageHighlighterColors.PARAMETER
        )
        val PARAM_INDEX = TextAttributesKey.createTextAttributesKey(
            "JAM_PARAM_INDEX",
            DefaultLanguageHighlighterColors.INLAY_DEFAULT
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "JAM_PRIMITIVE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "JAM_CLASS_TYPE",
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "JAM_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        private val KEYWORD_KEYS = arrayOf(SrgSyntaxHighlighter.KEYWORD)
        private val PRIMITIVE_KEYS = arrayOf(SrgSyntaxHighlighter.PRIMITIVE)
        private val CLASS_TYPE_KEYS = arrayOf(SrgSyntaxHighlighter.CLASS_TYPE)
        private val COMMENT_KEYS = arrayOf(SrgSyntaxHighlighter.COMMENT)
    }
}
