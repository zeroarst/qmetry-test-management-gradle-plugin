package com.qmetry;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskExecutionException;

import java.util.Map;
import java.io.File;

public class QTMResultsPublisher extends DefaultTask 
{
	public QTMResultsPublisher()
	{
		setDescription("Ship high quality products, faster by linking automated test results from Gradle to QMetry Test Management");
		setGroup("QMetry Test Management");
	}
	
    @TaskAction
    public void publishResults() throws QTMException
	{
		String pluginName = "QMetry Test Management Gradle Plugin";
		try 
		{
			QTMGradleExtension config = getProject().getExtensions().findByType(QTMGradleExtension.class);
			if(config == null) throw new QTMException("Could not find QTM configuration! please provide qtmConfig block with appropriate parameters in your build.gradle file!");

			String displayName = pluginName + " : Starting Post Build Action";
			String repeated = new String(new char[displayName.length()]).replace("\0", "-");
			System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);

			String compfilepath = getProject().getBuildDir().toString() + "/" + config.getParsedTestResultFilePath();
			File resultFile = new File(compfilepath);
			if(resultFile==null || !resultFile.exists()) 
				throw new QTMException("Result file(s) '"+compfilepath+"' not Found!");
			
			QTMApiConnection conn = new QTMApiConnection(config.getParsedQtmUrl(), config.getParsedQtmAutomationApiKey());
			synchronized (conn) 
			{
				if(resultFile.isDirectory()) 
				{
					System.out.println(pluginName + " : Reading result files from Directory '" + compfilepath+ "'");
					System.out.println(pluginName + " : Creating zip file...");
					String zipFilePath = CreateZip.createZip(compfilepath,config.getParsedAutomationFramework());
					System.out.println(pluginName + " : Reading result file '" + zipFilePath + "'");
					System.out.println(pluginName + " : Uploading result file...");
					conn.uploadFileToTestSuite(zipFilePath,
												config.getParsedTestSuiteId(),
												config.getTestSuiteName(),
												config.getParsedAutomationFramework(),
												config.getParsedProject(),
												config.getParsedRelease(),
												config.getParsedCycle(),
												config.getParsedBuild(),
												config.getParsedPlatform());
					System.out.println(pluginName + " : Result file successfully uploaded!");
					/*File[] listOfFiles = resultFile.listFiles();

					for (int i = 0; i < listOfFiles.length; i++) 
					{
						if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml") || listOfFiles[i].getName().endsWith(".json"))) 
						{
							System.out.println("\n" +pluginName+ " : Result File Found '" + listOfFiles[i].getName() + "'");
							System.out.println(pluginName + " : Uploading result file...");
							conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(), 
														config.getParsedTestSuiteName(),
														config.getParsedAutomationFramework(), 
														config.getParsedProjectName(),
														config.getParsedReleaseName(),
														config.getParsedCycleName(),
														config.getParsedBuildName(), 
														config.getParsedPlatformName());
							System.out.println(pluginName + " : Result file successfully uploaded!");
						}
					}*/
				} 
				else if(resultFile.isFile())
				{
					String format = config.getParsedAutomationFramework();
					if(format.equals("QAS"))
					{
						throw new QTMException(pluginName + " : For QAS enter path to directory not file.");
					}
					else if(compfilepath.endsWith(".xml") && !(format.equals("JUNIT") || format.equals("TESTNG") || format.equals("HPUFT")))
					{
						throw new QTMException(pluginName + " : Cannot upload xml file when AutomationFramework is " + format);
					}
					else if(!format.equals("CUCUMBER") && compfilepath.endsWith(".json"))
					{
						throw new QTMException(pluginName + " : Cannot upload json file when AutomationFramework is " + format);
					}
					System.out.println(pluginName + " : Reading result file '" + compfilepath+ "'");
					System.out.println(pluginName + " : Uploading result file...");
					conn.uploadFileToTestSuite(compfilepath, 
												config.getParsedTestSuiteId(), 
												config.getTestSuiteName(),
												config.getParsedAutomationFramework(),
												config.getParsedProject(),
												config.getParsedRelease(),
												config.getParsedCycle(),
												config.getParsedBuild(), 
												config.getParsedPlatform());
					System.out.println(pluginName + " : Result file successfully uploaded!");
				}
				else
				{
					throw new QTMException("Failed to read result file '"+compfilepath+"'");
				}
			}
		} 
		catch (QTMException e) 
		{
			System.out.println(pluginName + " : ERROR : " + e.getMessage());
			System.out.println(pluginName + " : Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information");
			throw new TaskExecutionException(this,e);
		} 
		catch (Exception e) 
		{
			System.out.println(pluginName + " : ERROR : " + e.toString());
			System.out.println(pluginName + " : Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information");
			throw new TaskExecutionException(this,e);
		}
		System.out.println(pluginName + " : Finished Post Build Action!");
    }
}