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

package com.demonwav.mcdev.util

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.platform.mcp.McpModuleType
import com.intellij.execution.RunConfigurationExtension
import com.intellij.execution.configurations.DebuggingRunnerData
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.modules
import org.jdom.Element
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration

// RFG Patches: Support gradle run configurations in RFG projects, not just IJ native ones
abstract class ModuleDebugRunConfigurationExtension : RunConfigurationExtension() {

    override fun isApplicableFor(configuration: RunConfigurationBase<*>): Boolean {
        return configuration is ModuleBasedConfiguration<*, *> || configuration is GradleRunConfiguration
    }

    override fun <T : RunConfigurationBase<*>> updateJavaParameters(
        configuration: T,
        params: JavaParameters,
        runnerSettings: RunnerSettings?,
    ) {
    }

    protected abstract fun attachToProcess(handler: ProcessHandler, module: Module)

    override fun attachToProcess(
        configuration: RunConfigurationBase<*>,
        handler: ProcessHandler,
        runnerSettings: RunnerSettings?,
    ) {
        // Check if we are in a debug run
        if (runnerSettings !is DebuggingRunnerData) {
            return
        }

        when (configuration) {
            is ModuleBasedConfiguration<*, *> -> {
                val module = configuration.configurationModule.module ?: return
                attachToProcess(handler, module)
            }
            is GradleRunConfiguration -> {
                // Loose way to confirm we are in an MCP project, this is fine for now because we don't rely on specific
                // information to ungrab (like MC version)
                // Ideally we would find the module matching the run's sourceSet as defined in the build script
                val module = configuration.project.modules.firstOrNull {
                    MinecraftFacet.getInstance(it)?.isOfType(McpModuleType) == true
                } ?: return
                attachToProcess(handler, module)
            }
        }
    }

    override fun readExternal(runConfiguration: RunConfigurationBase<*>, element: Element) {}

    override fun getEditorTitle(): String? = null
    override fun <P : RunConfigurationBase<*>> createEditor(configuration: P): SettingsEditor<P>? = null
}
