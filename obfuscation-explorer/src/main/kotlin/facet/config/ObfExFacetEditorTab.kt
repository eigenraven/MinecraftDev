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

package io.mcdev.obfex.facet.config

import com.intellij.facet.ui.FacetEditorTab
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import io.mcdev.obfex.facet.ObfExFacetConfiguration
import javax.swing.JComponent

class ObfExFacetEditorTab(private val config: ObfExFacetConfiguration) : FacetEditorTab() {

    private val table = MappingsTable(config)

    override fun createComponent(): JComponent {
        return panel {
            row {
                cell(table.panel)
                    .align(AlignX.FILL)
            }
        }
    }

    override fun isModified(): Boolean {
        return config.state.mappingTargets != table.stateToConfig()
    }

    override fun getDisplayName(): String = "Obfuscation Explorer Module Settings"

    override fun reset() {
        table.reset()
    }

    override fun apply() {
        config.state.mappingTargets = table.stateToConfig()
    }
}
