package com.qmetry

import org.gradle.api.Project
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.exists

object Utils {

    private val defaultInclusiveFileExtensions = listOf("xml", "json", "png")

    fun findAllTestResults(project: Project, regex: Regex): List<File> {
        return project.projectDir
            .walk()
            .filter { regex.matches(it.path) }
            .toList()
    }

    fun zipFile(file: File, destinationFilePath: File, inclusiveExtensions: List<String>?) {
        zipFiles(listOf(file), destinationFilePath, inclusiveExtensions)
    }

    fun zipFiles(files: List<File>, destinationFilePath: File, inclusiveExtensions: List<String>?) {
        if (destinationFilePath.exists()) destinationFilePath.delete()
        val parentPath = destinationFilePath.toPath().parent
        if (!parentPath.exists()) Files.createDirectories(parentPath)
        if (!destinationFilePath.exists()) destinationFilePath.createNewFile()
        ZipOutputStream(BufferedOutputStream(FileOutputStream(destinationFilePath.absolutePath))).use { out ->
            val buffer = ByteArray(1024)
            files.forEach {
                zipFile(
                    file = it,
                    buffer = buffer,
                    zos = out,
                    inclusiveExtensions = inclusiveExtensions
                )
            }
        }
    }

    private fun zipFile(
        parentPath: String? = null,
        isRoot: Boolean = true,
        file: File,
        buffer: ByteArray,
        zos: ZipOutputStream,
        inclusiveExtensions: List<String>?
    ) {
        when {
            file.isDirectory -> {
                file.listFiles()?.forEach {
                    zipFile(
                        parentPath = if (isRoot) null else file.name,
                        isRoot = false,
                        file = it,
                        buffer = buffer,
                        zos = zos,
                        inclusiveExtensions = inclusiveExtensions
                    )
                }
            }
            file.isFile -> {
                FileInputStream(file).use { fi ->
                    BufferedInputStream(fi).use { bis ->
                        if (file.extension in (inclusiveExtensions ?: defaultInclusiveFileExtensions)) {
                            val fileWithParentPath = if (parentPath != null) "${parentPath}/${file.name}" else file.name
                            val entry = ZipEntry(fileWithParentPath)
                            zos.putNextEntry(entry)
                            while (true) {
                                val readBytes = bis.read(buffer)
                                if (readBytes == -1) break
                                zos.write(buffer, 0, readBytes)
                            }
                        }
                    }
                }
            }
        }
    }
}