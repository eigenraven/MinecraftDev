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

package com.demonwav.mcdev.platform.neoforge

import com.demonwav.mcdev.insight.generation.GenerationData
import com.demonwav.mcdev.insight.generation.MethodRenderer
import com.demonwav.mcdev.insight.generation.MethodRendererBasedEventListenerGenerationSupport
import com.demonwav.mcdev.platform.neoforge.util.NeoForgeConstants
import com.demonwav.mcdev.util.psiType
import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiTypes

class NeoForgeEventListenerGenerationSupport : MethodRendererBasedEventListenerGenerationSupport() {

    override fun invokeRenderer(
        renderer: MethodRenderer,
        context: PsiElement,
        listenerName: String,
        eventClass: PsiClass,
        data: GenerationData?,
        editor: Editor
    ): String {
        return renderer.renderMethod(
            listenerName,
            listOf("event" to eventClass.psiType),
            setOf(JvmModifier.PUBLIC),
            PsiTypes.voidType(),
            listOf(NeoForgeConstants.SUBSCRIBE_EVENT to emptyList())
        )
    }
}
