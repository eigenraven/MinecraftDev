/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.bungeecord.creator

import com.demonwav.mcdev.creator.addTemplates
import com.demonwav.mcdev.creator.buildsystem.AbstractBuildSystemStep
import com.demonwav.mcdev.creator.buildsystem.AbstractRunBuildSystemStep
import com.demonwav.mcdev.creator.buildsystem.BuildSystemSupport
import com.demonwav.mcdev.creator.splitPackage
import com.demonwav.mcdev.creator.step.AbstractLongRunningAssetsStep
import com.demonwav.mcdev.creator.step.AbstractModNameStep
import com.demonwav.mcdev.creator.step.AuthorsStep
import com.demonwav.mcdev.creator.step.DependStep
import com.demonwav.mcdev.creator.step.DescriptionStep
import com.demonwav.mcdev.creator.step.MainClassStep
import com.demonwav.mcdev.creator.step.SoftDependStep
import com.demonwav.mcdev.util.MinecraftTemplates
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key

class BungeeProjectFilesStep(parent: NewProjectWizardStep) : AbstractLongRunningAssetsStep(parent) {
    override val description = "Creating project files"

    override fun setupAssets(project: Project) {
        val mainClass = data.getUserData(MainClassStep.KEY) ?: return
        val (packageName, className) = splitPackage(mainClass)
        val versionRef = data.getUserData(VERSION_REF_KEY) ?: "\${version}"
        val pluginName = data.getUserData(AbstractModNameStep.KEY) ?: return
        val authors = data.getUserData(AuthorsStep.KEY) ?: emptyList()
        val description = data.getUserData(DescriptionStep.KEY) ?: ""
        val depend = data.getUserData(DependStep.KEY) ?: emptyList()
        val softDepend = data.getUserData(SoftDependStep.KEY) ?: emptyList()

        assets.addTemplateProperties(
            "PACKAGE" to packageName,
            "CLASS_NAME" to className,
            "MAIN" to mainClass,
            "VERSION" to versionRef,
            "NAME" to pluginName,
        )

        if (authors.isNotEmpty()) {
            assets.addTemplateProperties(
                "AUTHOR" to authors.joinToString(", ")
            )
        }
        if (description.isNotBlank()) {
            assets.addTemplateProperties(
                "DESCRIPTION" to description
            )
        }
        if (depend.isNotEmpty()) {
            assets.addTemplateProperties(
                "DEPEND" to depend
            )
        }
        if (softDepend.isNotEmpty()) {
            assets.addTemplateProperties(
                "SOFT_DEPEND" to softDepend
            )
        }

        assets.addTemplates(
            project,
            "src/main/resources/bungee.yml" to MinecraftTemplates.BUNGEECORD_PLUGIN_YML_TEMPLATE,
            "src/main/java/${mainClass.replace('.', '/')}.java" to MinecraftTemplates.BUNGEECORD_MAIN_CLASS_TEMPLATE,
        )
    }

    companion object {
        val VERSION_REF_KEY = Key.create<String>("${BungeeProjectFilesStep::class.java.name}.versionRef")
    }
}

class BungeeBuildSystemStep(parent: NewProjectWizardStep) : AbstractBuildSystemStep(parent) {
    override val platformName = "BungeeCord"
}

class BungeePostBuildSystemStep(
    parent: NewProjectWizardStep
) : AbstractRunBuildSystemStep(parent, BungeeBuildSystemStep::class.java) {
    override val step = BuildSystemSupport.POST_STEP
}
