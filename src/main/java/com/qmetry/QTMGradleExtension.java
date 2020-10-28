package com.qmetry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class QTMGradleExtension {
   
    private String qtmUrl;
    private String qtmAutomationApiKey;
    private String automationFramework;
    private String automationHierarchy;
    private String testResultFilePath;
    private String testSuiteId;
    private String testSuiteName;
    private String platform;
    private String build;
    private String project;
    private String release;
    private String cycle;
    private String testcaseFields;
    private String testsuiteFields;
    private String skipWarning;
    
    public String getQtmUrl() {
        return qtmUrl;
    }

    public void setQtmUrl(String qtmUrl) {
        this.qtmUrl = qtmUrl;
    }

    public String getQtmAutomationApiKey() {
        return qtmAutomationApiKey;
    }

    public void setQtmAutomationApiKey(String qtmAutomationApiKey) {
        this.qtmAutomationApiKey = qtmAutomationApiKey;
    }

    public String getAutomationFramework() {
        return automationFramework;
    }

    public void setAutomationFramework(String automationFramework) {
        this.automationFramework = automationFramework;
    }

    public String getAutomationHierarchy() {
        return automationHierarchy;
    }

    public void setAutomationHierarchy(String automationHierarchy) {
        this.automationHierarchy = automationHierarchy;
    }

    public String getTestResultFilePath() {
        return testResultFilePath;
    }

    public void setTestResultFilePath(String testResultFilePath) {
        this.testResultFilePath = testResultFilePath;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getTestcaseFields() {
        return testcaseFields;
    }

    public void setTestcaseFields(String testcaseFields) {
        this.testcaseFields = testcaseFields;
    }

    public String getTestsuiteFields() {
        return testsuiteFields;
    }

    public void setTestsuiteFields(String testsuiteFields) {
        this.testsuiteFields = testsuiteFields;
    }
    
    public String getSkipWarning() {
	return skipWarning;
    }
    
    public void setSkipWarning(String skipwarning) {
	this.skipWarning = skipwarning;
    }

    public String getParsedQtmUrl() throws QTMException {
	if (this.qtmUrl == null)
	    throw new QTMException("Please provide your QMetry Test Management URL in qtmConfig block as 'qtmUrl'");
	if (!this.qtmUrl.startsWith("http:/") && !this.qtmUrl.startsWith("https:/"))
	    throw new QTMException("Please provide a valid QMetry Test Management URL");

	if (!getQtmUrl().endsWith("/"))
	    return getQtmUrl() + "/";
	else
	    return getQtmUrl();
    }

    public String getParsedQtmAutomationApiKey() throws QTMException {
	if (this.qtmAutomationApiKey == null)
	    throw new QTMException("Please provide your QMetry Test Management Automation API Key in qtmConfig block as 'qtmAutomationApiKey'");
	return this.qtmAutomationApiKey;
    }

    public String getParsedTestResultFilePath() throws QTMException {
	if (this.testResultFilePath == null)
	    throw new QTMException("Please provide your test result file path in qtmConfig block as 'testResultFilePath'");
	String filePath = this.testResultFilePath.trim();
	if (filePath.length() < 2)
	    throw new QTMException("Please provide your test result file path in qtmConfig block as 'testResultFilePath'");
	return this.testResultFilePath;
    }

    public String getParsedProject() throws QTMException {
	if (this.project == null)
	    throw new QTMException("Please provide your qmetry project id, key or name in qtmConfig block as 'project'");
	String temp_project = this.project.trim();
	if (temp_project.length() < 2)
	    throw new QTMException("Please provide your qmetry project id, key or name in qtmConfig block as 'project'");
	return temp_project;
    }

    public String getParsedRelease() throws QTMException {
	if (this.release == null)
	    return "";
	return this.release.trim();
    }

    public String getParsedCycle() throws QTMException {
	if (this.cycle == null) {
	    return "";
	} else {
	    if ((this.release == null || this.release.trim().length() == 0)) {
		throw new QTMException("Please provide release value for cycle!");
	    } else {
		return this.cycle.trim();
	    }
	}
    }

    public String getParsedBuild() {
	if (this.build == null)
	    return "";
	return this.build.trim();
    }

    public String getParsedPlatform() {
	if (this.platform == null)
	    return "";
	return this.platform.trim();
    }

    public String getParsedTestSuiteId() {
	if (this.testSuiteId == null)
	    return "";
	return this.testSuiteId.trim();
    }

    public String getParsedTestSuiteName() {
	if (this.testSuiteName == null)
	    return "";
	return this.testSuiteName.trim();
    }

    public String getParsedAutomationFramework() throws QTMException {
	if (this.automationFramework == null)
	    throw new QTMException("Please provide your Automation Framework in qtmConfig block as 'automationFramework'");
	if (!(this.automationFramework.equals("JUNIT") || this.automationFramework.equals("TESTNG") || this.automationFramework.equals("QAS") || this.automationFramework.equals("CUCUMBER") || this.automationFramework.equals("HPUFT") || this.automationFramework.equals("ROBOT")))
	    throw new QTMException("Automation Framework '" + automationFramework + "' not supported. Use [JUNIT TESTNG QAS CUCUMBER HPUFT ROBOT]");
	return this.automationFramework;
    }
    
    public String getParsedAutomationHierarchy() {
	if (this.automationHierarchy == null)
	    return "";
	return this.automationHierarchy.trim();
    }

    public String getParsedTestcaseFields() throws QTMException {
	if (this.testcaseFields == null)
	    return "";
	try {
	    JSONParser parser = new JSONParser();
	    JSONObject j = (JSONObject) parser.parse(this.testcaseFields);
	    return j.toString();
	} catch (Exception ex) {
	    throw new QTMException("Please provide correct Json data of Test case fields !");
	}
    }

    public String getParsedTestsuiteFields() throws QTMException {
	if (this.testsuiteFields == null)
	    return "";
	try {
	    JSONParser parser = new JSONParser();
	    JSONObject j = (JSONObject) parser.parse(this.testsuiteFields);
	    return j.toString();
	} catch (Exception ex) {
	    throw new QTMException("Please provide correct Json data of Test suite fields !");
	}
    }
    
    public String getParsedSkipWarning() {
	if (this.skipWarning == null)
	    return "";
	return this.skipWarning.trim();
    }
}