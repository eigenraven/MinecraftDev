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

import com.demonwav.mcdev.platform.mixin.expression.gen.psi.MEExpressionTypes
import com.demonwav.mcdev.platform.mixin.expression.gen.psi.MEName
import com.demonwav.mcdev.platform.mixin.expression.psi.mixins.MENameMixin
import com.demonwav.mcdev.platform.mixin.expression.reference.MEDefinitionReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference

abstract class MENameImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), MENameMixin {
    override val isWildcard get() = node.firstChildNode.elementType == MEExpressionTypes.TOKEN_WILDCARD
    override val identifierElement get() = if (isWildcard) null else firstChild

    override fun getReference(): PsiReference? {
        if (isWildcard) {
            return null
        }
        return MEDefinitionReference(this as MEName)
    }
}
