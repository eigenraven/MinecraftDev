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

package io.mcdev.obfex.formats.tsrg.lang

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
import io.mcdev.obfex.formats.tsrg.gen.parser.TSrgParser
import io.mcdev.obfex.formats.tsrg.gen.psi.TSrgTypes
import io.mcdev.obfex.formats.tsrg.lang.psi.TSrgLexerAdapter

class TSrgParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer = TSrgLexerAdapter()
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createParser(project: Project): PsiParser = TSrgParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = TSrgFile(viewProvider)
    override fun createElement(node: ASTNode): PsiElement = TSrgTypes.Factory.createElement(node)
}

private val COMMENTS = TokenSet.create(TSrgTypes.COMMENT)
private val FILE = IFileElementType(Language.findInstance(TSrgLanguage::class.java))
