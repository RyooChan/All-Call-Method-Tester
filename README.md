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
<img width="719" alt="image" src="https://github.com/user-attachments/assets/c86c9c47-b67a-4226-9b56-2390ab7510a5">

5. Choose the downloaded file.
6. Restart IntelliJ.
7. Right-click on the method you want to test and select "Run Related Tests."
<img width="596" alt="image" src="https://github.com/user-attachments/assets/0f1d5a76-14ac-4700-8bdf-d3f4b7a31fc5">

8. Testing is complete.
<img width="594" alt="image" src="https://github.com/user-attachments/assets/354fa71f-1b2b-448c-a13c-21ec12183bf9">

## For:

- Junit4, Junit5 both
- Java

## TODO 

- For Kotlin (There is an issue in Kotlin code where all methods in the same class are targeted for testing)
