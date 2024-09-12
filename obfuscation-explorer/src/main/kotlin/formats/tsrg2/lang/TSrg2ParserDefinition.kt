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

package io.mcdev.obfex.formats.tsrg2.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import io.mcdev.obfex.formats.tsrg2.gen.parser.TSrg2Parser
import io.mcdev.obfex.formats.tsrg2.gen.psi.TSrg2Types

class TSrg2ParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer = TSrg2LayoutLexer()
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createParser(project: Project): PsiParser = TSrg2Parser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = TSrg2File(viewProvider)
    override fun createElement(node: ASTNode): PsiElement = TSrg2Types.Factory.createElement(node)
    override fun getWhitespaceTokens(): TokenSet = WHITESPACE
}

private val WHITESPACE = TokenSet.orSet(TokenSet.WHITE_SPACE, TokenSet.create(TSrg2Types.TAB))
private val COMMENTS = TokenSet.create(TSrg2Types.COMMENT)
private val FILE = IFileElementType(Language.findInstance(TSrg2Language::class.java))
