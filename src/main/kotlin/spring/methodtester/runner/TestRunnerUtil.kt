package spring.methodtester.runner

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
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
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import spring.methodtester.model.JavaTestableMethod
import spring.methodtester.model.KotlinTestableMethod
import spring.methodtester.model.TestableMethod

object TestRunnerUtil {

    fun runRelatedTests(methodElement: PsiElement) {
        val project = methodElement.project
        val relatedTests = findAllRelatedTests(methodElement)

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

    fun runTestsForAllMethodsInClass(classElement: PsiElement) {
        val project = classElement.project
        val allMethods = when (classElement) {
            is PsiClass -> classElement.methods.map { JavaTestableMethod(it) }
            is KtClass -> classElement.declarations.filterIsInstance<KtFunction>().map { KotlinTestableMethod(it) }
            else -> emptyList()
        }.toSet()

        val allRelatedTests = allMethods.flatMap { method ->
            findAllRelatedTests(method)
        }.toSet()

        if (allRelatedTests.isEmpty()) {
            Messages.showMessageDialog(
                project,
                "No related tests found for methods in this class.",
                "Information",
                Messages.getInformationIcon()
            )
            return
        }

        runMultipleTests(project, allRelatedTests)
    }

    private fun runMultipleTests(project: Project, testMethods: Set<TestableMethod>) {
        val runManager = RunManager.getInstance(project)
        val junitConfigurationType = ConfigurationTypeUtil.findConfigurationType(JUnitConfigurationType::class.java)
        val configurationFactory = junitConfigurationType.configurationFactories.firstOrNull()
            ?: throw IllegalStateException("JUnit configuration factory not found")

        val settings = runManager.createConfiguration("Run All Related Tests", configurationFactory)
        val configuration = settings.configuration as JUnitConfiguration
        val data = configuration.persistentData

        val module = testMethods.firstOrNull()?.let {
            ModuleUtil.findModuleForPsiElement(it.psiElement)
        } ?: throw IllegalStateException("Cannot find module for test methods")

        configuration.setModule(module)

        val methodPatterns = testMethods.mapNotNull { method ->
            method.containingClassQualifiedName ?: return@mapNotNull null
            "${method.containingClassQualifiedName},${method.name}"
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

    private fun findAllRelatedTests(element: PsiElement, visitedMethods: Set<TestableMethod> = emptySet()): Set<TestableMethod> {
        val testMethods = mutableSetOf<TestableMethod>()

        when (element) {
            is PsiMethod -> {
                testMethods.add(JavaTestableMethod(element))
            }
            is KtFunction -> {
                testMethods.add(KotlinTestableMethod(element))
            }
        }

        val relatedTests = testMethods.flatMap { method ->
            findAllRelatedTests(method, visitedMethods)
        }.toSet()

        return relatedTests
    }

    private fun findAllRelatedTests(
        method: TestableMethod,
        visitedMethods: Set<TestableMethod> = emptySet()
    ): Set<TestableMethod> {
        if (method in visitedMethods) {
            return emptySet()
        }

        val updatedVisited = visitedMethods + method
        val currentMethodTests = if (method.isTestAnnotated() || method.isNestedTestClass()) {
            setOf(method)
        } else {
            emptySet()
        }

        val references: Collection<PsiReference> = ReferencesSearch.search(method.psiElement).findAll()

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

    private fun findEnclosingMethod(reference: PsiReference): TestableMethod? {
        val element = reference.element

        val enclosingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
            ?: PsiTreeUtil.getParentOfType(element, KtFunction::class.java)
            ?: return null

        return when (enclosingMethod) {
            is PsiMethod -> JavaTestableMethod(enclosingMethod)
            is KtFunction -> KotlinTestableMethod(enclosingMethod)
            else -> null
        }
    }
}