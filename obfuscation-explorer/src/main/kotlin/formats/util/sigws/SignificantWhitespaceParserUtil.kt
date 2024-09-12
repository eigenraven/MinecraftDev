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

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase

open class SignificantWhitespaceParserUtil : GeneratedParserUtilBase() {
    companion object {
        @JvmStatic
        fun indent0(
            builder: PsiBuilder,
            level: Int,
            virtualOpen: Parser,
            virtualClose: Parser,
            param: Parser,
        ): Boolean {
            if (!recursion_guard_(builder, level, "indent0")) {
                return false
            }

            val marker = enter_section_(builder)

            val res =
                // VIRTUAL_OPEN
                virtualOpen.parse(builder, level + 1) &&
                    // <<param>>
                    param.parse(builder, level) &&
                    // (VIRTUAL_CLOSE | <<eof>>)
                    (virtualClose.parse(builder, level + 1) || eof(builder, level + 1))

            exit_section_(builder, marker, null, res)
            return res
        }
    }
}
