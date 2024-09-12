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

package io.mcdev.obfex.formats.enigma.lang.colors

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaTypes
import io.mcdev.obfex.formats.enigma.lang.psi.EnigmaLexerAdapter

class EnigmaSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer() = EnigmaLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        when (tokenType) {
            EnigmaTypes.CLASS_KEY, EnigmaTypes.FIELD_KEY, EnigmaTypes.METHOD_KEY,
            EnigmaTypes.ARG_KEY, EnigmaTypes.COMMENT_KEY -> KEYWORD_KEYS
            EnigmaTypes.CLASS_TYPE -> CLASS_TYPE_KEYS
            EnigmaTypes.PRIMITIVE -> PRIMITIVE_KEYS
            EnigmaTypes.COMMENT -> COMMENT_KEYS
            EnigmaTypes.DOC_TEXT -> DOC_KEYS
            else -> TextAttributesKey.EMPTY_ARRAY
        }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_CLASS_NAME",
            DefaultLanguageHighlighterColors.STRING
        )
        val METHOD = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_METHOD",
            DefaultLanguageHighlighterColors.INSTANCE_METHOD
        )
        val FIELD = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PARAM = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_PARAM",
            DefaultLanguageHighlighterColors.PARAMETER
        )
        val PARAM_INDEX = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_PARAM_INDEX",
            DefaultLanguageHighlighterColors.INLAY_DEFAULT
        )
        val PRIMITIVE = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_PRIMITIVE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val CLASS_TYPE = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_CLASS_TYPE",
            DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )
        val DOC = TextAttributesKey.createTextAttributesKey(
            "ENIGMA_JAVADOC",
            DefaultLanguageHighlighterColors.DOC_COMMENT
        )

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val PRIMITIVE_KEYS = arrayOf(PRIMITIVE)
        private val CLASS_TYPE_KEYS = arrayOf(CLASS_TYPE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val DOC_KEYS = arrayOf(DOC)
        private val PARAM_INDEX_KEYS = arrayOf(PARAM_INDEX)
    }
}
