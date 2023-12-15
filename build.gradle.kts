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
    implementation("com.github.cretz.kastree:kastree-ast-jvm:0.4.0")
    implementation("com.github.cretz.kastree:kastree-ast-psi:0.4.0")
    // testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test<Test> {
    useJUnitPlatform()
}

task("report", Exec::class) {
    workingDir("./build/reports/tests/test")
    commandLine("cmd", "/c", "start index.html")
}

