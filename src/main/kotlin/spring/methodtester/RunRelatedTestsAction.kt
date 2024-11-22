package spring.methodtester

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod

class RunRelatedTestsAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT)

        when (psiElement) {
            is PsiMethod -> {
                TestRunnerUtil.runRelatedTests(psiElement)
            }
            is PsiClass -> {
                TestRunnerUtil.runTestsForAllMethodsInClass(psiElement)
            }
            else -> {
                Messages.showMessageDialog(
                    project,
                    "No method or class selected.",
                    "Information",
                    Messages.getInformationIcon()
                )
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        if (project == null || psiElement == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        e.presentation.isEnabledAndVisible = (psiElement is PsiMethod || psiElement is PsiClass)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}