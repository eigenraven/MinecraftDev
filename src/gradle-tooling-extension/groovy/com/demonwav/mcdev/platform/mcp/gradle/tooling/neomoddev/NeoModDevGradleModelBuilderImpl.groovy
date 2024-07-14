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

package com.demonwav.mcdev.platform.mcp.gradle.tooling.neomoddev

import com.demonwav.mcdev.platform.mcp.gradle.tooling.McpModelNMD
import org.gradle.api.Project
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService

import java.nio.file.Files

final class NeoModDevGradleModelBuilderImpl implements ModelBuilderService {

    @Override
    boolean canBuild(String modelName) {
        return McpModelNMD.name == modelName
    }

    @Override
    Object buildAll(String modelName, Project project) {
        def extension = project.extensions.findByName('neoForge')
        if (extension == null) {
            return null
        }

        if (!project.plugins.findPlugin("net.neoforged.moddev")) {
            return null
        }

        def neoforgeVersion = extension.version.get()
        if (neoforgeVersion == null) {
            return null
        }

        def accessTransformers = extension.accessTransformers.get().collect { project.file(it) }

        // Hacky way to guess where the mappings file is, but I could not find a proper way to find it
        def neoformDir = project.buildDir.toPath().resolve("neoForm")
        def mappingsFile = Files.list(neoformDir)
                .map { it.resolve("config/joined.tsrg") }
                .filter { Files.exists(it) }
                .findFirst()
                .orElse(null)
                ?.toFile()

        //noinspection GroovyAssignabilityCheck
        return new NeoModDevGradleModelImpl(neoforgeVersion, mappingsFile, accessTransformers)
    }

    @Override
    ErrorMessageBuilder getErrorMessageBuilder(@NotNull Project project, @NotNull Exception e) {
        return ErrorMessageBuilder.create(
                project, e, "MinecraftDev import errors"
        ).withDescription("Unable to build MinecraftDev MCP project configuration")
    }
}
