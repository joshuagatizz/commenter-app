plugins {
    id("java")
    id("io.ratpack.ratpack-java") version "2.0.0-rc-1"
    id("io.freefair.lombok") version "8.6"
}

group = "com.commenter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("io.ratpack:ratpack-guice:2.0.0-rc-1")
    implementation("com.google.inject:guice:7.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.36")
}

tasks.test {
    useJUnitPlatform()
}
