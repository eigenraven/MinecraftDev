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

package io.mcdev.obfex.formats.util.sigws

import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

abstract class SignificantWhitespaceLexer(private val delegate: Lexer) : LexerBase() {

    private var lookingForIndent: Boolean = true
    private var lastSeenTabCount = 0
    private var currentToken: Token? = null

    private val tokenStack = ArrayDeque<Token>()

    abstract val newlineTokens: TokenSet
    abstract val tabTokens: TokenSet

    abstract val virtualOpenToken: IElementType
    abstract val virtualCloseToken: IElementType

    override fun getState(): Int = delegate.state

    override fun getTokenType(): IElementType? = currentToken?.tokenType
    override fun getTokenStart(): Int = currentToken?.start ?: 0
    override fun getTokenEnd(): Int = currentToken?.end ?: 0

    override fun getBufferSequence(): CharSequence = delegate.bufferSequence
    override fun getBufferEnd(): Int = delegate.bufferEnd

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        delegate.start(buffer, startOffset, endOffset, initialState)
        compute(getCurrentToken())
    }

    override fun advance() {
        if (tokenStack.isNotEmpty()) {
            currentToken = tokenStack.removeFirst()
            return
        }

        compute()
    }

    private fun compute(firstToken: Token? = null) {
        var newToken = firstToken ?: getNextToken() ?: return
        val firstNewToken = newToken

        if (!lookingForIndent) {
            currentToken = newToken
            if (newToken.tokenType in newlineTokens) {
                lookingForIndent = true
            }
            return
        }
        lookingForIndent = false

        tokenStack.addLast(newToken)

        var tabCount = 0
        while (newToken.tokenType in tabTokens) {
            tabCount++
            newToken = getNextToken()?.also { tokenStack.addLast(it) } ?: break
        }

        if (newToken.tokenType in newlineTokens) {
            // this is a blank line, ignore it
            currentToken = tokenStack.removeFirstOrNull()
            return
        }

        when {
            tabCount > lastSeenTabCount -> {
                val indentToken = virtualOpen(firstNewToken)
                repeat(tabCount - lastSeenTabCount) {
                    tokenStack.addFirst(indentToken)
                }
            }
            tabCount < lastSeenTabCount -> {
                val unindentToken = virtualClose(firstNewToken)
                repeat(lastSeenTabCount - tabCount) {
                    tokenStack.addFirst(unindentToken)
                }
            }
        }
        lastSeenTabCount = tabCount

        currentToken = tokenStack.removeFirstOrNull()
    }

    private fun getNextToken(): Token? {
        delegate.advance()
        return getCurrentToken()
    }

    private fun getCurrentToken(): Token? {
        if (delegate.tokenType == null) {
            currentToken = null
            return null
        }
        return Token(delegate.tokenType, delegate.tokenStart, delegate.tokenEnd)
    }

    private fun virtualOpen(precedesToken: Token) = Token(
        tokenType = virtualOpenToken,
        start = precedesToken.start,
        end = precedesToken.start,
    )

    private fun virtualClose(precedesToken: Token) = Token(
        tokenType = virtualCloseToken,
        start = precedesToken.start,
        end = precedesToken.start,
    )

    private class Token(
        val tokenType: IElementType?,
        val start: Int,
        val end: Int,
    ) {
        val isEof: Boolean
            get() = tokenType == null
    }
}
