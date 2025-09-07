Automation code for the Luminor Home task assignment. 

To see the file that contains the test steps, follow the path ->
src\test\java\ee\sandrak\imdb\tests java file name is LuminorTask.java

URL
https://github.com/SandrakEST/LuminorHomeTask/blob/main/src/test/java/ee/sandrak/imdb/tests/LuminorTask.java

To run the suite in a terminal, run (once inside the imdb-test directory) :

.\gradlew clean test --tests "ee.sandrak.imdb.tests.LuminorTask" 
allure serve build\allure-results

This will run the test in the browser and then generate an Allure report
