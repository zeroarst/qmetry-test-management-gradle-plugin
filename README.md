QTMGradlePlugin uploads result file(s) generated in a Gradle project to QTM Enterprise server.
The plugin, if used in a gradle project, provides an additional gradle task 'publishResultsToQTM'

To use the plugin,
'gradle clean install' - to install the plugin in your local maven repository. 
Use the plugin from anywhere in your gradle project, by including the following code in 'build.gradle' file...

apply plugin: 'com.icpl.qtmgradleplugin'

qtmConfig 
{
	qtmUrl = 'https://qtmserverurl.com/'
	qtmAutomationApiKey = ''
	automationFramework = 'JUNIT'
	testSuiteName = 'QTM4Gradle Test Run - JUNIT'
	testResultFilePath = '/test-results/test/*.xml'
	platformName = 'MyPlatform2'
	buildName = 'myCycleName'
}

buildscript
{
    repositories 
	{
        mavenLocal()
		mavenCentral()
    }
    dependencies 
	{
        classpath 'com.icpl:QTMGradlePlugin:1.1'
    }
}

use command 'gradle test publishReslutsToQTM' from your project.

...or alternatively, generate .jar plugin file by command 'gradle clean build' and use the file directly as a dependency in your Gradle project 'build.gradle' file.

The task publishResultsToQTM always looks for qtmConfig in build.gradle file of your project. Provide following details :-

qtmUrl - url to qtm instance
qtmAutomationApiKey - Automation Key
automationFramework - JUNIT/TESTNG/CUCUMBER/QAS
testSuiteName - name of test suite.
testResultFilePath - path to result file relative to bild directory
platformName(optional) - Name of the platform to connect the suite
buildName - Name of cycle linked to test suite