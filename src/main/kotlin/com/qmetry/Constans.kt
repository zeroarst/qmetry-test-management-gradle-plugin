package com.qmetry

object PluginInfo {
    const val name = "QMetry Test Management Gradle Plugin"
    const val description = "Faster way to linking automated test result to QMetry Test Management, to ship high quality products."
    const val group = "QMetry Test Management"
}

enum class AutomationFramework(val resultFileExtension: String) {
    JUNIT("xml"), TESTNG("xml"), HPUFT("xml"), ROBOT("xml"),
    QAS("json"), CUCUMBER("json"), JSON("json")
}