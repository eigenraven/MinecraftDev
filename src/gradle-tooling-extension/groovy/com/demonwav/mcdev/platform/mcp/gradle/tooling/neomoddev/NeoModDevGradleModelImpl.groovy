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
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [File])
final class NeoModDevGradleModelImpl implements McpModelNMD, Serializable {

    final String neoForgeVersion
    final String neoFormVersion
    final File mappingsFile
    final List<File> accessTransformers
}
