package com.icpl;

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

	public String getAutomationApiKey() throws QTMConnectionException {
		String key = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(getUrl() + "/rest/admin/project/getinfo");
			httpGet.addHeader("apikey", getKey());
			httpGet.addHeader("scope", "default");
			httpGet.addHeader("Accept", "application/json");
			response = httpClient.execute(httpGet);
			String respEntityStr = EntityUtils.toString(response.getEntity());
			if (!respEntityStr.contains("success"))
				throw new QTMConnectionException();
			JSONParser parser = new JSONParser();
			JSONObject automationJson = (JSONObject) parser.parse(respEntityStr);
			key = automationJson.get("automationAPIKey").toString();
		} catch (Exception e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e);
			throw new QTMConnectionException("Could not generate Automation Key!");
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (Exception e) {
			}
		}
		return key;
	}

	public Map<String, String> getGeneralInfo() throws QTMConnectionException {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		Map<String, String> projectData = null;
		try {
			HttpGet httpGet = new HttpGet(getUrl() + "/rest/admin/project/getinfo");
			httpGet.addHeader("apikey", getKey());
			httpGet.addHeader("scope", "default");
			httpGet.addHeader("Accept", "application/json");

			httpClient = HttpClients.createDefault();
			response = httpClient.execute(httpGet);
			String respEntityStr = EntityUtils.toString(response.getEntity());
			
			JSONParser parser = new JSONParser();
			JSONObject projectsInfoJson = (JSONObject) parser.parse(respEntityStr);
			JSONObject rootFolders = (JSONObject) projectsInfoJson.get("rootFolders");
			JSONObject rootFoldersTS = (JSONObject) rootFolders.get("TS");

			projectData = new HashMap<String, String>();
			projectData.put("testSuiteParentFolderId", rootFoldersTS.get("id").toString());
			projectData.put("projectId", projectsInfoJson.get("currentProjectId").toString());
			projectData.put("releaseId", projectsInfoJson.get("currentReleaseId").toString());
			projectData.put("buildId", projectsInfoJson.get("currentBuildId").toString());

			JSONObject views = (JSONObject) projectsInfoJson.get("views");
			JSONObject views_ts = (JSONObject) views.get("TS");
			JSONObject views_ts_platform = (JSONObject) views_ts.get("TsPlatform");
			projectData.put("defaultPlatformId", views_ts_platform.get("id").toString());

			JSONObject projectPlatforms = (JSONObject) projectsInfoJson.get("projectPlatforms");
			Iterator iterator = projectPlatforms.keySet().iterator();
			for (; iterator.hasNext();) {
				String pid = (String) iterator.next();
				projectData.put("qtmplatform:" + projectPlatforms.get(pid), pid);
			}

			JSONObject currentUser = (JSONObject) projectsInfoJson.get("currentUser");
			projectData.put("currentUserId", currentUser.get("id").toString());
		} catch (Exception e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e);
			throw new QTMConnectionException("Could not fetch project information!");
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (Exception e) {
			}
		}
		return projectData;
	}

	public String createNewPlatform(String platformName, String scope) throws QTMConnectionException {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		String platformId = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(getUrl() + "/rest/admin/platform");
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			post.setHeader("scope", scope);
			post.setHeader("apiKey", getKey());

			JSONObject reqJson = new JSONObject();
			reqJson.put("name", platformName);
			reqJson.put("platformID", 0);
			reqJson.put("platformType", "single");

			post.setEntity(new StringEntity(reqJson.toJSONString()));
			response = httpClient.execute(post);
			String respEntity = EntityUtils.toString(response.getEntity());
			if (!respEntity.contains("success"))
				throw new QTMConnectionException();
			JSONParser parser = new JSONParser();
			JSONObject respJson = (JSONObject) parser.parse(respEntity);
			platformId = respJson.get("platformID").toString();
		} catch (Exception e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e);
			throw new QTMConnectionException("Could not create new platform '" + platformName + "'!");
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (Exception e) {
			}
		}
		return platformId;
	}

	public String createNewTestSuite(String testSuiteName, String testSuiteDescription, String ownerId,
			String parentFolderId, String automationFramework, String buildFramework, String projectDirectory,
			String filePath, String projectId, String releaseId, String buildId) throws QTMConnectionException {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		String scope = projectId + ":" + releaseId + ":" + buildId;
		String testSuiteId = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(getUrl() + "/rest/testsuites");
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			post.setHeader("scope", scope);
			post.setHeader("apiKey", getKey());

			JSONObject reqJson = new JSONObject();
			reqJson.put("name", testSuiteName);
			reqJson.put("description", testSuiteDescription);
			reqJson.put("owner", ownerId);
			reqJson.put("parentFolderId", parentFolderId);
			reqJson.put("isAutomatedFlag", "true");
			reqJson.put("frameWork", automationFramework);
			if (!buildFramework.equals("none"))
				reqJson.put("buildFramework", buildFramework);
			reqJson.put("projectHomeDirectory", projectDirectory);
			reqJson.put("projectPath", filePath);

			post.setEntity(new StringEntity(reqJson.toJSONString()));
			response = httpClient.execute(post);

			String respEntity = EntityUtils.toString(response.getEntity());
			if (!respEntity.contains("success"))
				throw new QTMConnectionException();
			JSONParser parser = new JSONParser();
			JSONObject respJson = (JSONObject) parser.parse(respEntity);
			JSONArray data = (JSONArray) respJson.get("data");
			respJson = (JSONObject) data.get(0);
			testSuiteId = respJson.get("id").toString();
		} catch (Exception e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e);
			throw new QTMConnectionException("Could not create new test suite '" + testSuiteName + "'!");
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (Exception e) {
			}
		}
		return testSuiteId;
	}

	public boolean mapTestSuiteReleaseCycle(String testSuiteId, String releaseId, String buildId, String scope)
			throws QTMConnectionException {
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		HttpPost post = null;
		try {
			httpClient = HttpClients.createDefault();
			post = new HttpPost(getUrl() + "/rest/testsuites/mapReleaseCycle");
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			post.setHeader("scope", scope);
			post.setHeader("apiKey", getKey());

			JSONObject reqJson = new JSONObject();
			reqJson.put("tsId", testSuiteId);
			reqJson.put("buildID", buildId);
			reqJson.put("releaseId", releaseId);

			JSONArray dataarray = new JSONArray();
			dataarray.add(reqJson);

			JSONObject finalJson = new JSONObject();
			finalJson.put("data", dataarray);

			post.setEntity(new StringEntity(finalJson.toJSONString()));
			response = httpClient.execute(post);
			String respEntity = EntityUtils.toString(response.getEntity());
			if (!respEntity.contains("success"))
				throw new QTMConnectionException();

			return true;
		} catch (Exception e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e);
			throw new QTMConnectionException("Could not map test suite '" + testSuiteId + "' to release & cycle!");
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (Exception e) {
			}
		}
	}

	public boolean uploadFileToTestSuite(String filePath, String testSuiteId, String automationFramework,
			String buildId, String platformId, String scope, String automationApiKey)
			throws InvalidCredentialsException, ProtocolException, IOException, QTMConnectionException {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("entityType", automationFramework, ContentType.TEXT_PLAIN);
			builder.addTextBody("testsuiteId", testSuiteId, ContentType.TEXT_PLAIN);
			builder.addTextBody("buildID", buildId, ContentType.TEXT_PLAIN);
			builder.addTextBody("platformID", platformId, ContentType.TEXT_PLAIN);

			File f = new File(filePath);
			builder.addPart("file", new FileBody(f));
			HttpEntity multipart = builder.build();

			HttpPost uploadFile = new HttpPost(getUrl() + "/rest/import/createandscheduletestresults/1");
			uploadFile.addHeader("accept", "application/json");
			uploadFile.addHeader("scope", scope);
			uploadFile.addHeader("apiKey", automationApiKey);
			uploadFile.setEntity(multipart);

			httpClient = HttpClients.createDefault();
			response = httpClient.execute(uploadFile);

			String respEntityStr = EntityUtils.toString(response.getEntity());
			if (!respEntityStr.contains("success"))
				throw new QTMConnectionException();
			return true;
		} catch (Exception e) {
			System.out.println("QTMGradlePlugin : ERROR : " + e);
			throw new QTMConnectionException(
					"Could not upload file '" + filePath + "' to test suite '" + testSuiteId + "'!");
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (Exception e) {
			}
		}
	}
}