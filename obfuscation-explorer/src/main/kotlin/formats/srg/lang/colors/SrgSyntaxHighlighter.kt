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

package io.mcdev.obfex.formats.srg.lang.colors

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.srg.gen.psi.SrgTypes
import io.mcdev.obfex.formats.srg.lang.psi.SrgLexerAdapter

class SrgSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = SrgLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            SrgTypes.PACKAGE_KEY, SrgTypes.CLASS_KEY, SrgTypes.FIELD_KEY, SrgTypes.METHOD_KEY -> KEYWORD_KEYS
            SrgTypes.CLASS_TYPE -> CLASS_TYPE_KEYS
            SrgTypes.PRIMITIVE -> PRIMITIVE_KEYS
            SrgTypes.COMMENT -> COMMENT_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey("SRG_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "SRG_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "SRG_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "SRG_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "SRG_PRIMITIVE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "SRG_CLASS_TYPE",
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "SRG_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val CLASS_TYPE_KEYS = arrayOf(CLASS_TYPE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
    }
}
