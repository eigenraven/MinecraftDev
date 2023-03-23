/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
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
                if (configuration.project.modules.isEmpty()) {
                    return
                }
                val module = configuration.project.modules.find { module ->
                    MinecraftFacet.getInstance(module)?.isOfType(McpModuleType) == true
                } ?: return
                attachToProcess(handler, module)
            }
        }
    }

    override fun readExternal(runConfiguration: RunConfigurationBase<*>, element: Element) {}

    override fun getEditorTitle(): String? = null
    override fun <P : RunConfigurationBase<*>> createEditor(configuration: P): SettingsEditor<P>? = null
}
