/*
 * Minecraft Development for IntelliJ
 *
 * https://mcdev.io/
 *
 * Copyright (C) 2023 minecraft-dev
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

package com.demonwav.mcdev.platform.mcp.gradle.datahandler

import com.demonwav.mcdev.platform.mcp.McpModuleSettings
import com.demonwav.mcdev.platform.mcp.gradle.McpModelData
import com.demonwav.mcdev.platform.mcp.gradle.tooling.McpModelRFG
import com.demonwav.mcdev.platform.mcp.srg.SrgType
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.gradle.tooling.model.idea.IdeaModule
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData
import org.jetbrains.plugins.gradle.service.project.ProjectResolverContext

object McpModelRFGHandler : McpModelDataHandler {

    override fun build(
        gradleModule: IdeaModule,
        node: DataNode<ModuleData>,
        resolverCtx: ProjectResolverContext
    ) {
        val data = resolverCtx.getExtraProject(gradleModule, McpModelRFG::class.java) ?: return

        val state = McpModuleSettings.State(
            data.minecraftVersion,
            data.mcpVersion,
            data.mappingFiles.find { it.endsWith("mcp-srg.srg") },
            SrgType.SRG,
            data.minecraftVersion
        )

        // This check is based purely on observation
        var task = node.data.id;
        if (!task.startsWith(":")) { // MC project is root
            task = ":"
        } else if (!task.endsWith(":")) { // MC project is not the root
            task += ":"
        }

        task += "generateForgeSrgMappings"

        val modelData = McpModelData(
            node.data,
            state,
            task,
            data.accessTransformers
        )

        node.createChild(
            McpModelData.KEY,
            McpModelData(
                node.data,
                McpModuleSettings.State(
                    data.minecraftVersion,
                    data.mcpVersion,
                    data.mappingFiles.find { it.endsWith("mcp-srg.srg") },
                    SrgType.SRG
                ),
                task,
                data.accessTransformers
            )
        )

        for (child in node.children) {
            val childData = child.data
            if (childData is GradleSourceSetData) {
                child.createChild(McpModelData.KEY, modelData.copy(module = childData))
            }
        }
    }
}
