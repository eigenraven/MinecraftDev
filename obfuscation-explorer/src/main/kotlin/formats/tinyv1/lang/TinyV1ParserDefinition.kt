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

package io.mcdev.obfex.formats.tinyv1.lang

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
import io.mcdev.obfex.formats.tinyv1.gen.parser.TinyV1Parser
import io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1Types
import io.mcdev.obfex.formats.tinyv1.lang.psi.TinyV1LexerAdapter

class TinyV1ParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer = TinyV1LexerAdapter()
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createParser(project: Project?): PsiParser = TinyV1Parser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = TinyV1File(viewProvider)
    override fun createElement(node: ASTNode): PsiElement = TinyV1Types.Factory.createElement(node)
}

private val COMMENTS = TokenSet.create(TinyV1Types.COMMENT)
private val FILE = IFileElementType(Language.findInstance(TinyV1Language::class.java))
