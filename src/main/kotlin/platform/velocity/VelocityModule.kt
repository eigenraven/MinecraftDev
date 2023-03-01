/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2023 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.velocity

import com.demonwav.mcdev.asset.PlatformAssets
import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.insight.generation.GenerationData
import com.demonwav.mcdev.platform.AbstractModule
import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.platform.velocity.generation.VelocityGenerationData
import com.demonwav.mcdev.platform.velocity.util.VelocityConstants
import com.demonwav.mcdev.platform.velocity.util.VelocityConstants.SUBSCRIBE_ANNOTATION
import com.demonwav.mcdev.util.createVoidMethodWithParameterType
import com.intellij.lang.jvm.JvmModifier
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.toUElementOfType

class VelocityModule(facet: MinecraftFacet) : AbstractModule(facet) {
    override val moduleType = VelocityModuleType
    override val type = PlatformType.VELOCITY
    override val icon = PlatformAssets.VELOCITY_ICON

    override fun isEventClassValid(eventClass: PsiClass, method: PsiMethod?): Boolean = true

    override fun generateEventListenerMethod(
        containingClass: PsiClass,
        chosenClass: PsiClass,
        chosenName: String,
        data: GenerationData?,
    ): PsiMethod? {
        val method = createVoidMethodWithParameterType(project, chosenName, chosenClass) ?: return null
        val modifierList = method.modifierList

        val subscribeAnnotation = modifierList.addAnnotation(SUBSCRIBE_ANNOTATION)

        val generationData = data as VelocityGenerationData

        if (generationData.eventOrder != "NORMAL") {
            val value = JavaPsiFacade.getElementFactory(project)
                .createExpressionFromText(
                    "com.velocitypowered.api.event.PostOrder." + generationData.eventOrder,
                    subscribeAnnotation,
                )

            subscribeAnnotation.setDeclaredAttributeValue("order", value)
        }

        return method
    }

    override fun shouldShowPluginIcon(element: PsiElement?): Boolean {
        val identifier = element?.toUElementOfType<UIdentifier>()
            ?: return false

        val psiClass = identifier.uastParent as? UClass
            ?: return false

        return !psiClass.hasModifier(JvmModifier.ABSTRACT) &&
            psiClass.findAnnotation(VelocityConstants.PLUGIN_ANNOTATION) != null
    }
}
