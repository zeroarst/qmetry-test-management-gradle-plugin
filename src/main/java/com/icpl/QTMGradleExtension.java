package com.icpl;

public class QTMGradleExtension
{
	private String qtmUrl;
	private String qtmProfileApiKey;
	private String qtmAutomationApiKey;
	private String automationFramework;
	private String testResultFilePath;
	private String testSuiteName;
	private String testSuiteDescription;
	private String platformName;
	private String platformCreateNew;

	public String getQtmUrl() {
		return this.qtmUrl;
	}

	public String getQtmProfileApiKey() {
		return this.qtmProfileApiKey;
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

	public String getTestSuiteDescription() {
		return this.testSuiteDescription;
	}

	public String getPlatformName() {
		return this.platformName;
	}

	public String getPlatformCreateNew() {
		return this.platformCreateNew;
	}
	
	public String getParsedQtmUrl() {
		if (!getQtmUrl().endsWith("/")) {
			return getQtmUrl() + "/";
		} else
			return getQtmUrl();
	}

	public String getParsedTestSuiteName(String fileName) {
		return getTestSuiteName().replace("%FNAME%", fileName);
	}

	public String getParsedTestSuiteDescription() {
		if (getTestSuiteDescription() == null || getTestSuiteDescription().isEmpty()) {
			return "This Suite has been created by QTMJenkinsPlugin Plugin!";
		}
		return getTestSuiteDescription();
	}

	public String getParsedPlatformName() {
		if (getPlatformName() == null || getPlatformName().isEmpty()) {
			return "none";
		}
		return getPlatformName();
	}
	
	public void setQtmUrl(String qtmApiUrl) {
		this.qtmUrl = qtmApiUrl;
	}

	public void setQtmProfileApiKey(String qtmApiKey) {
		this.qtmProfileApiKey = qtmApiKey;
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
	
	public void setTestSuiteDescription(String testDescription) {
		this.testSuiteDescription = testDescription;
	}
	
	public void setPlatformName(String name)
	{
		this.platformName = platformName;
	}
	
	public void setPlatformCreateNew(String platformCreateNew)
	{
		this.platformCreateNew = platformCreateNew;
	}
}