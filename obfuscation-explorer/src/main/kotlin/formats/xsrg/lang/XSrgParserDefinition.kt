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

package io.mcdev.obfex.formats.xsrg.lang

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
import io.mcdev.obfex.formats.srg.gen.parser.SrgParser
import io.mcdev.obfex.formats.srg.gen.psi.SrgTypes
import io.mcdev.obfex.formats.srg.lang.psi.SrgLexerAdapter

class XSrgParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer = SrgLexerAdapter()
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createParser(project: Project): PsiParser = SrgParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = XSrgFile(viewProvider)
    override fun createElement(node: ASTNode): PsiElement = SrgTypes.Factory.createElement(node)
}

private val COMMENTS = TokenSet.create(SrgTypes.COMMENT)
private val FILE = IFileElementType(Language.findInstance(XSrgLanguage::class.java))
