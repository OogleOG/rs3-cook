plugins {
    id("java")
    `maven-publish`
}

group = "net.botwithus.rs3cook"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://nexus.botwithus.net/repository/maven-snapshots/") }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

tasks.withType<JavaCompile> {
    // keep only if you actually need preview features
    options.compilerArgs.add("--enable-preview")
} /** Libs you want to *embed* (unzipped) inside each jar (simple shading). */
val includeInJar by configurations.creating {
    isTransitive = false
}

dependencies {
    // Compile against BWU APIs (do NOT embed these)
    compileOnly("net.botwithus.rs3:botwithus-api:1.0.0-SNAPSHOT")
    compileOnly("net.botwithus.xapi.public:api:1.0.0-SNAPSHOT")

    // Local alternative if snapshots don't resolve:
    // compileOnly(files("libs/rs3-api.jar"))
    // compileOnly(files("libs/botwithusx-api.jar"))

    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.test { useJUnitPlatform() }

/** Helper to embed includeInJar libs into a Jar task. */
fun Jar.embedIncludeInJar() {
    from(provider { includeInJar.resolve().map { zipTree(it) } })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

/** Declare your bots here: baseName = jar base name; iniPath = where the bot's script.ini lives. */
data class Bot(val displayName: String, val baseName: String, val iniPath: String)

val bots = listOf(
    Bot(
        displayName = "RS3 Cooking Script 2",
        baseName = "RS3-Cooking",
        iniPath = "src/main/resources/script.ini"
    )
)

/** Destination where BWU loads local scripts on Windows. */
val bwuLocalDir = file("${System.getProperty("user.home")}\\BotWithUs\\scripts\\local\\")

/** Create per-bot jar + install tasks dynamically. */
val botJarTasks = mutableListOf<TaskProvider<Jar>>()
val installTasks = mutableListOf<TaskProvider<Copy>>()

bots.forEach { bot ->
    val iniFile = layout.projectDirectory.file(bot.iniPath)

    val jarTask = tasks.register<Jar>("jar${bot.baseName}") {
        dependsOn(tasks.named("classes"))

        archiveBaseName.set(bot.baseName)
        archiveVersion.set(project.version.toString())
        destinationDirectory.set(layout.buildDirectory.dir("libs"))

        // sanity check
        doFirst {
            if (!iniFile.asFile.exists()) {
                throw GradleException("Missing script.ini for '${bot.displayName}' at ${iniFile.asFile.absolutePath}")
            }
        }

        // compiled classes/resources
        from(sourceSets.main.get().output)

        // include ONLY this bot's script.ini at jar root
        from(iniFile) { into("") }

        // embed any libs you've put in includeInJar
        embedIncludeInJar()
    }

    botJarTasks += jarTask

    val installTask = tasks.register<Copy>("install${bot.baseName}") {
        dependsOn(jarTask)
        from(jarTask.flatMap { it.archiveFile }) // the actual .jar file
        into(bwuLocalDir)
        doFirst { bwuLocalDir.mkdirs() }
    }

    installTasks += installTask
}

/** Build lifecycle: produce and install all bot jars. */
tasks.assemble { dependsOn(botJarTasks) }
tasks.build { dependsOn(botJarTasks + installTasks) }

/** Optional: print paths of produced jars. */
tasks.register("printJarPaths") {
    dependsOn(botJarTasks)
    doLast {
        botJarTasks.forEach { tp ->
            val f = tp.get().archiveFile.get().asFile
            println("${tp.name} -> ${f.absolutePath}")
        }
    }
}