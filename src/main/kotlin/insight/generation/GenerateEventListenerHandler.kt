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

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.insight.generation.ui.EventGenerationDialog
import com.demonwav.mcdev.platform.AbstractModule
import com.demonwav.mcdev.util.findModule
import com.demonwav.mcdev.util.mapFirstNotNull
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.ide.util.TreeClassChooserFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.refactoring.RefactoringBundle

class GenerateEventListenerHandler : CodeInsightActionHandler {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val module = file.findModule() ?: return
        val facet = MinecraftFacet.getInstance(module) ?: return
        val eventListenerGenSupport = facet.modules.mapFirstNotNull { it.eventListenerGenSupport } ?: return
        val caretElement = file.findElementAt(editor.caretModel.offset) ?: return
        val context = caretElement.context ?: return

        if (!EditorModificationUtil.requestWriting(editor)) {
            return
        }

        val chooser = TreeClassChooserFactory.getInstance(project)
            .createWithInnerClassesScopeChooser(
                RefactoringBundle.message("choose.destination.class"),
                GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false),
                { aClass1 -> isSuperEventListenerAllowed(aClass1, facet) },
                null,
            )

        chooser.showDialog()
        val chosenClass = chooser.selected ?: return

        val relevantModule = facet.modules.asSequence()
            .filter { m -> isSuperEventListenerAllowed(chosenClass, m) }
            .firstOrNull() ?: return

        val chosenClassName = chosenClass.nameIdentifier?.text ?: return

        val generationDialog = EventGenerationDialog(
            editor,
            relevantModule.moduleType.getEventGenerationPanel(chosenClass),
            chosenClassName,
            relevantModule.moduleType.getDefaultListenerName(chosenClass),
        )

        if (!generationDialog.showAndGet()) {
            return
        }

        eventListenerGenSupport.generateEventListener(
            context,
            generationDialog.chosenName,
            chosenClass,
            generationDialog.data,
            editor
        )
    }

    override fun startInWriteAction(): Boolean = false

    private fun isSuperEventListenerAllowed(eventClass: PsiClass, module: AbstractModule): Boolean {
        val supers = eventClass.supers
        for (aSuper in supers) {
            if (module.isEventClassValid(aSuper, null)) {
                return true
            }
            if (isSuperEventListenerAllowed(aSuper, module)) {
                return true
            }
        }
        return false
    }

    private fun isSuperEventListenerAllowed(eventClass: PsiClass, facet: MinecraftFacet): Boolean {
        val supers = eventClass.supers
        for (aSuper in supers) {
            if (facet.isEventClassValidForModule(aSuper)) {
                return true
            }
            if (isSuperEventListenerAllowed(aSuper, facet)) {
                return true
            }
        }
        return false
    }
}
