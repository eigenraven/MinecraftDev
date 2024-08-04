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

package com.demonwav.mcdev.platform.bungeecord

import com.demonwav.mcdev.insight.generation.EventGenHelper
import com.demonwav.mcdev.insight.generation.GenerationData
import com.demonwav.mcdev.insight.generation.MethodRenderer
import com.demonwav.mcdev.insight.generation.MethodRendererBasedEventListenerGenerationSupport
import com.demonwav.mcdev.platform.bungeecord.generation.BungeeCordGenerationData
import com.demonwav.mcdev.platform.bungeecord.util.BungeeCordConstants
import com.demonwav.mcdev.util.psiType
import com.intellij.lang.jvm.JvmModifier
import com.intellij.lang.jvm.actions.AnnotationAttributeRequest
import com.intellij.lang.jvm.actions.constantAttribute
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiTypes

class BungeeCordEventListenerGenerationSupport : MethodRendererBasedEventListenerGenerationSupport() {

    override fun preGenerationProcess(
        context: PsiElement,
        data: GenerationData?
    ) {
        require(data is BungeeCordGenerationData)

        EventGenHelper.COLLECTOR.forLanguage(context.language)
            .addImplements(context, BungeeCordConstants.LISTENER_CLASS)
    }

    override fun invokeRenderer(
        renderer: MethodRenderer,
        context: PsiElement,
        listenerName: String,
        eventClass: PsiClass,
        data: GenerationData?,
        editor: Editor
    ): String {
        require(data is BungeeCordGenerationData)

        val handlerAttributes = mutableListOf<AnnotationAttributeRequest>()
        if (data.eventPriority != "NORMAL") {
            handlerAttributes.add(
                constantAttribute("priority", BungeeCordConstants.EVENT_PRIORITY_CLASS + '.' + data.eventPriority)
            )
        }

        return renderer.renderMethod(
            listenerName,
            listOf("event" to eventClass.psiType),
            setOf(JvmModifier.PUBLIC),
            PsiTypes.voidType(),
            listOf(
                BungeeCordConstants.HANDLER_ANNOTATION to handlerAttributes
            )
        )
    }
}
