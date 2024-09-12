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

package io.mcdev.obfex

import com.intellij.ui.IconManager
import javax.swing.Icon

object ObfIcons {

    val OBF_EX_ICON = load("/fileTypes/obfex.svg")

    val SRG_ICON = load("/fileTypes/srg.svg")
    val TSRG_ICON = load("/fileTypes/tsrg.svg")
    val TSRG2_ICON = load("/fileTypes/tsrg2.svg")
    val CSRG_ICON = load("/fileTypes/csrg.svg")
    val XSRG_ICON = load("/fileTypes/xsrg.svg")
    val JAM_ICON = load("/fileTypes/jam.svg")
    val ENIGMA_ICON = load("/fileTypes/enigma.svg")
    val TINY_V1_ICON = load("/fileTypes/tinyv1.svg")
    val TINY_V2_ICON = load("/fileTypes/tinyv2.svg")
    val PROGUARD_ICONS = load("/fileTypes/proguard.svg")

    private fun load(path: String): Icon {
        return IconManager.getInstance().getIcon(path, ObfIcons::class.java.classLoader)
    }
}
