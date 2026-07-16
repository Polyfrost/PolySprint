import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.kikugie.loom-back-compat")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("dev.deftu.gradle.bloom") version "0.2.0"
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val modid = property("mod.id") as String
val modname = property("mod.name") as String
val modversion = property("mod.version") as String
val mcversion = property("minecraft_version") as String
val versionrange = property("minecraft_version_range") as String
val loaderversion = property("loader_version") as String
val oneconfigVersion = "1.0.0-beta.7"

base {
    archivesName.set("$modid-$modversion+$mcversion")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://maven.parchmentmc.org") {
        content { includeGroup("org.parchmentmc") }
    }
    maven("https://maven.gegy.dev/releases") {
        content { includeGroup("dev.lambdaurora") }
    }
    maven("https://central.sonatype.com/repository/maven-snapshots") {
        content { includeGroup("net.kyori") }
    }
    maven("https://maven.deftu.dev/releases") {
        content { includeGroup("dev.deftu") }
    }
    maven("https://maven.fabricmc.net/releases") {
        content { includeGroup("net.fabricmc") }
    }
    maven("https://maven.terraformersmc.com/releases") {
        content { includeGroup("com.terraformersmc") }
    }
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run" // This sets the run folder for all mc versions to the same folder. Remove this line if you want individual run folders.
    }

    runConfigs.remove(runConfigs["server"]) // Removes server run configs
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")

    val hasOfficialMappings = findProperty("has_official_mappings")?.toString()?.toBoolean() ?: true
    if (hasOfficialMappings) {
        @Suppress("UnstableApiUsage")
        mappings(loom.layered {
            officialMojangMappings()
            optionalProp("${property("parchment_version")}") {
                parchment("org.parchmentmc.data:parchment-${property("minecraft_version")}:$it@zip")
            }
            optionalProp("${property("yalmm_version")}") {
                mappings("dev.lambdaurora:yalmm-mojbackward:${property("minecraft_version")}+build.$it")
            }
        })
    } else {
        findProperty("mappings_version")?.toString()?.takeUnless { it.isBlank() }?.let {
            mappings(it)
        }
    }

    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-command-api-v2:${property("fabric_command_api_v2_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-lifecycle-events-v1:${property("fabric_lifecycle_events_v1_version")}")
    modImplementation("org.polyfrost.oneconfig:${property("minecraft_version")}-fabric:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:commands:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:config:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:config-impl:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:events:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:internal:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:ui:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:utils:$oneconfigVersion")
    implementation("org.polyfrost.oneconfig:hud:$oneconfigVersion")
    implementation("org.polyfrost:polyui:2.1.7")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
}

bloom {
    replacement("@MOD_ID@", modid!!)
    replacement("@MOD_NAME@", modname!!)
    replacement("@MOD_VERSION@", modversion!!)
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to modid,
        "mod_name" to modname,
        "mod_version" to modversion,
        "minecraft_version_range" to versionrange,
        "loader_version" to loaderversion
    )

    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

val javaVersionStr = findProperty("java_version")?.toString() ?: "21"
val javaVersionInt = javaVersionStr.toInt()

val kotlinJvmTarget = when (javaVersionInt) {
    21 -> JvmTarget.JVM_21
    22 -> JvmTarget.JVM_22
    23 -> JvmTarget.JVM_23
    24 -> JvmTarget.JVM_24
    25 -> JvmTarget.JVM_25
    else -> JvmTarget.JVM_21
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersionInt)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(kotlinJvmTarget)
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersionInt))
    }
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)

    from("LICENSE") {
        rename { "${it}_${inputs.properties["archivesName"]}" }
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)

val modrinthMinecraftVersionOverride = mapOf(
    "26.1" to listOf("26.1", "26.1.1", "26.1.2")
)

val modrinthId = listOf("oneconfig.publish.modrinth", "publish.modrinth").firstNotNullOfOrNull { findProperty(it) }?.toString()?.takeIf { it.isNotBlank() }
val modrinthToken = listOf("oneconfig.publish.modrinth.token", "publish.modrinth.token", "modrinth.token").firstNotNullOfOrNull { findProperty(it) }?.toString()?.takeIf { it.isNotBlank() }
val minecraftVersion = modrinthMinecraftVersionOverride[mcversion] ?: listOf(mcversion)
val publishJarTaskName = if ("remapJar" in tasks.names) "remapJar" else "jar"
val changelogs = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

publishMods {
    file = tasks.named<AbstractArchiveTask>(publishJarTaskName).flatMap { it.archiveFile }

    displayName = modversion
    version = "v$modversion"
    changelog = changelogs
    type = STABLE

    modLoaders.add("fabric")

    dryRun = modrinthId == null || modrinthToken == null

    if (modrinthId != null) {
        modrinth {
            projectId = modrinthId
            accessToken = modrinthToken.orEmpty()

            minecraftVersions.addAll(minecraftVersion)

            requires("oneconfig")
            requires("fabric-language-kotlin")
        }
    }
}
