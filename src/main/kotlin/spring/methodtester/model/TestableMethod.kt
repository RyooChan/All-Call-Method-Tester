package spring.methodtester.model

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

interface TestableMethod {
    val project: Project
    val containingClassQualifiedName: String?
    val name: String
    fun hasAnnotation(annotationFQN: String): Boolean
    val psiElement: PsiElement

    fun getFullyQualifiedClassName(): String {
        return containingClassQualifiedName ?: ""
    }

    fun isTestAnnotated(): Boolean {
        return hasAnnotation("org.junit.jupiter.api.Test")
                || hasAnnotation("org.junit.Test")
    }

    fun isNestedTestClass(): Boolean {
        return false
    }
}