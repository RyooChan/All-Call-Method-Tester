package spring.methodtester

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil

class RunRelatedTestsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val psiFile: PsiFile? = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE)

        psiFile?.let { file ->
            editor?.let { editor ->
                val offset = editor.caretModel.offset
                val elementAtCaret: PsiElement? = file.findElementAt(offset)
                val method = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod::class.java)

                method?.let {
                    TestRunnerUtil.runRelatedTests(it)
                } ?: run {
                    Messages.showMessageDialog(
                        event.project,
                        "No method at caret position.",
                        "Information",
                        Messages.getInformationIcon()
                    )
                }
            } ?: run {
                Messages.showMessageDialog(
                    event.project,
                    "Editor not available.",
                    "Information",
                    Messages.getInformationIcon()
                )
            }
        } ?: run {
            Messages.showMessageDialog(
                event.project,
                "File not available.",
                "Information",
                Messages.getInformationIcon()
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val psiFile = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE)

        val method = editor?.let { editor ->
            psiFile?.let { file ->
                val offset = editor.caretModel.offset
                val elementAtCaret: PsiElement? = file.findElementAt(offset)
                PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod::class.java)
            }
        }

        // Set visible if a method is found at caret
        e.presentation.isEnabledAndVisible = method != null
    }
}