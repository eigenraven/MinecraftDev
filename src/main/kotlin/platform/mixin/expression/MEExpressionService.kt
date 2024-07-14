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

package com.demonwav.mcdev.platform.mixin.expression

import com.demonwav.mcdev.platform.mixin.util.toPsiType
import com.demonwav.mcdev.util.descriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiType
import com.llamalad7.mixinextras.expression.impl.ExpressionService
import com.llamalad7.mixinextras.expression.impl.flow.FlowContext
import org.objectweb.asm.Type

object MEExpressionService : ExpressionService() {
    override fun getCommonSuperClass(ctx: FlowContext, type1: Type, type2: Type): Type {
        ctx as MEFlowContext
        val elementFactory = JavaPsiFacade.getElementFactory(ctx.project)
        return Type.getType(
            getCommonSuperClass(
                ctx.project,
                type1.toPsiType(elementFactory) as PsiClassType,
                type2.toPsiType(elementFactory) as PsiClassType
            )?.descriptor ?: error("Could not intersect types $type1 and $type2!")
        )
    }

    // Copied from ClassInfo
    private fun getCommonSuperClass(
        project: Project,
        type1: PsiType,
        type2: PsiType
    ): PsiClassType? {
        val left = (type1 as? PsiClassType)?.resolve() ?: return null
        val right = (type2 as? PsiClassType)?.resolve() ?: return null

        fun objectType() = PsiType.getJavaLangObject(PsiManager.getInstance(project), left.resolveScope)
        fun PsiClass.type() = PsiElementFactory.getInstance(project).createType(this)

        if (left.isInheritor(right, true)) {
            return right.type()
        }
        if (right.isInheritor(left, true)) {
            return left.type()
        }
        if (left.isInterface || right.isInterface) {
            return objectType()
        }

        return generateSequence(left) { it.superClass }
            .firstOrNull { right.isInheritor(it, true) }
            ?.type()
            ?: objectType()
    }
}
