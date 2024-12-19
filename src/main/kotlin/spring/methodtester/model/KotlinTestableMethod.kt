package spring.methodtester.model

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import spring.methodtester.utils.AnnotationUtils

class KotlinTestableMethod(val ktFunction: KtFunction) : TestableMethod {
    override val project: Project
        get() = ktFunction.project
    override val containingClassQualifiedName: String?
        get() = ktFunction.containingClass()?.fqName?.asString()
    override val name: String
        get() = ktFunction.name ?: ""
    override fun hasAnnotation(annotationFQN: String): Boolean {
        return ktFunction.annotationEntries.any { entry ->
            entry.shortName?.asString() == annotationFQN.substringAfterLast('.') &&
                    entry.containingFile.language.`is`(org.jetbrains.kotlin.idea.KotlinLanguage.INSTANCE)
        }
    }
    override val psiElement: PsiElement
        get() = ktFunction

    override fun isNestedTestClass(): Boolean {
        val containingClass = ktFunction.containingClass()
        return containingClass?.let {
            AnnotationUtils.hasAnnotation(it, "Nested")
        } ?: false
    }
}