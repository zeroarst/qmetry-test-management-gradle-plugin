package com.qmetry

import com.qmetry.Utils.zipFile
import com.qmetry.Utils.zipFiles
import com.qmetry.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class QTMResultsPublisher : DefaultTask() {

    @TaskAction
    @Throws(QTMException::class)
    fun publishResults() {
        try {
            val config = project.extensions.findByType(QTMGradleExtension::class.java)
                ?: throw QTMException("Could not find QTM configuration! please provide qmetryConfig {... } block with appropriate parameters in your build.gradle file!")
            val displayName = "${PluginInfo.name} : Starting Post Build Action"
            // TODO need to test whether print correctly.
            val line = "-".repeat(displayName.length)
            println(
                """
                $line
                $displayName
                $line
                """.trimIndent()
            )

            println("Using Automation Framework '${config.parsedAutomationFramework}'")

            if (config.parsedTestSuiteId.isNotEmpty())
                println("Using Test Suite Id '${config.parsedTestSuiteId}'")

            if (config.parsedTestSuiteName.isNotEmpty())
                println("Using Test Suite Name '${config.parsedTestSuiteName}'")

            if (config.parsedAutomationHierarchy.isNotEmpty())
                println("Using Automation Hierarchy '${config.parsedAutomationHierarchy}'")

            if (config.parsedPlatform.isNotEmpty())
                println("Using Platform '${config.parsedPlatform}'")

            if (config.parsedBuild.isNotEmpty())
                println("Using Build (or Drop) '${config.parsedBuild}'")

            println("Using Project '${config.parsedProject}'")

            println("Using QTM url '${config.parsedQtmUrl}'")

            require(config.parsedQtmAutomationApiKey.isNotBlank())

            if (config.parsedRelease.isNotEmpty())
                println("Using Release '${config.parsedRelease}'")

            if (config.parsedCycle.isNotEmpty())
                println("Using Cycle '${config.parsedCycle}'")

            if (config.parsedTestcaseFields.isNotEmpty())
                println("Using Test Case Fields '${config.parsedTestcaseFields}'")

            if (config.parsedTestsuiteFields.isNotEmpty())
                println("Using Test Suite Fields '${config.parsedTestsuiteFields}'")

            println("Using File Matcher Regex '${config.parsedTestResultFilePathMatherRegex}'")

            val matchedFiles = Utils.findAllTestResults(project, config.parsedTestResultFilePathMatherRegex)
            if (matchedFiles.isEmpty()) throw QTMException("Result file(s) not Found! Tested regex '${config.parsedTestResultFilePathMatherRegex}'")

            when (config.parsedAutomationFramework) {
                // TODO This has been modified but not fully tested.
                //  We are unlikely to use QAS, but keep what we have pulled from the original plugin source code.
                AutomationFramework.QAS -> {
                    if (matchedFiles.size > 1) throw Exception("For QAS enter path to a directory or to a zip file. Path matcher regex=${config.parsedTestResultFilePathMatherRegex}")
                    val resultFile = matchedFiles[0]
                    when {
                        resultFile.isFile -> {
                            if (!resultFile.extension.equals("zip", ignoreCase = true))
                                throw QTMException("Upload .Zip file or configure Directory to upload QAS results.")
                            uploadFile(resultFile, config)
                        }
                        resultFile.isDirectory -> {
                            println("Reading result files from Directory '$resultFile'")
                            // list all directories in the given directory.
                            val dirs = resultFile.listFiles { directory, fileName -> (directory.isDirectory) }
                                ?: throw QTMException("Could not find result file(s) at given path!")

                            // find the latest modified directory.
                            var lastMod = Long.MIN_VALUE
                            var latestDir: File? = null
                            for (dir in dirs) {
                                if (dir.isDirectory && dir.lastModified() > lastMod) {
                                    latestDir = dir
                                    lastMod = dir.lastModified()
                                }
                            }
                            if (latestDir == null) throw QTMException("Results' directory of type QAS not found in given directory '${resultFile.absolutePath}'")

                            // zip all json files in the directory.
                            val zipArchive = File(latestDir, "qmetry_result.zip")
                            zipFile(latestDir, zipArchive, listOf("json"))

                            // TODO test replaced above.
                            // val zipUtils = ZipUtils("json")
                            // zipUtils.zipDirectory(latestDir, "qmetry_result.zip")

                            if (!zipArchive.exists()) throw QTMException("Failed to create zip archive for QAS results at directory '${latestDir.absolutePath}'")
                            uploadFile(zipArchive, config)
                        }
                    }
                }
                else -> {
                    val zipFile = File("${project.buildDir.path}/test-results-aggregated/testresult.zip")
                    zipFiles(matchedFiles, zipFile, listOf(config.parsedAutomationFramework.resultFileExtension, "png"))
                    uploadFile(zipFile, config)
                }
            }
        } catch (e: QTMException) {
            println("ERROR : ${e.message}")
        } catch (e: Exception) {
            println("ERROR : $e")
        }
        println("\n${PluginInfo.name}: Finished Post Build Action!")
    }

    private fun uploadFile(zipFile: File, config: QTMGradleExtension) {
        println("${PluginInfo.name}: Uploading result file '$zipFile'")
        val conn = QTMApiConnection(config.parsedQtmUrl, config.parsedQtmAutomationApiKey)
        synchronized(conn) {
            conn.uploadFileToTestSuite(
                zipFile,
                config.parsedTestSuiteId,
                config.testSuiteName,
                config.parsedAutomationFramework,
                config.parsedAutomationHierarchy,
                config.parsedBuild,
                config.parsedPlatform,
                config.parsedProject,
                config.parsedRelease,
                config.parsedCycle,
                config.testcaseFields,
                config.parsedTestsuiteFields
            )
        }
        println("${PluginInfo.name}: Result file successfully uploaded!")
    }
}