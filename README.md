Automation code for the Luminor Home task assignment. 

To see the file that contains the test steps, follow the path ->
src\test\java\ee\sandrak\imdb\tests java file name is LuminorTask.java

URL
https://github.com/SandrakEST/LuminorHomeTask/blob/main/src/test/java/ee/sandrak/imdb/tests/LuminorTask.java

To run the suite in a terminal, run (once inside the imdb-test directory) :

.\gradlew clean test --tests "ee.sandrak.imdb.tests.LuminorTask" 
allure serve build\allure-results

This will run the test in the browser and then generate an Allure report

Tech:
1 Gradle -v	9.0.0		
2 "Java -vopenjdk version 17.0.16		
3 Scoop 	installed		
4 Visual Studio Code	installed	Extensions added	
4.1 "Extension Pack for Java (Microsoft)
4.2 Gradle for Java (Microsoft)
4.3 TestNg TestSuite Runner"
5. Allure version	2.35.0		
6. Git version	2.51.0.windows.1		
