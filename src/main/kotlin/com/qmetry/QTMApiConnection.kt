package com.qmetry

import org.apache.commons.httpclient.auth.InvalidCredentialsException
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.IOException
import java.net.ProtocolException

class QTMApiConnection(private val url: String, private val key: String) {

    @Throws(InvalidCredentialsException::class, ProtocolException::class, IOException::class, QTMException::class)
    fun uploadFileToTestSuite(
        file: File,
        testSuiteId: String?,
        testSuiteName: String?,
        automationFramework: AutomationFramework,
        automationHierarchy: String?,
        buildName: String?,
        platformName: String?,
        project: String?,
        release: String?,
        cycle: String?,
        testCaseFields: String?,
        testSuiteFields: String?
    ): Boolean {
        var httpClient: CloseableHttpClient? = null
        var response: CloseableHttpResponse? = null
        return try {
            val builder = MultipartEntityBuilder.create()
            builder.addTextBody("entityType", automationFramework.name, ContentType.TEXT_PLAIN)
            builder.addTextBody("apiVersion", "2", ContentType.TEXT_PLAIN)
            if (!testSuiteId.isNullOrEmpty()) {
                builder.addTextBody(
                    "testsuiteId",
                    testSuiteId,
                    ContentType.TEXT_PLAIN
                )
            }
            if (!testSuiteName.isNullOrEmpty()) builder.addTextBody(
                "testsuiteName",
                testSuiteName,
                ContentType.TEXT_PLAIN
            )
            if (!automationHierarchy.isNullOrEmpty()) builder.addTextBody(
                "automationHierarchy",
                automationHierarchy,
                ContentType.TEXT_PLAIN
            )
            if (!buildName.isNullOrEmpty()) builder.addTextBody(
                "build",
                buildName,
                ContentType.TEXT_PLAIN
            )
            if (!platformName.isNullOrEmpty()) builder.addTextBody(
                "platformID",
                platformName,
                ContentType.TEXT_PLAIN
            )
            if (!project.isNullOrEmpty()) builder.addTextBody("projectID", project, ContentType.TEXT_PLAIN)
            if (!release.isNullOrEmpty()) builder.addTextBody("releaseID", release, ContentType.TEXT_PLAIN)
            if (!cycle.isNullOrEmpty()) builder.addTextBody("cycleID", cycle, ContentType.TEXT_PLAIN)
            if (!testCaseFields.isNullOrEmpty()) builder.addTextBody(
                "testcase_fields",
                testCaseFields,
                ContentType.TEXT_PLAIN
            )
            if (!testSuiteFields.isNullOrEmpty()) builder.addTextBody(
                "testsuite_fields",
                testSuiteFields,
                ContentType.TEXT_PLAIN
            )
            builder.addPart("file", FileBody(file))
            val multipart = builder.build()
            val uploadFile = HttpPost("${url.trimEnd('/')}/rest/import/createandscheduletestresults/1")
            uploadFile.addHeader("accept", "application/json")
            uploadFile.addHeader("scope", "default")
            uploadFile.addHeader("apiKey", key)
            uploadFile.entity = multipart
            httpClient = HttpClients.createDefault()
            response = httpClient.execute(uploadFile)
            val respEntityStr = EntityUtils.toString(response.entity)
            println("Response : $respEntityStr")
            if (response.statusLine.statusCode != 200) {
                throw QTMException("Error uploading file!")
            }
            true
        } catch (e: Exception) {
            println("ERROR : $e")
            throw QTMException("Could not upload file '$file' to test suite!")
        } finally {
            try {
                httpClient!!.close()
                response!!.close()
            } catch (e: Exception) {
            }
        }
    }
}