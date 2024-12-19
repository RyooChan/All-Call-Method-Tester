package spring.methodtester.utils

import com.intellij.psi.PsiModifierListOwner
import org.jetbrains.kotlin.psi.KtAnnotated

object AnnotationUtils {
    fun hasAnnotation(element: PsiModifierListOwner, annotationFQN: String): Boolean {
        return element.annotations.any { annotation ->
            annotation.qualifiedName == annotationFQN
        }
    }

    fun hasAnnotation(ktAnnotated: KtAnnotated, annotationSimpleName: String): Boolean {
        return ktAnnotated.annotationEntries.any { entry ->
            entry.shortName?.asString() == annotationSimpleName
        }
    }
}