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

package io.mcdev.obfex.formats.tinyv2

import io.mcdev.obfex.ObfIcons
import io.mcdev.obfex.formats.MappingsFormatType
import javax.swing.Icon

object TinyV2MappingsFormatType : MappingsFormatType("tiny-v2") {

    override val icon: Icon = ObfIcons.TINY_V2_ICON
    override val name: String = "Tiny v2"
}
