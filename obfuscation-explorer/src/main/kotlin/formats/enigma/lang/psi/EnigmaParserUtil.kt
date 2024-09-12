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

package io.mcdev.obfex.formats.enigma.lang.psi

import com.intellij.lang.PsiBuilder
import io.mcdev.obfex.formats.enigma.gen.psi.EnigmaTypes
import io.mcdev.obfex.formats.util.sigws.SignificantWhitespaceParserUtil

@Suppress("UNUSED_PARAMETER")
object EnigmaParserUtil : SignificantWhitespaceParserUtil() {
    @JvmStatic
    fun fieldMappingPart(
        builder: PsiBuilder,
        level: Int,
    ): Boolean {
        if (builder.tokenType !== EnigmaTypes.PRIMITIVE) {
            return true
        }
        val next = builder.lookAhead(1)
        return next != null && // eof
            next !== EnigmaTypes.CRLF && // end of line
            next !== EnigmaTypes.VIRTUAL_CLOSE // automatically inserted at eof
    }
}
