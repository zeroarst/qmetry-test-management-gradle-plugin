package com.icpl;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.Input;

import java.util.Map;
import java.io.File;

public class QTMResultsPublisher extends DefaultTask 
{
    @TaskAction
    public void publishResults() throws QTMException
	{
		try 
		{
			QTMGradleExtension config = getProject().getExtensions().findByType(QTMGradleExtension.class);
			if(config == null) throw new QTMException("Could not find QTM configuration! please provide qtmConfig block with appropriate parameters in your build.gradle file!");

			String displayName = "QTMGradlePlugin : Starting Post Build Action";
			String repeated = new String(new char[displayName.length()]).replace("\0", "-");
			System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);

			String compfilepath = getProject().getBuildDir().toString() + config.getParsedTestResultFilePath();
			File resultFile = new File(compfilepath);
			if(resultFile==null || !resultFile.exists()) 
				throw new QTMException("Result file(s) '"+compfilepath+"' not Found!");
			
			QTMApiConnection conn = new QTMApiConnection(config.getParsedQtmUrl(), config.getParsedQtmAutomationApiKey());
			synchronized (conn) 
			{
				if(resultFile.isDirectory()) 
				{
					System.out.println("QTMGradlePlugin : Reading result files from Directory '" + compfilepath+ "'");
					File[] listOfFiles = resultFile.listFiles();

					for (int i = 0; i < listOfFiles.length; i++) 
					{
						if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml") || listOfFiles[i].getName().endsWith(".json"))) 
						{
							System.out.println("\nQTMGradlePlugin : Result File Found '" + listOfFiles[i].getName() + "'");
							System.out.println("QTMGradlePlugin : Uploading result file...");
							conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(), 
														config.getParsedTestSuiteName(),
														config.getParsedAutomationFramework(), 
														config.getParsedBuildName(), 
														config.getParsedPlatformName());
							System.out.println("QTMGradlePlugin : Result file successfully uploaded!");
						}
					}
				} 
				else if(resultFile.isFile())
				{
					System.out.println("QTMGradlePlugin : Uploading result file '"+compfilepath+"'");
					conn.uploadFileToTestSuite(compfilepath, 
												config.getParsedTestSuiteName(), 
												config.getParsedAutomationFramework(),
												config.getParsedBuildName(), 
												config.getParsedPlatformName());
					System.out.println("QTMGradlePlugin : Result file successfully uploaded!");
				}
				else
				{
					throw new QTMException("Failed to read result file '"+compfilepath+"'");
				}
			} // connection synchronized block
		} 
		catch (QTMException e) 
		{
			System.out.println("QTMGradlePlugin : ERROR : " + e.getMessage());
		} 
		catch (Exception e) 
		{
			System.out.println("QTMGradlePlugin : ERROR : " + e.toString());
		}
		System.out.println("\nQTMGradlePlugin : Finished Post Build Action!");
    }
}