plugins {
    id("java")
    `maven-publish`
}

group = "net.botwithus"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        setUrl("https://nexus.botwithus.net/repository/maven-releases/")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

configurations {
    create("includeInJar") {
        this.isTransitive = false
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
    sourceCompatibility = "20"
    targetCompatibility = "20"
}

dependencies {
    // BotWithUs API dependencies
    implementation("net.botwithus.rs3:botwithus-api:1.+")
    implementation("net.botwithus.xapi.public:api:1.+")
    "includeInJar"("net.botwithus.xapi.public:api:1.+")

    // JSON handling for configuration
    implementation("com.google.code.gson:gson:2.10.1")

    // Testing dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

val copyJar by tasks.register<Copy>("copyJar") {
    from("build/libs/")
    into("${System.getProperty("user.home")}\\BotWithUs\\scripts\\local\\")
    include("*.jar")

    doLast {
        println("Copied JAR to: ${System.getProperty("user.home")}\\BotWithUs\\scripts\\local\\")
    }
}

tasks.named<Jar>("jar") {
    from({
        configurations["includeInJar"].map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    finalizedBy(copyJar)

    manifest {
        attributes(
            "Main-Class" to "net.botwithus.CookingScriptSimple",
            "Implementation-Title" to "RS3 Cooking Script",
            "Implementation-Version" to version,
            "Implementation-Vendor" to "BotWithUs Community"
        )
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// Custom task to clean and rebuild
tasks.register("cleanBuild") {
    dependsOn("clean", "build")
    tasks.findByName("build")?.mustRunAfter("clean")
}
