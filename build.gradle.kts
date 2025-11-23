plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.phosky.antoniojuan"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        // Algunos plugins como ejemplo (añade los que necesites):
        bundledPlugin("com.intellij.java")
        // Puedes añadir otros si usas APIs específicas, como:
        // bundledPlugin("com.jetbrains.python")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }
        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("251")
        untilBuild.set("*")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17" // ¡Usa solo 17 aquí!
    }
    compileJava {
        targetCompatibility = "17"
        sourceCompatibility = "17"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        // Si compilas para JVM 21 asegúrate que todo tu código y dependencias lo permiten,
        // pero para plugins de JetBrains el target garantizado es 17.
    }
}
