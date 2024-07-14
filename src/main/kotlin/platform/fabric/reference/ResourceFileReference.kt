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

package com.demonwav.mcdev.platform.fabric.reference

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.util.SourceType
import com.demonwav.mcdev.util.findModule
import com.demonwav.mcdev.util.manipulator
import com.demonwav.mcdev.util.mapFirstNotNull
import com.demonwav.mcdev.util.reference.InspectionReference
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.IncorrectOperationException
import com.intellij.util.ProcessingContext
import org.jetbrains.jps.model.java.JavaResourceRootType

class ResourceFileReference(
    private val description: String,
    private val filenamePattern: Regex? = null
) : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        return arrayOf(Reference(description, element as JsonStringLiteral))
    }

    private inner class Reference(desc: String, element: JsonStringLiteral) :
        PsiReferenceBase<JsonStringLiteral>(element),
        InspectionReference {
        override val description = desc
        override val unresolved = resolve() == null

        override fun resolve(): PsiElement? {
            fun findFileIn(module: Module): PsiFile? {
                val facet = MinecraftFacet.getInstance(module) ?: return null
                val virtualFile = facet.findFile(element.value, SourceType.RESOURCE) ?: return null
                return PsiManager.getInstance(element.project).findFile(virtualFile)
            }

            val module = element.findModule() ?: return null
            return findFileIn(module)
                ?: ModuleRootManager.getInstance(module)
                    .getDependencies(false)
                    .mapFirstNotNull(::findFileIn)
                ?: ModuleManager.getInstance(element.project)
                    .getModuleDependentModules(module)
                    .mapFirstNotNull(::findFileIn)
        }

        override fun bindToElement(newTarget: PsiElement): PsiElement? {
            if (newTarget !is PsiFile) {
                throw IncorrectOperationException("Cannot target $newTarget")
            }
            val manipulator = element.manipulator ?: return null
            return manipulator.handleContentChange(element, manipulator.getRangeInElement(element), newTarget.name)
        }

        override fun getVariants(): Array<out Any?> {
            if (filenamePattern == null) {
                return emptyArray()
            }

            val module = element.findModule() ?: return emptyArray()
            val variants = mutableListOf<Any>()
            val relevantModules = ModuleManager.getInstance(element.project).getModuleDependentModules(module) + module
            runReadAction {
                val relevantRoots = relevantModules.flatMap {
                    it.rootManager.getSourceRoots(JavaResourceRootType.RESOURCE)
                }
                for (roots in relevantRoots) {
                    for (child in roots.children) {
                        val relativePath = child.path.removePrefix(roots.path)
                        val testRelativePath = "/$relativePath"
                        if (testRelativePath.matches(filenamePattern)) {
                            variants.add(child.findPsiFile(element.project) ?: relativePath)
                        }
                    }
                }
            }
            return variants.toTypedArray()
        }
    }
}
