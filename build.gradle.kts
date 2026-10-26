plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.phosky.antoniojuan"
version = "1.3.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.json:json:20231013")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    intellijPlatform {
        create("IC", "2023.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("com.intellij.java")
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "231"
        }
        changeNotes = """
            See CHANGELOG.md for release notes.
        """.trimIndent()
    }
    pluginVerification {
        ides {
            create("IC", "2023.1.7")
            create("IC", "2024.1.7")
            create("IC", "2025.2.1")
        }
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("262.*")
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
    compileJava {
        targetCompatibility = "17"
        sourceCompatibility = "17"
    }

    // Configuración para runIde con mock API
    // Uso: ./gradlew runIde -PmockApi
    val mockApi: Boolean by project.extra(false)
    val runIde = named("runIde", org.gradle.api.tasks.JavaExec::class.java)
    runIde.configure {
        if (mockApi) {
            jvmArgs?.add("-Dperplexity.api.url=http://127.0.0.1:8765/chat/completions")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
