package spring.methodtester.model

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import spring.methodtester.utils.AnnotationUtils

class JavaTestableMethod(private val psiMethod: PsiMethod) : TestableMethod {
    override val project: Project
        get() = psiMethod.project
    override val containingClassQualifiedName: String?
        get() = psiMethod.containingClass?.qualifiedName
    override val name: String
        get() = psiMethod.name
    override fun hasAnnotation(annotationFQN: String): Boolean {
        return AnnotationUtils.hasAnnotation(psiMethod, annotationFQN)
    }
    override val psiElement: PsiElement
        get() = psiMethod

    override fun isNestedTestClass(): Boolean {
        val containingClass = psiMethod.containingClass
        return containingClass?.let {
            AnnotationUtils.hasAnnotation(it, "org.junit.jupiter.api.Nested")
        } ?: false
    }
}