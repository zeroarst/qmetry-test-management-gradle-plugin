package com.qmetry;

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;

import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class QTMApiConnection {

    private String url;
    private String key;

    public QTMApiConnection(String url, String key) {
	this.url = url;
	this.key = key;
    }

    public String getUrl() {
	return this.url;
    }

    public String getKey() {
	return this.key;
    }

    public boolean validateConnection() {
	// TODO validate connection using API
	return true;
    }

    public boolean uploadFileToTestSuite(String filePath, String testSuiteId, String testSuiteName, String automationFramework, String automationHierarchy, String buildName, String platformName, String project, String release, String cycle,
	    String testCaseFields, String testSuiteFields, String skipWarning) throws InvalidCredentialsException, ProtocolException, IOException, QTMException {
	CloseableHttpClient httpClient = null;
	CloseableHttpResponse response = null;
	try {
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    builder.addTextBody("entityType", automationFramework, ContentType.TEXT_PLAIN);
	    if (testSuiteId != null && !testSuiteId.isEmpty())
		builder.addTextBody("testsuiteId", testSuiteId, ContentType.TEXT_PLAIN);
	    if (testSuiteName != null && !testSuiteName.isEmpty())
		builder.addTextBody("testsuiteName", testSuiteName, ContentType.TEXT_PLAIN);
	    if (automationHierarchy != null && !automationHierarchy.isEmpty())
		builder.addTextBody("automationHierarchy", automationHierarchy, ContentType.TEXT_PLAIN);
	    if (buildName != null && !buildName.isEmpty())
		builder.addTextBody("build", buildName, ContentType.TEXT_PLAIN);
	    if (platformName != null && !platformName.isEmpty())
		builder.addTextBody("platformID", platformName, ContentType.TEXT_PLAIN);
	    if (project != null && !project.isEmpty())
		builder.addTextBody("projectID", project, ContentType.TEXT_PLAIN);
	    if (release != null && !release.isEmpty())
		builder.addTextBody("releaseID", release, ContentType.TEXT_PLAIN);
	    if (cycle != null && !cycle.isEmpty())
		builder.addTextBody("cycleID", cycle, ContentType.TEXT_PLAIN);
	    if (testCaseFields != null && !testCaseFields.isEmpty())
		builder.addTextBody("testcase_fields", testCaseFields, ContentType.TEXT_PLAIN);
	    if (testSuiteFields != null && !testSuiteFields.isEmpty())
		builder.addTextBody("testsuite_fields", testSuiteFields, ContentType.TEXT_PLAIN);
	    if (skipWarning != null && !skipWarning.isEmpty())
		builder.addTextBody("skipWarning", skipWarning, ContentType.TEXT_PLAIN);
	    

	    File f = new File(filePath);
	    builder.addPart("file", new FileBody(f));
	    HttpEntity multipart = builder.build();

	    HttpPost uploadFile = new HttpPost(getUrl() + "/rest/import/createandscheduletestresults/1");
	    uploadFile.addHeader("accept", "application/json");
	    uploadFile.addHeader("scope", "default");
	    uploadFile.addHeader("apiKey", getKey());
	    uploadFile.setEntity(multipart);

	    httpClient = HttpClients.createDefault();
	    response = httpClient.execute(uploadFile);
	    String respEntityStr = EntityUtils.toString(response.getEntity());
	    System.out.println("QMetry Test Management Gradle Plugin : Response : " + respEntityStr);
	    if (!(response.getStatusLine().getStatusCode() == 200)) {
		throw new QTMException("Error uploading file!");
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("QMetry Test Management Gradle Plugin : ERROR : " + e.toString());
	    throw new QTMException("Could not upload file '" + filePath + "' to test suite!");
	} finally {
	    try {
		httpClient.close();
		response.close();
	    } catch (Exception e) {
	    }
	}
    }
}