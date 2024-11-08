## Features:

Method Usage Identification: The plugin scans the entire codebase to locate all occurrences of the target method, mapping out its usage across different modules and classes.

Dependent Test Execution: For each identified usage, the plugin triggers the execution of existing test cases. This means it not only runs the tests for the target method but also for each calling method, ensuring thorough verification of code changes.

Impact Analysis: By executing tests for all interconnected methods, the plugin helps developers understand the ripple effect of modifications in a single method, promoting safer refactorings and updates.

Integration with Testing Frameworks: Built to integrate seamlessly with popular testing frameworks, allowing it to fit naturally into existing development workflows without requiring significant process changes.

Improves Code Reliability: Ensures that changes do not inadvertently affect other parts of the application, thus increasing the reliability and stability of the code.

## How To Use:

1. Download the [zip file](https://github.com/RyooChan/All-Call-Method-Tester/releases/tag/v1.0.0).
2. In IntelliJ, go to Preferences and click on Plugins, then click the gear icon.
3. Select "Install Plugin from Disk."
4. Choose the downloaded file.
5. Restart IntelliJ.
6. Right-click on the method you want to test and select "Run Related Tests."
7. Testing is complete.
