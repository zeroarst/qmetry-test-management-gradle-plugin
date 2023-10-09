package com.qmetry

import org.gradle.api.Plugin
import org.gradle.api.Project

class QTMGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("qtmConfig", QTMGradleExtension::class.java)
        project.tasks.create("qtmUploadResults", QTMResultsPublisher::class.java)
    }
}