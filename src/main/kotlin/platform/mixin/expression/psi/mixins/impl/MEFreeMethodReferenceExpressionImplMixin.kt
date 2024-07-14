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

package com.demonwav.mcdev.platform.mixin.expression.psi.mixins.impl

import com.demonwav.mcdev.platform.mixin.expression.MESourceMatchContext
import com.demonwav.mcdev.platform.mixin.expression.gen.psi.MEExpression
import com.demonwav.mcdev.platform.mixin.expression.gen.psi.MEName
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodReferenceExpression

abstract class MEFreeMethodReferenceExpressionImplMixin(node: ASTNode) : MEExpressionImplMixin(node), MEExpression {
    override fun matchesJava(java: PsiElement, context: MESourceMatchContext): Boolean {
        if (java !is PsiMethodReferenceExpression) {
            return false
        }

        if (java.isConstructor) {
            return false
        }

        val qualifierClass = (java.qualifierType?.type as? PsiClassType)?.resolve() ?: return false

        // check wildcard after checking for the qualifier class, otherwise the reference could have been qualified by
        // an expression.
        val memberName = this.memberName ?: return false
        if (memberName.isWildcard) {
            return true
        }

        val method = java.resolve() as? PsiMethod ?: return false
        return context.getMethods(memberName.text).any { reference ->
            reference.matchMethod(method, qualifierClass)
        }
    }

    override fun getInputExprs() = emptyList<MEExpression>()

    abstract val memberName: MEName?
}
