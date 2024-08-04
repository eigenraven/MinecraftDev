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

package com.demonwav.mcdev.insight.generation

import com.intellij.lang.jvm.JvmModifier
import com.intellij.lang.jvm.actions.AnnotationAttributeRequest
import com.intellij.lang.jvm.actions.AnnotationAttributeValueRequest
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypes

interface MethodRenderer {

    fun renderMethod(
        name: String,
        parameters: List<Pair<String, PsiType>>,
        modifiers: Set<JvmModifier>,
        returnType: PsiType,
        annotations: List<Pair<String, List<AnnotationAttributeRequest>>>
    ): String

    companion object {

        val byLanguage = mapOf(
            "JAVA" to JavaRenderer,
            "kotlin" to KotlinRenderer,
        )
    }

    private object JavaRenderer : MethodRenderer {

        override fun renderMethod(
            name: String,
            parameters: List<Pair<String, PsiType>>,
            modifiers: Set<JvmModifier>,
            returnType: PsiType,
            annotations: List<Pair<String, List<AnnotationAttributeRequest>>>
        ): String = buildString {
            for ((fqn, attributes) in annotations) {
                renderAnnotation(fqn, attributes)
                appendLine()
            }

            when {
                JvmModifier.PUBLIC in modifiers -> append("public ")
                JvmModifier.PRIVATE in modifiers -> append("private ")
                JvmModifier.PROTECTED in modifiers -> append("protected ")
            }

            when {
                JvmModifier.STATIC in modifiers -> append("static ")
                JvmModifier.ABSTRACT in modifiers -> append("abstract ")
                JvmModifier.FINAL in modifiers -> append("final ")
            }

            append(returnType.canonicalText)
            append(' ')
            append(name)
            parameters.joinTo(this, prefix = "(", postfix = ")") { (paramName, paramType) ->
                paramType.canonicalText + " " + paramName
            }
            appendLine("{}")
        }

        fun StringBuilder.renderAnnotation(fqn: String, attributes: List<AnnotationAttributeRequest>) {
            append('@')
            append(fqn)
            if (attributes.isNotEmpty()) {
                attributes.joinTo(this, prefix = "(", postfix = ")") { attribute ->
                    attribute.name + " = " + renderAnnotationValue(attribute.value)
                }
            }
        }

        fun renderAnnotationValue(value: AnnotationAttributeValueRequest): String = when (value) {
            is AnnotationAttributeValueRequest.PrimitiveValue -> value.value.toString()
            is AnnotationAttributeValueRequest.StringValue -> '"' + value.value + '"'
            is AnnotationAttributeValueRequest.ClassValue -> value.classFqn + ".class"
            is AnnotationAttributeValueRequest.ConstantValue -> value.text
            is AnnotationAttributeValueRequest.NestedAnnotation -> buildString {
                renderAnnotation(value.annotationRequest.qualifiedName, value.annotationRequest.attributes)
            }

            is AnnotationAttributeValueRequest.ArrayValue ->
                value.members.joinToString(prefix = "{", postfix = "}", transform = ::renderAnnotationValue)
        }
    }

    private object KotlinRenderer : MethodRenderer {

        override fun renderMethod(
            name: String,
            parameters: List<Pair<String, PsiType>>,
            modifiers: Set<JvmModifier>,
            returnType: PsiType,
            annotations: List<Pair<String, List<AnnotationAttributeRequest>>>
        ): String = buildString {
            for ((fqn, attributes) in annotations) {
                renderAnnotation(fqn, attributes)
                appendLine()
            }

            if (JvmModifier.STATIC in modifiers) {
                appendLine("@JvmStatic")
            }

            when {
                // Skipping public as it is the default visibility
                JvmModifier.PRIVATE in modifiers -> append("private ")
                JvmModifier.PROTECTED in modifiers -> append("protected ")
                JvmModifier.PACKAGE_LOCAL in modifiers -> append("internal ") // Close enough
            }

            when {
                JvmModifier.ABSTRACT in modifiers -> append("abstract ")
                JvmModifier.FINAL in modifiers -> append("final ")
            }

            append("fun ")
            append(name)
            parameters.joinTo(this, prefix = "(", postfix = ")") { (paramName, paramType) ->
                paramName + ": " + paramType.canonicalText
            }

            if (returnType != PsiTypes.voidType()) {
                append(": ")
                append(returnType.canonicalText)
            }

            appendLine("{}")
        }

        fun StringBuilder.renderAnnotation(fqn: String, attributes: List<AnnotationAttributeRequest>) {
            append('@')
            append(fqn)
            if (attributes.isNotEmpty()) {
                attributes.joinTo(this, prefix = "(", postfix = ")") { attribute ->
                    attribute.name + " = " + renderAnnotationValue(attribute.value)
                }
            }
        }

        fun renderAnnotationValue(value: AnnotationAttributeValueRequest): String = when (value) {
            is AnnotationAttributeValueRequest.PrimitiveValue -> value.value.toString()
            is AnnotationAttributeValueRequest.StringValue -> '"' + value.value + '"'
            is AnnotationAttributeValueRequest.ClassValue -> value.classFqn + "::class"
            is AnnotationAttributeValueRequest.ConstantValue -> value.text
            is AnnotationAttributeValueRequest.NestedAnnotation -> buildString {
                renderAnnotation(value.annotationRequest.qualifiedName, value.annotationRequest.attributes)
            }

            is AnnotationAttributeValueRequest.ArrayValue ->
                value.members.joinToString(prefix = "[", postfix = "]", transform = ::renderAnnotationValue)
        }
    }
}
