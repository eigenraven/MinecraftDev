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

package io.mcdev.obfex.formats.srg.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import io.mcdev.obfex.ObfIcons
import javax.swing.Icon

object SrgFileType : LanguageFileType(SrgLanguage) {

    override fun getName(): String = "SRG"
    override fun getDescription(): String = "SRG obfuscation mapping file"
    override fun getDefaultExtension(): String = "srg"
    override fun getIcon(): Icon = ObfIcons.SRG_ICON
}
