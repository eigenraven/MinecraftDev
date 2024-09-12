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

package io.mcdev.obfex.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import io.mcdev.obfex.ObfIcons

class ObfExFacetType :
    FacetType<ObfExFacet, ObfExFacetConfiguration>(ObfExFacet.ID, TYPE_ID, "Obfuscation Explorer") {

    override fun createFacet(
        module: Module,
        name: String,
        configuration: ObfExFacetConfiguration,
        underlyingFacet: Facet<*>?
    ) = ObfExFacet(module, name, configuration, underlyingFacet)

    override fun createDefaultConfiguration() = ObfExFacetConfiguration()
    override fun isSuitableModuleType(moduleType: ModuleType<*>?) = true

    override fun getIcon() = ObfIcons.OBF_EX_ICON

    companion object {
        const val TYPE_ID = "obfex"
    }
}
