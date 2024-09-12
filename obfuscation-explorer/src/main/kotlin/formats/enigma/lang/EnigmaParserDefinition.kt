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

package io.mcdev.obfex.formats.enigma.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import io.mcdev.obfex.formats.enigma.gen.parser.EnigmaParser
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaTypes

class EnigmaParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer = EnigmaLayoutLexer()
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createParser(project: Project): PsiParser = EnigmaParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = EnigmaFile(viewProvider)
    override fun createElement(node: ASTNode): PsiElement = EnigmaTypes.Factory.createElement(node)
    override fun getWhitespaceTokens(): TokenSet = WHITESPACE
}

private val WHITESPACE = TokenSet.orSet(TokenSet.WHITE_SPACE, TokenSet.create(EnigmaTypes.TAB))
private val COMMENTS = TokenSet.create(EnigmaTypes.COMMENT)
private val FILE = IFileElementType(EnigmaLanguage)
