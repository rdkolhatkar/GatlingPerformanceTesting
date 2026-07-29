# Gatling 3.11+ Standalone Bundle Installation Guide

## Introduction

Starting with **Gatling 3.11**, the traditional standalone bundle has been redesigned.

Older versions (≤ 3.10.x) contained launch scripts such as:

* `bin/gatling.bat`
* `bin/recorder.bat`
* `bin/gatling.sh`

Beginning with **Gatling 3.11**, these scripts have been removed. The standalone bundle is now **Maven Wrapper-based**, making it easier to run Gatling without installing Maven separately. All Gatling commands are executed through the Maven Wrapper (`mvnw` or `mvnw.cmd`). ([Gatling][1])

---

# Prerequisites

Before using Gatling, ensure the following software is installed.

| Software                     | Required    | Notes                                                   |
| ---------------------------- | ----------- | ------------------------------------------------------- |
| Java 17+ (or Java 11–25 LTS) | ✅ Yes       | Gatling runs on the JVM.                                |
| Maven                        | ❌ No        | Maven Wrapper is included.                              |
| Git Bash                     | Optional    | Recommended for Windows users who prefer Unix commands. |
| PowerShell                   | Optional    | Built into Windows.                                     |
| Command Prompt               | Optional    | Built into Windows.                                     |
| IntelliJ IDEA / VS Code      | Recommended | Makes editing simulations easier.                       |

Gatling supports 64-bit OpenJDK LTS releases and recommends using the latest supported version. ([Gatling documentation][2])

---

# Download Gatling

Download the latest Gatling Bundle from the official installation page:

**Official Installation Guide**

[Gatling Installation Guide](https://docs.gatling.io/reference/deploy/install-local/?utm_source=chatgpt.com)

Choose

```
Java + Maven Bundle
```

Download example

```
gatling-charts-highcharts-bundle-3.15.1.zip
```

---

# Extract the ZIP File

Windows users are encouraged to use **7-Zip** instead of the built-in Windows extractor for the bundle. ([Gatling documentation][2])

Example destination

```
D:\Gatling\
```

After extraction

```
D:\Gatling\
└── gatling-charts-highcharts-bundle-3.15.1
```

---

# Folder Structure

Unlike previous versions, the new bundle looks similar to a Maven project.

```
gatling-charts-highcharts-bundle-3.15.1
│
├── .gatling
├── .m2
├── .mvn
├── src
│   ├── test
│   │   ├── java
│   │   └── resources
│
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── target
```

Important folders

| Folder             | Purpose                                |
| ------------------ | -------------------------------------- |
| src/test/java      | Java simulation classes                |
| src/test/resources | Feeders, request bodies, configuration |
| target             | Generated reports                      |
| pom.xml            | Maven configuration                    |
| mvnw               | Maven Wrapper (Linux/macOS/Git Bash)   |
| mvnw.cmd           | Maven Wrapper (Windows CMD/PowerShell) |

([Gatling documentation][2])

---

# Open the Project

## Windows – Git Bash

Open Git Bash.

Navigate to the project:

```bash
cd /d/Gatling/gatling-charts-highcharts-bundle-3.15.1
```

---

## Windows – Command Prompt

```cmd
cd /d D:\Gatling\gatling-charts-highcharts-bundle-3.15.1
```

---

## Windows – PowerShell

```powershell
cd D:\Gatling\gatling-charts-highcharts-bundle-3.15.1
```

---

## macOS Terminal

```bash
cd ~/Downloads/gatling-charts-highcharts-bundle-3.15.1
```

---

## Linux Terminal

```bash
cd ~/Downloads/gatling-charts-highcharts-bundle-3.15.1
```

---

# Verify Java

```bash
java -version
```

Example

```
openjdk version "21"
```

---

# Verify Maven Wrapper

Windows

```cmd
mvnw.cmd --version
```

Linux/macOS/Git Bash

```bash
./mvnw --version
```

You do **not** need Maven installed separately because the wrapper is included. ([Gatling documentation][2])

---

# Launch the Gatling Recorder

## Windows Command Prompt

```cmd
mvnw.cmd gatling:recorder
```

---

## Windows PowerShell

```powershell
.\mvnw.cmd gatling:recorder
```

---

## Windows Git Bash

```bash
./mvnw gatling:recorder
```

---

## macOS

```bash
./mvnw gatling:recorder
```

---

## Linux

```bash
./mvnw gatling:recorder
```

The Recorder opens as a GUI application that acts as an HTTP proxy or can convert HAR files into Gatling simulations. ([Gatling documentation][3])

---

# Run a Simulation

Windows

```cmd
mvnw.cmd gatling:test
```

Linux/macOS/Git Bash

```bash
./mvnw gatling:test
```

From Gatling 3.11 onward, running `gatling:test` starts an interactive prompt if multiple simulations are present. ([Gatling documentation][4])

---

# Run a Specific Simulation

Windows

```cmd
mvnw.cmd gatling:test -Dgatling.simulationClass=com.demo.LoadTest
```

Linux/macOS

```bash
./mvnw gatling:test -Dgatling.simulationClass=com.demo.LoadTest
```

---

# Generate Reports

After execution, reports are generated under

```
target/gatling/
```

Open the latest HTML report in your browser to view detailed performance metrics. ([Gatling documentation][5])

---

# Common Commands

| Task                    | Windows                                                     | Linux/macOS/Git Bash                                      |
| ----------------------- | ----------------------------------------------------------- | --------------------------------------------------------- |
| Check Java              | `java -version`                                             | `java -version`                                           |
| Check Maven Wrapper     | `mvnw.cmd --version`                                        | `./mvnw --version`                                        |
| Launch Recorder         | `mvnw.cmd gatling:recorder`                                 | `./mvnw gatling:recorder`                                 |
| Run Simulation          | `mvnw.cmd gatling:test`                                     | `./mvnw gatling:test`                                     |
| Run Specific Simulation | `mvnw.cmd gatling:test -Dgatling.simulationClass=ClassName` | `./mvnw gatling:test -Dgatling.simulationClass=ClassName` |

---

# Common Errors

### `mvn: command not found`

**Cause:** Maven is not installed or not in your `PATH`.

**Solution:** Use the Maven Wrapper included with the bundle instead:

```bash
./mvnw gatling:test
```

or

```cmd
mvnw.cmd gatling:test
```

---

### `java: command not found`

**Cause:** Java is not installed or `JAVA_HOME`/`PATH` is not configured.

**Solution:** Install a supported JDK and verify:

```bash
java -version
```

---

### `Permission denied`

On Linux/macOS, make the wrapper executable:

```bash
chmod +x mvnw
```

Then run:

```bash
./mvnw gatling:test
```

---

### `./mvnw: No such file or directory`

Ensure you are in the project root where `mvnw` is located:

```bash
pwd
ls
```

You should see:

```
mvnw
pom.xml
src
```

---

# Key Changes in Gatling 3.11+

* The standalone bundle is now **Maven Wrapper-based** instead of using `bin/gatling` scripts.
* You no longer need a separate Maven installation.
* Launch the Recorder with `mvnw gatling:recorder` (Windows: `mvnw.cmd gatling:recorder`).
* Run simulations with `mvnw gatling:test`.
* The bundle primarily targets Java users; Scala and Kotlin users should use Maven, Gradle, or sbt projects. ([Gatling][1])

[1]: https://community.gatling.io/t/gatling-3-11-is-available/8976?utm_source=chatgpt.com "Gatling 3.11 is Available! - Announcements - Gatling"
[2]: https://docs.gatling.io/reference/deploy/install-local/?utm_source=chatgpt.com "Gatling installation with the bundle, build tool, or package manager"
[3]: https://docs.gatling.io/reference/script/http/recorder/?utm_source=chatgpt.com "Gatling HTTP protocol reference - Recorder"
[4]: https://docs.gatling.io/release-notes/gatling/whats-new/3.11/?utm_source=chatgpt.com "What's new in Gatling 3.11"
[5]: https://docs.gatling.io/tutorials/test-as-code/java-jvm/installation-guide/?utm_source=chatgpt.com "Gatling Java and JDK Language SDK Installation Guide"
