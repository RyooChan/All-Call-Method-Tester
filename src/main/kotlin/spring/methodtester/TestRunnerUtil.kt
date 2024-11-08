package spring.methodtester

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.openapi.ui.Messages
import com.intellij.execution.junit.JUnitConfiguration
import com.intellij.execution.junit.JUnitConfigurationType
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil

object TestRunnerUtil {

    fun runRelatedTests(method: PsiMethod) {
        val project = method.project
        val relatedTests = findAllRelatedTests(method)

        if (relatedTests.isEmpty()) {
            Messages.showMessageDialog(
                project,
                "No related tests found for this method.",
                "Information",
                Messages.getInformationIcon()
            )
            return
        }

        runMultipleTests(project, relatedTests)
    }

    private fun runMultipleTests(project: Project, testMethods: Set<PsiMethod>) {
        val runManager = RunManager.getInstance(project)
        val junitConfigurationType = ConfigurationTypeUtil.findConfigurationType(JUnitConfigurationType::class.java)
        val configurationFactory = junitConfigurationType.configurationFactories.firstOrNull()
            ?: throw IllegalStateException("JUnit configuration factory not found")

        val settings = runManager.createConfiguration("Run All Related Tests", configurationFactory)
        val configuration = settings.configuration as JUnitConfiguration
        val data = configuration.persistentData

        val module = testMethods.firstOrNull()?.let {
            ModuleUtil.findModuleForPsiElement(it)
        } ?: throw IllegalStateException("Cannot find module for test methods")

        configuration.setModule(module)

        val methodPatterns = testMethods.mapNotNull { method ->
            val className = method.containingClass?.qualifiedName ?: return@mapNotNull null
            "$className,${method.name}"
        }.toCollection(LinkedHashSet())

        data.apply {
            TEST_OBJECT = JUnitConfiguration.TEST_PATTERN
            PACKAGE_NAME = ""
            METHOD_NAME = ""
            MAIN_CLASS_NAME = ""
        }
        data.setPatterns(methodPatterns)

        runManager.addConfiguration(settings)
        runManager.selectedConfiguration = settings

        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val environment = ExecutionEnvironmentBuilder.create(executor, settings).build()
        ProgramRunnerUtil.executeConfiguration(environment, false, true)
    }

    private fun findAllRelatedTests(
        method: PsiMethod,
        visitedMethods: Set<PsiMethod> = emptySet()
    ): Set<PsiMethod> {
        if (method in visitedMethods) {
            return emptySet()
        }

        val updatedVisited = visitedMethods + method
        val currentMethodTests = if (isTestAnnotated(method)) {
            setOf(method)
        } else {
            emptySet()
        }

        val references = ReferencesSearch.search(method).findAll()

        val referencedTests = references
            .mapNotNull { reference ->
                findEnclosingMethod(reference)
            }
            .flatMap { enclosingMethod ->
                findAllRelatedTests(enclosingMethod, updatedVisited)
            }
            .toSet()

        return currentMethodTests + referencedTests
    }

    private fun findEnclosingMethod(reference: PsiReference): PsiMethod? {
        val element = reference.element

        val enclosingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
        return enclosingMethod
    }

    private fun isTestAnnotated(method: PsiMethod): Boolean {
        return method.annotations.any { it.qualifiedName == "org.junit.jupiter.api.Test" }
    }
}