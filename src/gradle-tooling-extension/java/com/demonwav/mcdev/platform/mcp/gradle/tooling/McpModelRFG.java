/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.gradle.tooling;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface McpModelRFG extends McpModel {
    String getMinecraftVersion();
    Set<String> getMappingFiles();
    List<File> getAccessTransformers();
}
