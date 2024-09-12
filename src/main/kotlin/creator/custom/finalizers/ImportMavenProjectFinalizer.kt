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

package com.demonwav.mcdev.creator.custom.finalizers

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import java.nio.file.Path
import org.jetbrains.idea.maven.buildtool.MavenSyncSpec
import org.jetbrains.idea.maven.project.MavenProjectsManager

class ImportMavenProjectFinalizer : CreatorFinalizer {

    override suspend fun execute(
        context: WizardContext,
        project: Project,
        properties: Map<String, Any>,
        templateProperties: Map<String, Any?>
    ) {
        val projectDir = context.projectFileDirectory
        val pomFile = VfsUtil.findFile(Path.of(projectDir).resolve("pom.xml"), true)
            ?: return

        thisLogger().info("Invoking import on EDT pomFile = ${pomFile.path}")
        val projectsManager = MavenProjectsManager.getInstance(project)
        projectsManager.addManagedFiles(listOf(pomFile))
        projectsManager.updateAllMavenProjects(MavenSyncSpec.incremental("ImportMavenProjectFinalizer", false))

        thisLogger().info("Import finished")
    }
}
