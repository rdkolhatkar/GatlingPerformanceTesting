plugins {
    id("java")
    id("scala")
    id("io.gatling.gradle") version "3.9.5" // Ensure the version matches the latest compatible version
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.13.8")
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.9.5")
    implementation("io.gatling:gatling-test-framework:3.9.5")
    testImplementation("junit:junit:4.13.2")
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java"))
        }
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
    test {
        scala {
            setSrcDirs(listOf("src/test/scala"))
        }
        java {
            setSrcDirs(listOf("src/test/java")) // Include this if you have Java test sources
        }
        resources {
            setSrcDirs(listOf("src/test/resources"))
        }
    }
}

tasks.register<JavaExec>("perfTest") {
    group = "verification"
    description = "Run Gatling performance tests"
    mainClass.set("io.gatling.app.Gatling")
    classpath = sourceSets["test"].runtimeClasspath
    args = listOf("-s", "your.gatling.package.YourSimulationClass")

    jvmArgs = listOf("-Xms512M", "-Xmx2G")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<ScalaCompile> {
    scalaCompileOptions.encoding = "UTF-8"
}
