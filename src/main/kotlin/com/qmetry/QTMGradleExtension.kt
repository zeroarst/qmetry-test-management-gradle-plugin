package com.qmetry

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

open class QTMGradleExtension {
    var qtmUrl: String? = null
    var qtmAutomationApiKey: String? = null
    var automationFramework: String? = null
    var automationHierarchy: String? = null
    var testResultFilePathMatcherRegex: String? = null
    var testSuiteId: String? = null

    @JvmField
    var testSuiteName: String? = null
    var platform: String? = null
    var build: String? = null
    var project: String? = null
    var release: String? = null
    var cycle: String? = null

    @JvmField
    var testcaseFields: String? = null
    var testsuiteFields: String? = null

    @get:Throws(QTMException::class)
    val parsedQtmUrl: String
        get() {
            return qtmUrl?.let {
                it.takeIf {
                    it.startsWith("http:/") || it.startsWith("https:/")
                } ?: throw QTMException("Please provide a valid QMetry Test Management URL")
            } ?: throw QTMException("Please provide your QMetry Test Management URL in qtmConfig block as 'qtmUrl'")
        }

    @get:Throws(QTMException::class)
    val parsedQtmAutomationApiKey: String
        get() {
            return qtmAutomationApiKey
                ?: throw QTMException("Please provide your QMetry Test Management Automation API Key in qtmConfig block as 'qtmAutomationApiKey'")
        }

    @get:Throws(QTMException::class)
    val parsedTestResultFilePathMatherRegex: Regex
        get() {
            return runCatching { (testResultFilePathMatcherRegex ?: defaultPathMatcherRegex).toRegex() }.getOrNull()
                ?: throw QTMException("Please provide a valid test result file path matcher regex in qtmConfig block as 'testResultFilePathMatcherRegex'")
        }

    @get:Throws(QTMException::class)
    val parsedProject: String
        get() {
            return project?.let { it ->
                val tempProject = it.trim { it <= ' ' }
                if (tempProject.length < 2) throw QTMException("Please provide your qmetry project id, key or name in qtmConfig block as 'project'")
                tempProject.takeIf { it.isNotBlank() }
            } ?: throw QTMException("Please provide your qmetry project id, key or name in qtmConfig block as 'project'")
        }

    @get:Throws(QTMException::class)
    val parsedRelease: String
        get() = if (release == null) "" else release!!.trim { it <= ' ' }

    @get:Throws(QTMException::class)
    val parsedCycle: String
        get() = if (cycle == null) {
            ""
        } else {
            if (release == null || release!!.trim { it <= ' ' }.isEmpty()) {
                throw QTMException("Please provide release value for cycle!")
            } else {
                cycle!!.trim { it <= ' ' }
            }
        }
    val parsedBuild: String
        get() = if (build == null) "" else build!!.trim { it <= ' ' }

    val parsedPlatform: String
        get() = if (platform == null) "" else platform!!.trim { it <= ' ' }

    val parsedTestSuiteId: String
        get() = if (testSuiteId == null) "" else testSuiteId!!.trim { it <= ' ' }

    val parsedTestSuiteName: String
        get() = if (testSuiteName == null) "" else testSuiteName!!.trim { it <= ' ' }

    @get:Throws(QTMException::class)
    val parsedAutomationFramework: AutomationFramework
        get() {
            return automationFramework?.let {
                runCatching {
                    AutomationFramework.valueOf(it)
                }.getOrNull() ?: throw QTMException(
                    "Automation Framework '$it' not supported. Use [${
                        AutomationFramework.values().joinToString(", ")
                    }]"
                )
            } ?: throw QTMException("Please provide your Automation Framework in qtmConfig block as 'automationFramework'")
        }
    val parsedAutomationHierarchy: String
        get() = if (automationHierarchy == null) "" else automationHierarchy!!.trim { it <= ' ' }

    @get:Throws(QTMException::class)
    val parsedTestcaseFields: String
        get() = if (testcaseFields == null) "" else try {
            val parser = JSONParser()
            val j = parser.parse(testcaseFields) as JSONObject
            j.toString()
        } catch (ex: Exception) {
            throw QTMException("Please provide correct Json data of Test case fields !")
        }

    @get:Throws(QTMException::class)
    val parsedTestsuiteFields: String
        get() = if (testsuiteFields == null) "" else try {
            val parser = JSONParser()
            val j = parser.parse(testsuiteFields) as JSONObject
            j.toString()
        } catch (ex: Exception) {
            throw QTMException("Please provide correct Json data of Test suite fields !")
        }

    companion object {
        const val defaultPathMatcherRegex = ".*build[\\\\/]test-results[\\\\/]test(.*)?UnitTest\$"
    }
}