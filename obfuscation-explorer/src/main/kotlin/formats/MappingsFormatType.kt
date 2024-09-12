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

package io.mcdev.obfex.formats

import com.intellij.util.xmlb.Converter as IConverter
import javax.swing.Icon
import org.jetbrains.annotations.NonNls

abstract class MappingsFormatType(@param:NonNls val id: String) {

    abstract val icon: Icon
    abstract val name: String

    class Converter : IConverter<MappingsFormatType>() {
        override fun toString(value: MappingsFormatType): String? {
            return value.id
        }

        override fun fromString(value: String): MappingsFormatType? {
            return MappingsFormatTypeManager.get().registeredTypes.firstOrNull { it.id == value }
        }
    }
}
