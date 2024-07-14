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

package com.demonwav.mcdev.util

import com.demonwav.mcdev.platform.mixin.reference.MixinSelector
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import java.io.Serializable
import org.objectweb.asm.Type

/**
 * Represents a reference to a class member (a method or a field). It may
 * resolve to multiple members if [matchAllNames] or [matchAllDescs] is set or if the member is
 * not full qualified.
 */
data class MemberReference(
    val name: String,
    val descriptor: String? = null,
    override val owner: String? = null,
    val matchAllNames: Boolean = false,
    val matchAllDescs: Boolean = false,
) : Serializable, MixinSelector {

    init {
        assert(owner?.contains('/') != true)
    }

    val withoutDescriptor
        get() = if (this.descriptor == null) {
            this
        } else {
            copy(descriptor = null)
        }

    val withoutOwner
        get() = if (this.owner == null) {
            this
        } else {
            copy(owner = null)
        }

    override val methodDescriptor = descriptor?.takeIf { it.contains("(") }
    override val fieldDescriptor = descriptor?.takeUnless { it.contains("(") }
    override val displayName = name

    val presentableText: String get() = buildString {
        if (owner != null) {
            append(owner.substringAfterLast('.'))
            append('.')
        }
        append(name)
        if (descriptor != null && descriptor.startsWith("(")) {
            append('(')
            append(Type.getArgumentTypes(descriptor).joinToString { it.className.substringAfterLast('.') })
            append(')')
        }
    }

    override fun canEverMatch(name: String): Boolean {
        return matchAllNames || this.name == name
    }

    private fun matchOwner(clazz: String): Boolean {
        assert(!clazz.contains('.'))
        return this.owner == null || this.owner == clazz.replace('/', '.')
    }

    override fun matchField(owner: String, name: String, desc: String): Boolean {
        assert(!owner.contains('.'))
        return (this.matchAllNames || this.name == name) &&
            matchOwner(owner) &&
            (this.descriptor == null || this.descriptor == desc)
    }

    override fun matchMethod(owner: String, name: String, desc: String): Boolean {
        assert(!owner.contains('.'))
        return (this.matchAllNames || this.name == name) &&
            matchOwner(owner) &&
            (this.descriptor == null || this.descriptor == desc)
    }

    companion object {
        fun parse(value: String): MemberReference? {
            val reference = value.replace(" ", "")
            val owner: String?

            var pos = reference.lastIndexOf('.')
            if (pos != -1) {
                // Everything before the dot is the qualifier/owner
                owner = reference.substring(0, pos).replace('/', '.')
            } else {
                pos = reference.indexOf(';')
                if (pos != -1 && reference.startsWith('L')) {
                    val internalOwner = reference.substring(1, pos)
                    if (!StringUtil.isJavaIdentifier(internalOwner.replace('/', '_'))) {
                        // Invalid: Qualifier should only contain slashes
                        return null
                    }

                    owner = internalOwner.replace('/', '.')

                    // if owner is all there is to the selector, match anything with the owner
                    if (pos == reference.length - 1) {
                        return MemberReference("", null, owner, matchAllNames = true, matchAllDescs = true)
                    }
                } else {
                    // No owner/qualifier specified
                    pos = -1
                    owner = null
                }
            }

            val descriptor: String?
            val name: String
            val matchAllNames = reference.getOrNull(pos + 1) == '*'
            val matchAllDescs: Boolean

            // Find descriptor separator
            val methodDescPos = reference.indexOf('(', pos + 1)
            if (methodDescPos != -1) {
                // Method descriptor
                descriptor = reference.substring(methodDescPos)
                name = reference.substring(pos + 1, methodDescPos)
                matchAllDescs = false
            } else {
                val fieldDescPos = reference.indexOf(':', pos + 1)
                if (fieldDescPos != -1) {
                    descriptor = reference.substring(fieldDescPos + 1)
                    name = reference.substring(pos + 1, fieldDescPos)
                    matchAllDescs = false
                } else {
                    descriptor = null
                    matchAllDescs = reference.endsWith('*')
                    name = if (matchAllDescs) {
                        reference.substring(pos + 1, reference.lastIndex)
                    } else {
                        reference.substring(pos + 1)
                    }
                }
            }

            if (!matchAllNames && !StringUtil.isJavaIdentifier(name) && name != "<init>" && name != "<clinit>") {
                return null
            }

            return MemberReference(if (matchAllNames) "*" else name, descriptor, owner, matchAllNames, matchAllDescs)
        }
    }
}

// Class

fun PsiClass.findMethods(member: MixinSelector, checkBases: Boolean = false): Sequence<PsiMethod> {
    val methods = if (checkBases) {
        allMethods.asSequence()
    } else {
        methods.asSequence()
    } + constructors
    return methods.filter { member.matchMethod(it, this) }
}

fun PsiClass.findField(selector: MixinSelector, checkBases: Boolean = false): PsiField? {
    val fields = if (checkBases) {
        allFields.toList()
    } else {
        fields.toList()
    }
    return fields.firstOrNull { selector.matchField(it, this) }
}

// Method

val PsiMethod.memberReference
    get() = MemberReference(internalName, descriptor)

val PsiMethod.qualifiedMemberReference
    get() = MemberReference(internalName, descriptor, containingClass?.fullQualifiedName)

fun PsiMethod.getQualifiedMemberReference(owner: PsiClass): MemberReference {
    return getQualifiedMemberReference(owner.fullQualifiedName)
}

fun PsiMethod.getQualifiedMemberReference(owner: String?): MemberReference {
    return MemberReference(internalName, descriptor, owner)
}

fun PsiMethod?.isSameReference(reference: PsiMethod?): Boolean =
    this != null && (this === reference || qualifiedMemberReference == reference?.qualifiedMemberReference)

// Field
val PsiField.simpleMemberReference
    get() = MemberReference(name)

val PsiField.memberReference
    get() = MemberReference(name, descriptor)

val PsiField.simpleQualifiedMemberReference
    get() = MemberReference(name, null, containingClass!!.fullQualifiedName)

val PsiField.qualifiedMemberReference
    get() = MemberReference(name, descriptor, containingClass!!.fullQualifiedName)

fun PsiField.getQualifiedMemberReference(owner: PsiClass): MemberReference {
    return getQualifiedMemberReference(owner.fullQualifiedName)
}

fun PsiField.getQualifiedMemberReference(owner: String?): MemberReference {
    return MemberReference(name, descriptor, owner)
}
