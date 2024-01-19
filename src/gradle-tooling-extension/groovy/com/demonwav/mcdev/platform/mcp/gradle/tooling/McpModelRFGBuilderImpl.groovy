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

package com.demonwav.mcdev.platform.mcp.gradle.tooling

import org.gradle.api.Project
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService

final class McpModelRFGBuilderImpl implements ModelBuilderService {

    @Override
    boolean canBuild(String modelName) {
        return McpModelRFG.name == modelName
    }

    @Override
    Object buildAll(String modelName, Project project) {
        def extension = project.extensions.findByName('minecraft')
        if (extension == null) {
            return null
        }

        def mcpTasksObj = project.extensions.findByName('mcpTasks')
        if (mcpTasksObj == null) {
            return null
        }

        if (project.tasks.findByName("generateForgeSrgMappings") == null) {
            return null
        }

        def mappingFiles = project.tasks.generateForgeSrgMappings.outputs.files.files.collect { it.absolutePath }
        def atFiles = mcpTasksObj.deobfuscationATs.files.collect {it}
        try {
            def implObj = new McpModelRFGImpl(extension.mcVersion.get(), extension.mcpMappingChannel.get() + "-" + extension.mcpMappingVersion.get(), mappingFiles.toSet(), atFiles)
            return implObj
        } catch (Throwable t) {
            System.err.println(t.message)
            t.printStackTrace()
            throw t
        }
    }

    @Override
    ErrorMessageBuilder getErrorMessageBuilder(@NotNull Project project, @NotNull Exception e) {
        return ErrorMessageBuilder.create(
                project, e, "MinecraftDev import errors"
        ).withDescription("Unable to build MinecraftDev MCP project configuration")
    }
}
