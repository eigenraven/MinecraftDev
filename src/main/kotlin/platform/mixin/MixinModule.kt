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

package com.demonwav.mcdev.platform.mixin

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.facet.MinecraftFacetDetector
import com.demonwav.mcdev.platform.AbstractModule
import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.platform.mixin.config.MixinConfig
import com.demonwav.mcdev.platform.mixin.config.MixinConfigFileType
import com.demonwav.mcdev.platform.mixin.framework.MIXIN_LIBRARY_KIND
import com.demonwav.mcdev.util.SemanticVersion
import com.demonwav.mcdev.util.nullable
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import javax.swing.Icon

class MixinModule(facet: MinecraftFacet) : AbstractModule(facet) {
    val mixinVersion by nullable {
        var version = MinecraftFacetDetector.getLibraryVersions(facet.module)[MIXIN_LIBRARY_KIND]
            ?: return@nullable null
        // fabric mixin uses the format "0.10.4+mixin.0.8.4", return the original string otherwise.
        version = version.substringAfter("+mixin.")
        SemanticVersion.parse(version)
    }

    override val moduleType = MixinModuleType
    override val type = PlatformType.MIXIN
    override val icon: Icon? = null

    companion object {
        private val mixinFileTypes = listOf(MixinConfigFileType.Json, MixinConfigFileType.Json5)

        fun getMixinConfigs(
            project: Project,
            scope: GlobalSearchScope,
        ): Collection<MixinConfig> {
            return mixinFileTypes
                .flatMap { FileTypeIndex.getFiles(it, scope) }
                .mapNotNull { file ->
                    (PsiManager.getInstance(project).findFile(file) as? JsonFile)?.topLevelValue as? JsonObject
                }.map { jsonObject ->
                    MixinConfig(project, jsonObject)
                }
        }

        fun getAllMixinClasses(
            project: Project,
            scope: GlobalSearchScope,
        ): Collection<PsiClass> {
            return getMixinConfigs(project, scope).asSequence()
                .flatMap { (it.qualifiedMixins + it.qualifiedClient + it.qualifiedServer).asSequence() }
                .filterNotNull()
                .map { it.replace('$', '.') }
                .distinct()
                .flatMap { JavaPsiFacade.getInstance(project).findClasses(it, scope).asSequence() }
                .toList()
        }

        fun getBestWritableConfigForMixinClass(
            project: Project,
            scope: GlobalSearchScope,
            mixinClassName: String,
        ): MixinConfig? {
            return getMixinConfigs(project, scope)
                .filter { it.isWritable && mixinClassName.startsWith("${it.pkg}.") }
                .maxByOrNull { it.pkg?.length ?: 0 }
        }
    }
}

