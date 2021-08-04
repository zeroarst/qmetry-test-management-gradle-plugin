package com.qmetry;

import java.io.File;
import java.io.FilenameFilter;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class PublishResults extends DefaultTask {
    public PublishResults() {
	setDescription("Faster way to linking automated test result to QMetry Test Management, to ship high quality products.");
	setGroup("QMetry Test Management");
    }

    @TaskAction
    public void publishResults() throws QTMException {

	String pluginName = "QMetry Test Management Gradle Plugin";
	try {
	    QTMGradleExtension config = getProject().getExtensions().findByType(QTMGradleExtension.class);
	    if (config == null)
		throw new QTMException("Could not find QTM configuration! please provide qmetryConfig {... } block with appropriate parameters in your build.gradle file!");
	    String displayName = pluginName + " : Starting Post Build Action";
	    String repeated = new String(new char[displayName.length()]).replace("\0", "-");
	    System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);
	    
	    if (!config.getParsedAutomationFramework().isEmpty()) {
		System.out.println(pluginName + " : Using Automation Framework '" + config.getParsedAutomationFramework() + "'");
	    }
	    if (!config.getParsedTestSuiteId().isEmpty()) {
		System.out.println(pluginName + " : Using Test Suite Id '" + config.getParsedTestSuiteId() + "'");
	    }
	    if (!config.getParsedTestSuiteName().isEmpty()) {
		System.out.println(pluginName + " : Using Test Suite Name '" + config.getParsedTestSuiteName() + "'");
	    }
	    if (!config.getParsedAutomationHierarchy().isEmpty()) {
		System.out.println(pluginName + " : Using Automation Hierarchy '" + config.getParsedAutomationHierarchy() + "'");
	    }
	    if (!config.getParsedPlatform().isEmpty()) {
		System.out.println(pluginName + " : Using Platform '" + config.getParsedPlatform() + "'");
	    }
	    if (!config.getParsedBuild().isEmpty()) {
		System.out.println(pluginName + " : Using Build (or Drop) '" + config.getParsedBuild() + "'");
	    }
	    if (!config.getParsedProject().isEmpty()) {
		System.out.println(pluginName + " : Project '" + config.getParsedProject() + "'");
	    }
	    if (!config.getParsedRelease().isEmpty()) {
		System.out.println(pluginName + " : Using Release '" + config.getParsedRelease() + "'");
	    }
	    if (!config.getParsedCycle().isEmpty()) {
		System.out.println(pluginName + " : Using Cycle '" + config.getParsedCycle() + "'");
	    }
	    if (!config.getParsedTestcaseFields().isEmpty()) {
		System.out.println(pluginName + " : Using Test Case Fields '" + config.getParsedTestcaseFields() + "'");
	    }
	    if (!config.getParsedTestsuiteFields().isEmpty()) {
		System.out.println(pluginName + " : Using Test Suite Fields '" + config.getParsedTestsuiteFields() + "'");
	    }

	    String compfilepath = getProject().getBuildDir().toString() + File.separator.toString() + config.getParsedTestResultFilePath();
	    File resultFile = new File(compfilepath);
	    if (resultFile == null || !resultFile.exists())
		throw new QTMException("Result file(s) '" + compfilepath + "' not Found!");

	    QTMApiConnection conn = new QTMApiConnection(config.getParsedQtmUrl(), config.getParsedQtmAutomationApiKey());
	    synchronized (conn) {
		String automationFramework = config.getParsedAutomationFramework();
		if (automationFramework.equals("QAS")) {

		    if (resultFile.isFile()) {
			String fileExtensionJson=getExtensionOfFile(resultFile);
			String extension = "zip";
			if(extension.equalsIgnoreCase(fileExtensionJson)) {
			    System.out.println(pluginName + " : Reading result file '" + compfilepath + "'");
			    System.out.println(pluginName + " : Uploading result file...");
			    conn.uploadFileToTestSuite(compfilepath,
				    config.getParsedTestSuiteId(),
				    config.getParsedTestSuiteName(),
				    config.getParsedAutomationFramework(),
				    config.getParsedAutomationHierarchy(),
				    config.getParsedBuild(),
				    config.getParsedPlatform(),
				    config.getParsedProject(),
				    config.getParsedRelease(),
				    config.getParsedCycle(),
				    config.getParsedTestcaseFields(),
				    config.getParsedTestsuiteFields());
			    System.out.println(pluginName + " : Result file successfully uploaded!");
			} else {
			    throw new QTMException("Upload .Zip file or configure Directory to upload QAS results.");
			}
		    } else if (resultFile.isDirectory()) {
			System.out.println(pluginName + " : Reading result files from Directory '" + compfilepath + "'");
			File dirs[] = resultFile.listFiles(new FilenameFilter() {
			    public boolean accept(File directory, String fileName) {
				return (directory.isDirectory());
			    }
			});

			if (dirs == null) 
			    throw new QTMException("Could not find result file(s) at given path!");

			Long last_mod = Long.MIN_VALUE;
			File latest_dir = null;			    
			for (File adir : dirs) {
			    if (adir.isDirectory() && adir.lastModified() > last_mod) {
				latest_dir = adir;
				last_mod = adir.lastModified();
			    }
			}

			ZipUtils zipUtils = new ZipUtils("json");
			if (latest_dir == null)
			    throw new QTMException("Results' directory of type QAS not found in given directory '" + resultFile.getAbsolutePath() + "'");
			zipUtils.zipDirectory(latest_dir, "qmetry_result.zip");

			File zipArchive = new File(latest_dir, "qmetry_result.zip");
			if (zipArchive == null || !zipArchive.exists())
			    throw new QTMException("Failed to create zip archive for QAS results at directory '" + latest_dir.getAbsolutePath() + "'");

			System.out.println(pluginName + " : Uploading zip file...");
			conn.uploadFileToTestSuite(zipArchive.getAbsolutePath(),
				config.getParsedTestSuiteId(),
				config.getParsedTestSuiteName(),
				config.getParsedAutomationFramework(),
				config.getParsedAutomationHierarchy(),
				config.getParsedBuild(),
				config.getParsedPlatform(),
				config.getParsedProject(),
				config.getParsedRelease(),
				config.getParsedCycle(),
				config.getParsedTestcaseFields(),
				config.getParsedTestsuiteFields());
			System.out.println(pluginName + " : Result file successfully uploaded!");
		    }		  	
		} else if (resultFile.isDirectory()) {
		    System.out.println(pluginName + " : Reading result files from Directory '" + compfilepath + "'");
		    File[] listOfFiles = resultFile.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml") || listOfFiles[i].getName().endsWith(".json"))) {
			    System.out.println("\n" + pluginName + " : Result File Found '" + listOfFiles[i].getName() + "'");
			    System.out.println(pluginName + " : Uploading result file...");
			    conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(),
				    config.getParsedTestSuiteId(),
				    config.getParsedTestSuiteName(),
				    config.getParsedAutomationFramework(),
				    config.getParsedAutomationHierarchy(),
				    config.getParsedBuild(),
				    config.getParsedPlatform(),
				    config.getParsedProject(), 
				    config.getParsedRelease(),
				    config.getParsedCycle(),
				    config.getParsedTestcaseFields(),
				    config.getParsedTestsuiteFields());
			    System.out.println(pluginName + " : Result file successfully uploaded!");
			}
		    }
		} else if (resultFile.isFile()) {
		    System.out.println(pluginName + " : Reading result file '" + compfilepath + "'");
		    System.out.println(pluginName + " : Uploading result file...");
		    conn.uploadFileToTestSuite(compfilepath,
			    config.getParsedTestSuiteId(),
			    config.getParsedTestSuiteName(),
			    config.getParsedAutomationFramework(),
			    config.getParsedAutomationHierarchy(),
			    config.getParsedBuild(),
			    config.getParsedPlatform(),
			    config.getParsedProject(),
			    config.getParsedRelease(),
			    config.getParsedCycle(),
			    config.getParsedTestcaseFields(),
			    config.getParsedTestsuiteFields());
		    System.out.println(pluginName + " : Result file successfully uploaded!");
		} else {
		    throw new QTMException("Failed to read result file '" + compfilepath + "'");
		}
	    }
	} catch (QTMException e) {
	    System.out.println(pluginName + " : ERROR : " + e.getMessage());
	} catch (Exception e) {
	    System.out.println(pluginName + " : ERROR : " + e.toString());
	}
	System.out.println("\n" + pluginName + " : Finished Post Build Action!");
    }
    
    private static String getExtensionOfFile(File file) {

	String fileExtension="";
	// Get file Name first
	String fileName=file.getName();

	// If fileName do not contain "." or starts with "." then it is not a valid file
	if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0) 
	    fileExtension=fileName.substring(fileName.lastIndexOf(".")+1);

	return fileExtension;
    }
}