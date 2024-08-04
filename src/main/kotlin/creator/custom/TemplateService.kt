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

package com.demonwav.mcdev.creator.custom

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.platform.util.coroutines.namedChildScope
import com.intellij.util.application
import kotlinx.coroutines.CoroutineScope

@Service
class TemplateService(private val scope: CoroutineScope) {

    private val pendingActions: MutableMap<String, suspend () -> Unit> = mutableMapOf()

    fun registerFinalizerAction(project: Project, action: suspend () -> Unit) {
        if (pendingActions.containsKey(project.name)) {
            thisLogger().error("More than one finalizer action registered for project $project")
            return
        }

        pendingActions[project.locationHash] = action
    }

    suspend fun executeFinalizer(project: Project) {
        pendingActions.remove(project.locationHash)?.invoke()
    }

    @Suppress("UnstableApiUsage") // namedChildScope is Internal right now but has been promoted to Stable in 2024.2
    fun scope(name: String): CoroutineScope = scope.namedChildScope(name)

    companion object {

        val instance: TemplateService
            get() = application.service()
    }
}

class TemplateProjectFinalizerActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        TemplateService.instance.executeFinalizer(project)
    }
}
