plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.patcher") version "1.7.1"
}

val paperMavenPublicUrl = "https://papermc.io/repo/repository/maven-public/"

repositories {
    mavenCentral()
    maven(paperMavenPublicUrl) {
        content { onlyForConfigurations(configurations.paperclip.name) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.10.4:fat")
    decompiler("org.vineflower:vineflower:1.10.1")
    paperclip("io.papermc:paperclip:3.0.3")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
        maven("https://ci.pluginwiki.us/plugin/repository/everything/")
    }
}

paperweight {
    serverProject.set(project(":marigold-server"))

    remapRepo.set(paperMavenPublicUrl)
    decompileRepo.set(paperMavenPublicUrl)

    useStandardUpstream("leaf") {
        url.set(github("Winds-Studio", "Leaf"))
        ref.set(providers.gradleProperty("leafRef"))

        withStandardPatcher {
            apiOutputDir.set(layout.projectDirectory.dir("marigold-api"))
            serverOutputDir.set(layout.projectDirectory.dir("marigold-server"))

            apiSourceDirPath.set("Leaf-API")
            serverSourceDirPath.set("Leaf-Server")
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = layout.projectDirectory.dir("patches/generated-api")
            outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
        }

    }
}
