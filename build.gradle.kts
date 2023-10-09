plugins {
    kotlin("jvm") version "1.7.21"
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.0.0"
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(group = "org.apache.httpcomponents", name = "httpclient", version = "4.5.4")
    implementation(group = "org.apache.httpcomponents", name = "httpmime", version = "4.5.4")
    implementation(group = "com.googlecode.json-simple", name = "json-simple", version = "1.1.1")
    implementation(group = "commons-httpclient", name = "commons-httpclient", version = "3.1")
    implementation(group = "commons-io", name = "commons-io", version = "2.6")
}

group = property("GROUP").toString()
version = property("VERSION").toString()

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            displayName = property("DISPLAY_NAME").toString()
        }
    }
}


pluginBundle {
  website = property("WEBSITE").toString()
  vcsUrl = property("VCS_URL").toString()
  description = property("DESCRIPTION").toString()
  tags = listOf("test", "automation", "qmetry", "qtm")
}
