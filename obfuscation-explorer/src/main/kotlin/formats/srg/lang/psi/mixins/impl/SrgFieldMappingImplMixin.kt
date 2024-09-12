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

package io.mcdev.obfex.formats.srg.lang.psi.mixins.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import io.mcdev.obfex.formats.srg.gen.psi.SrgMappingPart
import io.mcdev.obfex.formats.srg.lang.psi.mixins.SrgFieldMappingMixin

abstract class SrgFieldMappingImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SrgFieldMappingMixin {

    override val mappingPartList: List<SrgMappingPart>
        get() = getStandardFieldMapping()?.mappingPartList ?: getStandardFieldMapping()?.mappingPartList ?: emptyList()

    override val obfName: SrgMappingPart?
        get() = getStandardFieldMapping()?.obfName ?: getExtendedFieldMapping()?.obfName

    override val deobfName: SrgMappingPart?
        get() = getStandardFieldMapping()?.deobfName ?: getExtendedFieldMapping()?.deobfName
}
