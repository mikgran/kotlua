plugins {
    kotlin("jvm") version ("1.9.20")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

sourceSets.main {
    kotlin.srcDirs("src/main/kotlin")
}

dependencies {
    // testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test<Test> {
    useJUnitPlatform()
}

task<Exec>("report") {
    exec {
        workingDir("./build/reports/tests/test")
        commandLine("cmd", "/c", "start index.html")
    }
}
