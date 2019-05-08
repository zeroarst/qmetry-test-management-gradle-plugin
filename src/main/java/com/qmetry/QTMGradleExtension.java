

package com.qmetry;

public class QTMGradleExtension
{
	private String qtmUrl;
	private String qtmAutomationApiKey;
	private String automationFramework;
	private String automationHierarchy;
	private String testResultFilePath;
	private String testSuiteId;
	private String testSuiteName;
	private String platform;
	private String project;
	private String release;
	private String cycle;
	private String build;
	
	public String getQtmUrl() {
		return this.qtmUrl;
	}

	/* public String getQtmAutomationApiKey() {
		return this.qtmAutomationApiKey;
	} */

	/* public String getAutomationFramework() {
		return this.automationFramework;
	} */

	public String getParsedAutomationHierarchy() throws QTMException{
		if(automationFramework.equals("JUNIT"))
		{
			if(!(automationHierarchy.equals("1") || automationHierarchy.equals("2") || automationHierarchy.equals("3")))
			{
				throw new QTMException("Please provide valid Automation Hierarchy for automationFramework " + automationFramework + " in qtmConfig block as 'automationHierarchy'");
			}
		}
		else if(automationFramework.equals("TESTNG"))
		{
			if(!(automationHierarchy.equals("1") || automationHierarchy.equals("2") || automationHierarchy.equals("3")))
			{
				throw new QTMException("Please provide valid Automation Hierarchy for automationFramework " + automationFramework + " in qtmConfig block as 'automationHierarchy'");
			}
		}
		return this.automationHierarchy;
	}

	/* public String getTestResultFilePath() {
		return this.testResultFilePath;
	} */

	/* public String getTestSuiteId() {
		return this.testSuiteId;
	} */
	
	public String getTestSuiteName()
	{
		return this.testSuiteName;
	}

	/* public String getPlatform() {
		return this.platform;
	} */

	/* public String getBuild() {
		return this.build;
	} */
	
	public String getParsedQtmUrl() throws QTMException 
	{
		if(this.qtmUrl == null || this.qtmUrl.isEmpty())
			throw new QTMException("Please provide your QMetry Test Management URL in qtmConfig block as 'qtmUrl'");
		if(!this.qtmUrl.startsWith("http:/") && !this.qtmUrl.startsWith("https:/"))
			throw new QTMException("Please provide a valid QMetry Test Management URL");
		
		if(!getQtmUrl().endsWith("/")) 
			return getQtmUrl() + "/";
		else
			return getQtmUrl();
	}
	
	public String getParsedQtmAutomationApiKey() throws QTMException
	{
		if(this.qtmAutomationApiKey == null || this.qtmAutomationApiKey.isEmpty())
			throw new QTMException("Please provide your QMetry Test Management Automation API Key in qtmConfig block as 'qtmAutomationApiKey'");
		return this.qtmAutomationApiKey;
	}
	
	public String getParsedTestResultFilePath() throws QTMException
	{
		if(this.testResultFilePath == null || this.testResultFilePath.isEmpty())
			throw new QTMException("Please provide your test result file path in qtmConfig block as 'testResultFilePath'");
		return this.testResultFilePath;
	}
	
	public String getParsedPlatform()
	{
		if(this.platform == null || this.platform.isEmpty())
			return "";
		return this.platform;
	}
	
	public String getParsedTestSuiteId()
	{
		if(this.testSuiteId == null || this.testSuiteId.isEmpty())
			return "";
		return this.testSuiteId;
	}
	
	public String getParsedAutomationFramework() throws QTMException
	{
		if(this.automationFramework == null || this.automationFramework.isEmpty())
			throw new QTMException("Please provide your Automation Framework in qtmConfig block as 'automationFramework'");
		if(!(this.automationFramework.equals("JUNIT") 
			|| this.automationFramework.equals("TESTNG")
			|| this.automationFramework.equals("QAS")
			|| this.automationFramework.equals("CUCUMBER")
			|| this.automationFramework.equals("HPUFT")))
			throw new QTMException("Automation Framework '"+automationFramework+"' not supported. Use [JUNIT TESTNG QAS CUCUMBER HPUFT]");
		return this.automationFramework;
	}
	
	public String getParsedProject() throws QTMException
	{
		if(this.project == null || this.project.isEmpty())
			throw new QTMException("Please provide target project ID or Key or Name in qtmConfig block as 'project'");
		return this.project;
	}
	
	public String getParsedRelease() throws QTMException
	{
		if(this.release == null || this.release.isEmpty())
			return "";
		else if(this.project == null || this.project.isEmpty())
			throw new QTMException("Please provide project in qtmConfig when release is provided");
		return this.release;
	}
	public String getParsedCycle() throws QTMException
	{
		if(this.cycle == null || this.cycle.isEmpty())
			return "";
		else if(this.release == null || this.release.isEmpty())
			throw new QTMException("Please provide release in qtmConfig when cycle is provided");
		return this.cycle;
	}
	public String getParsedBuild() throws QTMException
	{
		if(this.build == null  || this.build.isEmpty())
			return "";
		else if(this.cycle == null || this.cycle.isEmpty())
			throw new QTMException("Please provide cycle in qtmConfig when build is provided");
		return this.build;
	}
	
	public void setQtmUrl(String qtmApiUrl) {
		this.qtmUrl = qtmApiUrl.trim();
	}

	public void setQtmAutomationApiKey(String qtmApiKey) {
		this.qtmAutomationApiKey = qtmApiKey.trim();
	}
	
	public void setAutomationFramework(String autoFramework)
	{
		this.automationFramework = autoFramework.trim();
	}

	public void setAutomationHierarchy(String automationHierarchy) {
		this.automationHierarchy = automationHierarchy.trim();
	}

	public void setTestResultFilePath(String testResultFilePath) {
		this.testResultFilePath = testResultFilePath.trim();
	}
	
	public void setTestSuiteId(String testId) {
		this.testSuiteId = testId.trim();
	}
	
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName.trim();
	}
	
	public void setPlatform(String name)
	{
		this.platform = name.trim();
	}
	
	public void setBuild(String name)
	{
		this.build = name.trim();
	}
	public void setProject(String project)
	{
		this.project = project.trim();
	}
	
	public void setRelease(String release)
	{
		this.release = release.trim();
	}
	
	public void setCycle(String cycle)
	{
		this.cycle = cycle.trim();
	}
}