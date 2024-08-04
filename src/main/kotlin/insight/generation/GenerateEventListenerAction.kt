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

package com.demonwav.mcdev.insight.generation

import com.demonwav.mcdev.asset.MCDevBundle
import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.util.findModule
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class GenerateEventListenerAction : CodeInsightAction() {

    private val handler = GenerateEventListenerHandler()

    override fun getHandler(): CodeInsightActionHandler = handler

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.text = MCDevBundle("generate.event_listener.title")
    }

    override fun isValidForFile(
        project: Project,
        editor: Editor,
        file: PsiFile
    ): Boolean {
        val module = file.findModule() ?: return false
        val minecraftFacet = MinecraftFacet.getInstance(module) ?: return false
        val support = minecraftFacet.modules.firstNotNullOfOrNull { it.eventListenerGenSupport } ?: return false
        val caretElement = file.findElementAt(editor.caretModel.offset) ?: return false
        return support.canGenerate(caretElement, editor)
    }
}
