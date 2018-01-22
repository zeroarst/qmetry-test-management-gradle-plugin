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

			String displayName = "QTMGradlePlugin : Starting Post Build Action";
			String repeated = new String(new char[displayName.length()]).replace("\0", "-");
			System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);

			QTMApiConnection conn = new QTMApiConnection(config.getParsedQtmUrl(), config.getQtmAutomationApiKey());

			synchronized (conn) {
				//upload result files
				if (compfilepath.endsWith("*.xml") || compfilepath.endsWith("*.json")) {
					File filePath = new File(compfilepath);
					System.out.println("QTMGradlePlugin : Reading result files from Directory "
							+ filePath.getParentFile().getAbsolutePath());
					File[] listOfFiles = filePath.getParentFile().listFiles();

					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml")
								|| listOfFiles[i].getName().endsWith(".json"))) {
							System.out.println(
									"\nQTMGradlePlugin : Result File Found '" + listOfFiles[i].getName() + "'");
							System.out.println("QTMGradlePlugin : Uploading result file...");
							conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(), config.getTestSuiteName(),
									config.getAutomationFramework(), config.getBuildName(), config.getPlatformName());
							System.out.println("QTMGradlePlugin : Result file successfully uploaded!");
						}
					}
				} else {
					System.out.println("QTMGradlePlugin : Uploading result file...");
					conn.uploadFileToTestSuite(compfilepath, config.getTestSuiteName(), config.getAutomationFramework(),
									config.getBuildName(), config.getPlatformName());
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