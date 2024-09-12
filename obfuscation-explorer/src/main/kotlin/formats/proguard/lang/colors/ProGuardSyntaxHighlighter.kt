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

package io.mcdev.obfex.formats.proguard.lang.colors

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.proguard.gen.psi.ProGuardTypes
import io.mcdev.obfex.formats.proguard.lang.psi.ProGuardLexerAdapter

class ProGuardSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = ProGuardLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        when (tokenType) {
            ProGuardTypes.PRIMITIVE -> PRIMITIVE_KEYS
            ProGuardTypes.NUMBER -> NUMBER_KEYS
            ProGuardTypes.DOT -> DOT_KEYS
            ProGuardTypes.COMMA -> COMMA_KEYS
            ProGuardTypes.COMMENT -> COMMENT_KEYS
            ProGuardTypes.COLON -> COLON_KEYS
            ProGuardTypes.POINTER -> POINTER_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_PRIMITIVE",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val NUMBER = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_NUMBER",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )
        val COMMA = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_COMMA",
            DefaultLanguageHighlighterColors.DOT
        )
        val DOT = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_DOT",
            COMMA
        )
        val COLON = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_COLON",
            DOT
        )
        val POINTER = TextAttributesKey.createTextAttributesKey(
            "PROGUARD_POINTER",
            DefaultLanguageHighlighterColors.INLAY_TEXT_WITHOUT_BACKGROUND
        )

        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val COMMA_KEYS = arrayOf(COMMA)
        private val DOT_KEYS = arrayOf(DOT)
        private val COLON_KEYS = arrayOf(COLON)
        private val POINTER_KEYS = arrayOf(POINTER)
    }
}
