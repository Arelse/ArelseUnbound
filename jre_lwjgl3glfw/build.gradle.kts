plugins {
    java
}

group = "org.lwjgl.glfw"

// LWJGL 3.4.x Java classes from Maven Central (natives are built separately for Android).
val lwjglVersion: String = (project.findProperty("lwjglVersion") as? String) ?: "3.4.1"

configurations.getByName("default").isCanBeResolved = true

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("lwjgl-glfw-classes")
    destinationDirectory.set(file("../ZalithLauncher/src/main/assets/components/lwjgl3/"))
    // Auto update the version with a timestamp so the project jar gets updated by Pojav
    doLast {
        val versionFile = file("../ZalithLauncher/src/main/assets/components/lwjgl3/version")
        versionFile.writeText(System.currentTimeMillis().toString())
    }
    from({
        configurations.getByName("default").map {
            println(it.name)
            if (it.isDirectory) it else zipTree(it)
        }
    })
    exclude("net/java/openjdk/cacio/ctc/**")
    manifest {
        attributes("Manifest-Version" to lwjglVersion)
        attributes("Automatic-Module-Name" to "org.lwjgl")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    // Local jars that are not on Maven Central
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("lwjgl-lwjglx.jar", "jsr305.jar"))))

    // LWJGL 3.4.x Java API (no natives: Zalith provides its own Android natives)
    listOf(
        "lwjgl", "lwjgl-glfw", "lwjgl-opengl", "lwjgl-openal", "lwjgl-stb",
        "lwjgl-freetype", "lwjgl-tinyfd", "lwjgl-jemalloc", "lwjgl-nanovg",
        "lwjgl-sdl", "lwjgl-vulkan", "lwjgl-shaderc", "lwjgl-spvc", "lwjgl-vma"
    ).forEach { artifact ->
        implementation("org.lwjgl:$artifact:$lwjglVersion")
    }
}
