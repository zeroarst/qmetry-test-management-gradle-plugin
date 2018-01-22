package com.icpl;

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
	
	public String getParsedQtmUrl() {
		if (!getQtmUrl().endsWith("/")) {
			return getQtmUrl() + "/";
		} else
			return getQtmUrl();
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