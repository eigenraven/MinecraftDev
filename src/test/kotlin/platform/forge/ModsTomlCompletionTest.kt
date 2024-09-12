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

package com.demonwav.mcdev.platform.forge

import com.demonwav.mcdev.framework.BaseMinecraftTest
import com.intellij.testFramework.TestDataPath
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val TEST_DATA_PATH = "platform/forge/modsTomlCompletion"

@TestDataPath(TEST_DATA_PATH)
@DisplayName("Mods Toml Completion Tests")
class ModsTomlCompletionTest : BaseMinecraftTest() {

    override val testPath: String = TEST_DATA_PATH

    @Test
    @DisplayName("Root Keys")
    fun rootKeys() {
        fixture.testCompletionVariants(
            "rootKeys/mods.toml",
            "modLoader",
            "loaderVersion",
            "license",
            "showAsResourcePack",
            "issueTrackerURL",
            "clientSideOnly",
        )
    }

    @Test
    @DisplayName("Mods Keys")
    fun modsKeys() {
        fixture.testCompletionVariants(
            "modsKeys/mods.toml",
            "modId",
            "version",
            "displayName",
            "updateJSONURL",
            "displayURL",
            "logoFile",
            "logoBlur",
            "credits",
            "authors",
            "displayTest",
            "description",
        )
    }

    @Test
    @DisplayName("Dependencies Keys")
    fun dependenciesKeys() {
        fixture.testCompletionVariants(
            "dependenciesKeys/mods.toml",
            "modId",
            "mandatory",
            "versionRange",
            "ordering",
            "side",
        )
    }

    @Test
    @DisplayName("Mod Dependency Key")
    fun modDependencyKey() {
        fixture.testCompletionVariants("modDependencyKey/mods.toml", "declared_mod_1", "declared_mod_2")
    }

    @Test
    @DisplayName("Boolean Value")
    fun booleanValue() {
        fixture.testCompletionVariants("boolean/mods.toml", "true", "false")
    }

    @Test
    @DisplayName("Display Test Value")
    fun displayTestValue() {
        fixture.testCompletionVariants(
            "displayTestValue/mods.toml",
            "MATCH_VERSION",
            "IGNORE_SERVER_VERSION",
            "IGNORE_ALL_VERSION",
            "NONE",
        )
    }

    @Test
    @DisplayName("Dependency Ordering Value")
    fun orderingValue() {
        fixture.testCompletionVariants("dependencyOrderingValue/mods.toml", "NONE", "BEFORE", "AFTER")
    }

    @Test
    @DisplayName("Dependency Side Value")
    fun sideValue() {
        fixture.testCompletionVariants("dependencySideValue/mods.toml", "BOTH", "CLIENT", "SERVER")
    }

    @Test
    @DisplayName("String Completion From Nothing")
    fun stringCompletionFromNothing() {
        fixture.testCompletion(
            "stringCompletionFromNothing/mods.toml",
            "stringCompletionFromNothing/mods.toml.after",
        )
    }

    @Test
    @DisplayName("String Completion")
    fun stringCompletion() {
        fixture.testCompletion(
            "stringCompletion/mods.toml",
            "stringCompletion/mods.toml.after",
        )
    }
}
