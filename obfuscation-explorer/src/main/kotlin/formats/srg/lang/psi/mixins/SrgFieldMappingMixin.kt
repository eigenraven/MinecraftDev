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

package io.mcdev.obfex.formats.srg.lang.psi.mixins

import com.intellij.psi.PsiElement
import io.mcdev.obfex.formats.srg.gen.psi.SrgExtendedFieldMapping
import io.mcdev.obfex.formats.srg.gen.psi.SrgMappingPart
import io.mcdev.obfex.formats.srg.gen.psi.SrgStandardFieldMapping

interface SrgFieldMappingMixin : PsiElement {
    fun getExtendedFieldMapping(): SrgExtendedFieldMapping?

    fun getStandardFieldMapping(): SrgStandardFieldMapping?

    val mappingPartList: List<SrgMappingPart>
    val obfName: SrgMappingPart?
    val deobfName: SrgMappingPart?
}
