package com.icpl;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.Input;

import java.util.Map;
import java.io.File;

public class QTMResultsPublisher extends DefaultTask 
{
    @TaskAction
    public void publishResults() throws QTMConnectionException
	{
		try {
			QTMGradleExtension config = getProject().getExtensions().findByType(QTMGradleExtension.class);
			
			if(config == null)
			{
				throw new QTMConnectionException("Could not find QTM configuration!");
			}
			
			String compfilepath = getProject().getBuildDir().toString() + config.getTestResultFilePath();
			String parsedBuildFramework = "GRADLE";
			String parsedPlatformName = config.getParsedPlatformName();

			String displayName = "QTMGradlePlugin : Starting Post Build Action";
			String repeated = new String(new char[displayName.length()]).replace("\0", "-");
			System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);

			QTMApiConnection conn = new QTMApiConnection(config.getQtmUrl(), config.getQtmProfileApiKey());

			synchronized (conn) {
				// Generating project information
				System.out.println("QTMGradlePlugin : Generating project information...");
				Map<String, String> projectData = conn.getGeneralInfo();
				String testSuiteParentFolderId = projectData.get("testSuiteParentFolderId");
				String projectId = projectData.get("projectId");
				String releaseId = projectData.get("releaseId");
				String buildId = projectData.get("buildId");
				String defaultPlatformId = projectData.get("defaultPlatformId");
				String currentUserId = projectData.get("currentUserId");
				String scope = projectId + ":" + releaseId + ":" + buildId;

				// Platform Creation
				String platformId = null;
				if (!parsedPlatformName.equals("none")) {
					platformId = projectData.get("qtmplatform:" + parsedPlatformName);
					if (platformId == null && "true".equals(config.getPlatformCreateNew())) {
						System.out.println("QTMGradlePlugin : Creating new platform...");
						try {
							platformId = conn.createNewPlatform(parsedPlatformName, scope);
							System.out.println("QTMGradlePlugin : New platform created '" + parsedPlatformName + "'");
						} catch (QTMConnectionException e) {
							platformId = defaultPlatformId;
							System.out.println("QTMGradlePlugin : WARNING : Could not create platform '"
									+ parsedPlatformName + "'. " + e.getMessage() + ". Using No Platform!");
						}
					} else if (platformId == null && !("true".equals(config.getPlatformCreateNew()))) {
						platformId = defaultPlatformId;
						System.out.println("QTMGradlePlugin : WARNING : Could not find platform '" + parsedPlatformName
								+ "'. Using No Platform!");
					} else {
						System.out.println("QTMGradlePlugin : Using Platform '" + parsedPlatformName + "'");
					}
				} else {
					System.out.println("QTMGradlePlugin : Using No platform");
					platformId = defaultPlatformId;
				}

				// fetch automation api key
				System.out.println("QTMGradlePlugin : Fetching automation API Key...");
				String automationApiKey = "";
				try {
					automationApiKey = conn.getAutomationApiKey();
					System.out.println("QTMGradlePlugin : Automation API Key successfully generated!");
				} catch (QTMConnectionException e) {
					System.out.println(
							"QTMGradlePlugin : WARNING : Could not fetch automation API KEY from server. Using provided key!");
					automationApiKey = config.getQtmAutomationApiKey();
				}

				// Upload Result Files
				String parsedTestSuiteName = null;
				String parsedTestSuiteDescription = null;
				if (compfilepath.endsWith("*.xml") || compfilepath.endsWith("*.json")) {
					File filePath = new File(compfilepath);
					System.out.println("QTMGradlePlugin : Reading result files from Directory "
							+ filePath.getParentFile().getAbsolutePath());
					File[] listOfFiles = filePath.getParentFile().listFiles();

					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml")
								|| listOfFiles[i].getName().endsWith(".json"))) {
							parsedTestSuiteName = config.getParsedTestSuiteName(listOfFiles[i].getName());
							parsedTestSuiteDescription = config.getParsedTestSuiteDescription();
							System.out.println(
									"\nQTMGradlePlugin : Result File Found '" + listOfFiles[i].getName() + "'");
							System.out.println(
									"QTMGradlePlugin : Creating new Test Suite '" + parsedTestSuiteName + "'");
							String testSuiteId = conn.createNewTestSuite(parsedTestSuiteName,
									parsedTestSuiteDescription, currentUserId, testSuiteParentFolderId,
									config.getAutomationFramework(), parsedBuildFramework, getProject().getBuildDir().toString(),
									listOfFiles[i].getAbsolutePath(), projectId, releaseId, buildId);
							conn.mapTestSuiteReleaseCycle(testSuiteId, releaseId, buildId, scope);
							System.out.println("QTMGradlePlugin : Uploading result file...");
							conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(), testSuiteId,
									config.getAutomationFramework(), buildId, platformId, scope, automationApiKey);
							System.out.println("QTMGradlePlugin : Result file successfully uploaded!");
						}
					}
				} else {
					parsedTestSuiteName = config.getParsedTestSuiteName(new File(compfilepath).getName());
					parsedTestSuiteDescription = config.getParsedTestSuiteDescription();
					System.out.println("QTMGradlePlugin : Creating new Test Suite '" + parsedTestSuiteName + "'");
					String testSuiteId = conn.createNewTestSuite(parsedTestSuiteName, parsedTestSuiteDescription,
							currentUserId, testSuiteParentFolderId, config.getAutomationFramework(), parsedBuildFramework,
							getProject().getBuildDir().toString(), compfilepath, projectId, releaseId, buildId);
					conn.mapTestSuiteReleaseCycle(testSuiteId, releaseId, buildId, scope);
					System.out.println("QTMGradlePlugin : Uploading result file...");
					conn.uploadFileToTestSuite(compfilepath, testSuiteId, config.getAutomationFramework(), buildId, platformId,
							scope, automationApiKey);
					System.out.println("QTMGradlePlugin : Result file successfully uploaded!");
				}
			} // connection synchronized block
		} catch (QTMConnectionException e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("ERROR : " + e);
		}
		System.out.println("\nQTMGradlePlugin : Finished Post Build Action!");
    }
}