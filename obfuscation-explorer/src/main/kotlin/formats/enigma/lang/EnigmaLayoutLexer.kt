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

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaTypes
import io.mcdev.obfex.formats.enigma.lang.psi.EnigmaLexerAdapter
import io.mcdev.obfex.formats.util.sigws.SignificantWhitespaceLexer

class EnigmaLayoutLexer : SignificantWhitespaceLexer(EnigmaLexerAdapter()) {

    override val newlineTokens: TokenSet = TokenSet.create(EnigmaTypes.CRLF)
    override val tabTokens: TokenSet = TokenSet.create(EnigmaTypes.TAB)
    override val virtualOpenToken: IElementType = EnigmaTypes.VIRTUAL_OPEN
    override val virtualCloseToken: IElementType = EnigmaTypes.VIRTUAL_CLOSE
}
