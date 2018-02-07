package com.qmetry;

public class QTMGradleExtension
{
	private String qtmUrl;
	private String qtmAutomationApiKey;
	private String automationFramework;
	private String testResultFilePath;
	private String testSuiteName;
	private String platformName;
	private String buildName;

	public String getQtmUrl() {
		return this.qtmUrl;
	}

	public String getQtmAutomationApiKey() {
		return this.qtmAutomationApiKey;
	}

	public String getAutomationFramework() {
		return this.automationFramework;
	}

	public String getTestResultFilePath() {
		return this.testResultFilePath;
	}

	public String getTestSuiteName() {
		return this.testSuiteName;
	}

	public String getPlatformName() {
		return this.platformName;
	}

	public String getBuildName() {
		return this.buildName;
	}
	
	public String getParsedQtmUrl() throws QTMException 
	{
		if(this.qtmUrl == null)
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
		if(this.qtmAutomationApiKey == null)
			throw new QTMException("Please provide your QMetry Test Management Automation API Key in qtmConfig block as 'qtmAutomationApiKey'");
		return this.qtmAutomationApiKey;
	}
	
	public String getParsedTestResultFilePath() throws QTMException
	{
		if(this.testResultFilePath == null)
			throw new QTMException("Please provide your test result file path in qtmConfig block as 'testResultFilePath'");
		return this.testResultFilePath;
	}
	
	public String getParsedBuildName() throws QTMException
	{
		if(this.buildName == null)
			return "";
		return this.buildName;
	}
	
	public String getParsedPlatformName()
	{
		if(this.platformName == null)
			return "";
		return this.platformName;
	}
	
	public String getParsedTestSuiteName()
	{
		if(this.testSuiteName == null)
			return "";
		return this.testSuiteName;
	}
	
	public String getParsedAutomationFramework() throws QTMException
	{
		if(this.automationFramework == null)
			throw new QTMException("Please provide your Automation Framework in qtmConfig block as 'automationFramework'");
		if(!(this.automationFramework.equals("JUNIT") 
			|| this.automationFramework.equals("TESTNG")
			|| this.automationFramework.equals("QAS")
			|| this.automationFramework.equals("CUCUMBER")
			|| this.automationFramework.equals("HPUFT")))
			throw new QTMException("Automation Framework '"+automationFramework+"' not supported. Use [JUNIT TESTNG QAS CUCUMBER HPUFT]");
		return this.automationFramework;
	}
	
	public void setQtmUrl(String qtmApiUrl) {
		this.qtmUrl = qtmApiUrl;
	}

	public void setQtmAutomationApiKey(String qtmApiKey) {
		this.qtmAutomationApiKey = qtmApiKey;
	}
	
	public void setAutomationFramework(String autoFramework)
	{
		this.automationFramework = autoFramework;
	}

	public void setTestResultFilePath(String testResultFilePath) {
		this.testResultFilePath = testResultFilePath;
	}
	
	public void setTestSuiteName(String testName) {
		this.testSuiteName = testName;
	}
	
	public void setPlatformName(String name)
	{
		this.platformName = name;
	}
	
	public void setBuildName(String name)
	{
		this.buildName = name;
	}
}