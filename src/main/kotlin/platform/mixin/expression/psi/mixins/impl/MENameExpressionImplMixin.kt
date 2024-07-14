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
import com.demonwav.mcdev.platform.mixin.expression.gen.psi.impl.MEExpressionImpl
import com.demonwav.mcdev.platform.mixin.handlers.injectionPoint.QualifiedMember
import com.demonwav.mcdev.platform.mixin.util.LocalVariables
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.PsiVariable
import com.intellij.psi.util.PsiUtil

abstract class MENameExpressionImplMixin(node: ASTNode) : MEExpressionImpl(node) {
    override fun matchesJava(java: PsiElement, context: MESourceMatchContext): Boolean {
        if (MEName.isWildcard) {
            return true
        }

        if (java !is PsiReferenceExpression) {
            return false
        }
        val variable = java.resolve() as? PsiVariable ?: return false

        val name = MEName.text

        // match against fields
        if (variable is PsiField) {
            val qualifier = QualifiedMember.resolveQualifier(java) ?: variable.containingClass ?: return false
            return context.getFields(name).any { it.matchField(variable, qualifier) }
        }

        // match against local variables
        val sourceArgs by lazy {
            LocalVariables.guessLocalsAt(java, true, !PsiUtil.isAccessedForWriting(java))
        }
        val sourceVariables by lazy {
            LocalVariables.guessLocalsAt(java, false, !PsiUtil.isAccessedForWriting(java))
        }
        for (localInfo in context.getLocalInfos(name)) {
            val sourceLocals = if (localInfo.argsOnly) sourceArgs else sourceVariables
            for (local in localInfo.matchSourceLocals(sourceLocals)) {
                if (local.variable == variable) {
                    return true
                }
            }
        }

        return false
    }

    override fun getInputExprs() = emptyList<MEExpression>()

    @Suppress("PropertyName")
    protected abstract val MEName: MEName
}
