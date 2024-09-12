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

import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import io.mcdev.obfex.facet.config.ObfExFacetEditorTab
import io.mcdev.obfex.formats.MappingsFormatType

@State(name = "ObfExFacet")
class ObfExFacetConfiguration : FacetConfiguration, PersistentStateComponent<ObfExFacetConfigurationData> {

    private var state = ObfExFacetConfigurationData()

    override fun createEditorTabs(
        editorContext: FacetEditorContext?,
        validatorsManager: FacetValidatorsManager?
    ): Array<FacetEditorTab> {
        return arrayOf(ObfExFacetEditorTab(this))
    }

    override fun getState() = state
    override fun loadState(state: ObfExFacetConfigurationData) {
        this.state = state
    }
}

data class ObfExFacetConfigurationData(
    @Tag("mappingTargets")
    @XCollection(elementName = "mappingTarget", style = XCollection.Style.v2)
    var mappingTargets: List<MappingTargetConfig> = listOf(),
)

data class MappingTargetConfig(

    @Attribute("type", converter = MappingsFormatType.Converter::class)
    var type: MappingsFormatType? = null,

    @Attribute("uri")
    var uri: String? = null,
)
