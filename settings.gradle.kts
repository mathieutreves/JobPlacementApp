rootProject.name = "final_project"

// Include all directories that have a build.gradle or build.gradle.kts file
rootDir.listFiles()?.forEach { file ->
    if (file.isDirectory && file.name != "spa") {
        val buildGradle = file.resolve("build.gradle")
        val buildGradleKts = file.resolve("build.gradle.kts")
        if (buildGradle.exists() || buildGradleKts.exists()) {
            include(file.name)
        }
    }
}