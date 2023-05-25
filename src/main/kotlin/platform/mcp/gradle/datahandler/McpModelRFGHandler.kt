/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
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

        val modelData = McpModelData(
            node.data,
            state,
            "generateForgeSrgMappings",
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
                "generateForgeSrgMappings",
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
