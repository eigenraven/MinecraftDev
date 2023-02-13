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

import groovy.transform.CompileStatic

@CompileStatic
final class McpModelRFGImpl implements McpModelRFG, Serializable {

    final String minecraftVersion
    final String mcpVersion
    final Set<String> mappingFiles
    final List<File> accessTransformers

    McpModelRFGImpl(String minecraftVersion, String mcpVersion, Set<String> mappingFiles, List<File> accessTransformers) {
        this.minecraftVersion = minecraftVersion
        this.mcpVersion = mcpVersion
        this.mappingFiles = mappingFiles
        this.accessTransformers = accessTransformers
    }

    @Override
    public String toString() {
        return "McpModelRFGImpl{" +
                "minecraftVersion='" + minecraftVersion + '\'' +
                ", mcpVersion='" + mcpVersion + '\'' +
                ", mappingFiles=" + mappingFiles +
                ", accessTransformers=" + accessTransformers +
                '}';
    }
}
