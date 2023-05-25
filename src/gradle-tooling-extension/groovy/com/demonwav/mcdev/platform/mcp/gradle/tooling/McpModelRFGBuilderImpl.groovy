/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
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
        System.err.println("Attempting build of " + modelName + " in " + project)
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
        System.err.println("Done build of " + modelName + " in " + project)
        try {
            def implObj = new McpModelRFGImpl(extension.mcVersion.get(), extension.mcpMappingChannel.get() + "-" + extension.mcpMappingVersion.get(), mappingFiles.toSet(), atFiles)
            System.err.println("Impl: " + implObj)
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
