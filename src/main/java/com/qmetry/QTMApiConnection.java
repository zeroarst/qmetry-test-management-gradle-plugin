package com.qmetry;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class QTMApiConnection {

    private String url;
    private String key;

    private JSONObject projectInformationJson;

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

    public boolean uploadFileToTestSuite(String filePath, String testSuiteId, String testSuiteName, String automationFramework, String project, String release, String cycle, String build, String platform)
    throws InvalidCredentialsException, ProtocolException, IOException, QTMException 
	{
		String pluginName = "QMetry Test Management Gradle Plugin";
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("entityType", automationFramework, ContentType.TEXT_PLAIN);
			if(testSuiteId!=null && !testSuiteId.isEmpty())
			{
				System.out.println(pluginName + " : TestSuiteId : " + testSuiteId);
				builder.addTextBody("testsuiteId", testSuiteId, ContentType.TEXT_PLAIN);
            }
			if(testSuiteName!=null && !testSuiteName.isEmpty())
			{
				System.out.println(pluginName + " : TestSuiteName : " + testSuiteName);
				builder.addTextBody("testsuiteName", testSuiteName, ContentType.TEXT_PLAIN);
			}
			if(project!=null && !project.isEmpty())
			{	
				System.out.println(pluginName + " : Project : " + project);
				builder.addTextBody("projectID", project, ContentType.TEXT_PLAIN);
			}
			if(release!=null && !release.isEmpty())
			{
				System.out.println(pluginName + " : Release : " + release);
				builder.addTextBody("releaseID", release, ContentType.TEXT_PLAIN);
			}
			if(cycle!=null && !cycle.isEmpty())
			{	
				System.out.println(pluginName + " : Cycle : " + cycle);
				builder.addTextBody("cycleID", cycle, ContentType.TEXT_PLAIN);
			}
			if(build!=null && !build.isEmpty())
			{
				System.out.println(pluginName + " : Build : " + build);
				builder.addTextBody("buildID", build, ContentType.TEXT_PLAIN);
            }
			if(platform!=null && !platform.isEmpty())
			{
				System.out.println(pluginName + " : Platform : " + platform);
				builder.addTextBody("platformID", platform, ContentType.TEXT_PLAIN);
			}
			
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
			System.out.println(pluginName + " : Response : " + respEntityStr);
            if (!(response.getStatusLine().getStatusCode() == 200)) {
				System.out.println(pluginName + " : Response Code : " + response.getStatusLine().getStatusCode());
                throw new QTMException("Error uploading file!");
            }
            return true;
        } catch (Exception e) {
            System.out.println(pluginName + " : ERROR : " + e.toString());
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