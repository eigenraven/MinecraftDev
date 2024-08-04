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

package com.demonwav.mcdev.platform.sponge

import com.demonwav.mcdev.insight.generation.GenerationData
import com.demonwav.mcdev.insight.generation.MethodRenderer
import com.demonwav.mcdev.insight.generation.MethodRendererBasedEventListenerGenerationSupport
import com.demonwav.mcdev.platform.sponge.generation.SpongeGenerationData
import com.demonwav.mcdev.platform.sponge.util.SpongeConstants
import com.demonwav.mcdev.util.psiType
import com.intellij.lang.jvm.JvmModifier
import com.intellij.lang.jvm.actions.AnnotationAttributeRequest
import com.intellij.lang.jvm.actions.constantAttribute
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiTypes

class SpongeEventListenerGenerationSupport : MethodRendererBasedEventListenerGenerationSupport() {

    override fun invokeRenderer(
        renderer: MethodRenderer,
        context: PsiElement,
        listenerName: String,
        eventClass: PsiClass,
        data: GenerationData?,
        editor: Editor
    ): String {
        require(data is SpongeGenerationData)

        val handlerAnnotations = mutableListOf<Pair<String, List<AnnotationAttributeRequest>>>()

        val handlerAttributes = mutableListOf<AnnotationAttributeRequest>()
        if (data.eventOrder != "DEFAULT") {
            handlerAttributes.add(
                constantAttribute("order", SpongeConstants.ORDER + '.' + data.eventOrder)
            )
        }

        handlerAnnotations.add(SpongeConstants.LISTENER_ANNOTATION to handlerAttributes)

        if (!data.isIgnoreCanceled) {
            handlerAnnotations.add(
                SpongeConstants.IS_CANCELLED_ANNOTATION to listOf(
                    constantAttribute("value", "org.spongepowered.api.util.Tristate.UNDEFINED")
                )
            )
        }

        return renderer.renderMethod(
            listenerName,
            listOf("event" to eventClass.psiType),
            setOf(JvmModifier.PUBLIC),
            PsiTypes.voidType(),
            listOf(
                SpongeConstants.LISTENER_ANNOTATION to handlerAttributes
            )
        )
    }
}
