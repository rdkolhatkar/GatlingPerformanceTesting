# Gatling Complete Setup Guide — Bundle, Maven, Gradle & Ant (Windows, macOS, Linux)

A complete, beginner-to-advanced guide for installing **Gatling Community Edition**, running it through the **standalone bundle** or through **Maven, Gradle, or Ant**, using the **Recorder**, running simulations, and reading the HTML report.

> **New to load testing?** Read [What is Gatling?](#what-is-gatling) first.
> **Just want the fastest path?** Go to [Path A — Standalone Bundle](#path-a--standalone-bundle-fastest-start).
> **Already using a build tool?** Jump to [Path B — Maven](#path-b--maven-plugin), [Path C — Gradle](#path-c--gradle-plugin), or [Path D — Ant](#path-d--apache-ant-unofficial).

---

## Table of Contents

1. [What is Gatling?](#what-is-gatling)
2. [Choosing Your Setup Path](#choosing-your-setup-path)
3. [Step 0 — Install Prerequisites (All Paths)](#step-0--install-prerequisites-all-paths)
4. [Path A — Standalone Bundle (Fastest Start)](#path-a--standalone-bundle-fastest-start)
5. [Path B — Maven Plugin](#path-b--maven-plugin)
6. [Path C — Gradle Plugin](#path-c--gradle-plugin)
7. [Path D — Apache Ant (Unofficial)](#path-d--apache-ant-unofficial)
8. [Using the Gatling Recorder (All Paths)](#using-the-gatling-recorder-all-paths)
9. [Running Simulations & Reading Reports](#running-simulations--reading-reports)
10. [Command Cheat Sheet by Build Tool](#command-cheat-sheet-by-build-tool)
11. [Common Errors & Solutions](#common-errors--solutions)
12. [Tips for Advanced Users](#tips-for-advanced-users)
13. [References](#references)

---

## What is Gatling?

**Gatling** is an open-source load and performance testing tool. It simulates many virtual users hitting your application concurrently and produces detailed reports on response times, throughput, and errors.

Gatling uses an asynchronous, non-blocking architecture (rather than one thread per virtual user like some older tools), so a single machine can generate a large amount of load efficiently.

| Edition | Best for |
|---|---|
| **Community Edition (Free, Open Source)** | Individuals, small teams, local/CI testing — covered in this guide |
| **Enterprise Edition** | Distributed cloud runs, dashboards, team collaboration, CI/CD integrations |

---

## Choosing Your Setup Path

Gatling officially supports several ways to build and run your simulations. There is **no official Ant plugin** — Ant integration is possible but must be wired manually, which this guide also covers for teams with legacy Ant pipelines.

| Path | Tool | Officially Supported? | Best for |
|---|---|---|---|
| **A** | Standalone Bundle (Maven Wrapper under the hood) | ✅ Yes | Beginners, QA engineers without a dev setup, restrictive corporate firewalls |
| **B** | Maven | ✅ Yes | Java/Scala/Kotlin teams already using Maven, CI/CD pipelines |
| **C** | Gradle | ✅ Yes | Teams using Gradle multi-module builds, Android/Kotlin-heavy shops |
| **D** | Ant | ⚠️ Unofficial (community workaround) | Legacy build pipelines that must remain on Ant |

> **Language note:** The standalone bundle only supports **Java** simulations. If you want to write simulations in **Scala** or **Kotlin**, use the Maven or Gradle plugin instead.

---

## Step 0 — Install Prerequisites (All Paths)

### 0.1 Install a Java JDK

Gatling requires a **64-bit OpenJDK LTS version** (11 through 25). JDK 17 or 21 LTS is recommended. 32-bit systems and OpenJ9 builds are **not** supported.

<details>
<summary><strong>Windows</strong></summary>

1. Download an OpenJDK build (e.g., [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/)).
2. Run the installer, checking "Set JAVA_HOME" and "Add to PATH".
3. Verify:
   ```cmd
   java -version
   ```
</details>

<details>
<summary><strong>macOS</strong></summary>

```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
java -version
```
</details>

<details>
<summary><strong>Linux</strong></summary>

Debian/Ubuntu:
```bash
sudo apt update && sudo apt install openjdk-17-jdk
```
Fedora/RHEL:
```bash
sudo dnf install java-17-openjdk
```
Verify:
```bash
java -version
```
</details>

**Expected output:**
```text
openjdk version "17.0.x"
```

### 0.2 Install Your Build Tool (if using Path B, C, or D)

| Build Tool | Install Check | Notes |
|---|---|---|
| **Maven** | `mvn -version` | Not required for Path A — the bundle ships its own Maven Wrapper |
| **Gradle** | `gradle -version` | Or just use a project's own `./gradlew` wrapper |
| **Ant** | `ant -version` | Requires `ANT_HOME` set and `bin` added to `PATH` |

<details>
<summary><strong>Install Maven</strong></summary>

- **Windows**: Download from [maven.apache.org](https://maven.apache.org/download.cgi), unzip, add `bin` to `PATH`.
- **macOS**: `brew install maven`
- **Linux**: `sudo apt install maven` (Debian/Ubuntu) or `sudo dnf install maven` (Fedora)
</details>

<details>
<summary><strong>Install Gradle</strong></summary>

- **Windows**: Download from [gradle.org/install](https://gradle.org/install/), unzip, add `bin` to `PATH`. Or use [SDKMAN](https://sdkman.io/) via Git Bash/WSL.
- **macOS**: `brew install gradle`
- **Linux**: `sdk install gradle` (via SDKMAN) or your package manager
</details>

<details>
<summary><strong>Install Ant</strong></summary>

- **Windows**: Download from [ant.apache.org](https://ant.apache.org/bindownload.cgi), unzip, set `ANT_HOME`, add `%ANT_HOME%\bin` to `PATH`.
- **macOS**: `brew install ant`
- **Linux**: `sudo apt install ant` (Debian/Ubuntu) or `sudo dnf install ant` (Fedora)
</details>

### 0.3 A Terminal & (Optional) IDE

- **Windows**: Command Prompt, PowerShell, or Git Bash
- **macOS**: Terminal or iTerm2
- **Linux**: Your default shell

**IntelliJ IDEA Community Edition** (free) recognizes Maven and Gradle projects automatically and is the most common IDE choice for Gatling. VS Code is a lighter alternative.

---

## Path A — Standalone Bundle (Fastest Start)

Best for beginners. This is a self-contained ZIP with everything pre-configured — no separate Maven/Gradle install needed.

### A.1 Download

- https://gatling.io/download-gatling-community-edition
- Alternative reference: https://docs.gatling.io/reference/install/oss/

Always grab the latest stable release — avoid milestone ("M") builds, which are undocumented and for internal/Enterprise use only.

### A.2 Extract the ZIP

> ⚠️ **Windows users:** the built-in "Extract All" tool can mishandle this bundle. Use **7-Zip** or `tar` instead. Also avoid extracting under `C:\Program Files` — permission issues can occur there.

Create a destination folder:
```bash
mkdir C:\Tools\Gatling        # Windows (Command Prompt)
mkdir -p ~/Tools/Gatling      # macOS/Linux
```

**PowerShell `Expand-Archive` (Windows)**
```powershell
powershell -Command "Expand-Archive -Path 'C:\Downloads\gatling.zip' -DestinationPath 'C:\Tools\Gatling' -Force"
```

**`tar` (built into Windows 10/11, macOS, Linux)**
```bash
tar -xf C:\Downloads\gatling.zip -C C:\Tools\Gatling
```

**`unzip` (macOS, Linux, or Git Bash on Windows)**
```bash
unzip ~/Downloads/gatling.zip -d ~/Tools/Gatling
```

**7-Zip (recommended on Windows)**
```cmd
7z x C:\Downloads\gatling.zip -oC:\Tools\Gatling
```
*(No space after `-o`.)*

Verify:
```bash
dir C:\Tools\Gatling      # Windows
ls -la ~/Tools/Gatling    # macOS/Linux
```

### A.3 Folder Structure

```
gatling
│
├── mvnw.cmd / mvnw   # Maven Wrapper launcher — the bundle runs on Maven under the hood
├── pom.xml
├── src
│   └── test
│       └── java
│           └── simulations   # Your simulation scripts live here
├── target             # Compiled output & HTML reports
└── ...
```

### A.4 Bundle Commands

**Windows**
```cmd
mvnw.cmd gatling:recorder
mvnw.cmd gatling:test
```

**macOS/Linux**
```bash
./mvnw gatling:recorder
./mvnw gatling:test
```

➡️ Continue to [Using the Gatling Recorder](#using-the-gatling-recorder-all-paths).

---

## Path B — Maven Plugin

Best for teams already using Maven, or anyone who wants Scala/Kotlin support and a clean IDE-friendly project.

### B.1 Fastest Start — Clone the Official Demo Project

```bash
git clone https://github.com/gatling/gatling-maven-plugin-demo-java.git
cd gatling-maven-plugin-demo-java
./mvnw clean install       # macOS/Linux
mvnw.cmd clean install     # Windows
```

This uses the Maven Wrapper, so you don't strictly need Maven installed system-wide — but installing it (Step 0.2) is still useful if you plan to manage multiple projects.

### B.2 Manual Setup — Add the Plugin to an Existing Project

Add this to your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.gatling</groupId>
      <artifactId>gatling-maven-plugin</artifactId>
      <version>REPLACE_WITH_LATEST_VERSION</version>
    </plugin>
  </plugins>
</build>
```

Add the Gatling dependency:

```xml
<dependency>
  <groupId>io.gatling.highcharts</groupId>
  <artifactId>gatling-charts-highcharts</artifactId>
  <version>REPLACE_WITH_LATEST_VERSION</version>
  <scope>test</scope>
</dependency>
```

> Check https://docs.gatling.io for the current recommended version — don't hardcode an old one from a tutorial.

Place simulations under `src/test/java/simulations` (or `src/test/scala` / `src/test/kotlin` if using those languages).

### B.3 Run

```bash
mvn gatling:test                     # run interactively, choose a simulation
mvn gatling:test -Dgatling.simulationClass=simulations.LoginSimulation   # run a specific one
mvn gatling:recorder                 # launch the Recorder
```

➡️ Continue to [Using the Gatling Recorder](#using-the-gatling-recorder-all-paths).

---

## Path C — Gradle Plugin

Best for teams on Gradle, multi-module builds, or Kotlin-heavy projects.

> **Compatibility note:** The Gatling Gradle plugin is tested against Gradle 7.1–8.6. Using a Gradle version outside that range isn't guaranteed to work — check current compatibility in the [official docs](https://docs.gatling.io/reference/extensions/build-tools/gradle-plugin/) before starting a new project.

### C.1 Fastest Start — Bootstrap Script

```bash
curl -sL https://raw.githubusercontent.com/lkishalmi/gradle-gatling-plugin/master/bootstrap.sh | \
  bash -s ~/sample-gradle-gatling && \
  cd ~/sample-gradle-gatling && ./gradlew gatlingRun
```

Or clone one of Gatling's official Gradle demo repositories (linked in the [References](#references) section) and open it directly in IntelliJ.

### C.2 Manual Setup — Add the Plugin to an Existing Project

**Using the Plugins DSL (`build.gradle`):**
```groovy
plugins {
    id 'io.gatling.gradle' version 'REPLACE_WITH_LATEST_VERSION'
}
```

**Kotlin DSL (`build.gradle.kts`):**
```kotlin
plugins {
    id("io.gatling.gradle") version "REPLACE_WITH_LATEST_VERSION"
}
```

The plugin creates a dedicated `gatling` source set for your simulations and configuration. By default:
```groovy
sourceSets {
    gatling {
        // simulations live here, e.g. src/gatling/java or src/gatling/scala
    }
}
```

Custom directories can be added if needed:
```groovy
sourceSets {
    gatling {
        scala.srcDir "extraSimulationsFolder"
    }
}
```

> **Multi-project builds:** only apply the Gatling plugin to the subproject(s) that actually contain simulations. That subproject can still depend on other subprojects.

### C.3 Run

```bash
./gradlew gatlingRun                                            # macOS/Linux — run interactively
gradlew.bat gatlingRun                                           # Windows
./gradlew gatlingRun-simulations.LoginSimulation                 # run a specific simulation (class-based task name)
```

➡️ Continue to [Using the Gatling Recorder](#using-the-gatling-recorder-all-paths). *(The Recorder is not exposed as a dedicated Gradle task; most teams run it via the standalone bundle or Maven and then copy the generated simulation into their Gradle project's source set.)*

---

## Path D — Apache Ant (Unofficial)

> ⚠️ **Important:** Gatling does **not** provide an official Ant plugin or maintained Ant integration. If you're starting a new project, Maven or Gradle is strongly recommended. This path exists for teams that must keep an existing Ant-based build pipeline and need to bolt Gatling onto it.

The general approach is to use Ant's `<exec>` task to invoke the Gatling CLI (from a standalone bundle or a manually assembled classpath), rather than trying to run Gatling *inside* the Ant JVM.

### D.1 Prepare Gatling

Download and extract the standalone bundle as described in [Path A](#path-a--standalone-bundle-fastest-start), or assemble the Gatling JARs manually via a local Maven repository (`mvn dependency:copy-dependencies`) if you want tighter version control.

### D.2 Call Gatling from `build.xml`

```xml
<project name="gatling-load-test" default="run-gatling" basedir=".">

  <property name="gatling.home" value="C:/Tools/Gatling" />

  <target name="run-gatling" description="Run Gatling simulation via the bundle">
    <exec executable="cmd" osfamily="windows" failonerror="true" dir="${gatling.home}">
      <arg value="/c" />
      <arg value="mvnw.cmd gatling:test -Dgatling.simulationClass=simulations.LoginSimulation" />
    </exec>
    <exec executable="${gatling.home}/mvnw" osfamily="unix" failonerror="true" dir="${gatling.home}">
      <arg value="gatling:test" />
      <arg value="-Dgatling.simulationClass=simulations.LoginSimulation" />
    </exec>
  </target>

</project>
```

Run it with:
```bash
ant run-gatling
```

### D.3 Notes on This Approach

- Ant isn't aware of Gatling's build lifecycle — you're just shelling out to Maven (or a Java classpath command) and letting Ant treat the exit code as pass/fail.
- Nested `<arg>` elements (not a single string) are the safer way to pass parameters, since Ant handles quoting/spacing more predictably that way.
- If you'd rather avoid the Maven Wrapper entirely, you can invoke the Gatling engine class directly via Ant's `<java>` task, pointing `classpath` at the bundle's `lib` folder — but this requires manually keeping the classpath in sync with each Gatling upgrade, which is exactly the maintenance burden Maven/Gradle plugins exist to avoid.
- For CI pipelines, treat `ant run-gatling`'s exit code as your pass/fail gate, same as you would for `mvn gatling:test`.

➡️ Continue to [Using the Gatling Recorder](#using-the-gatling-recorder-all-paths).

---

## Using the Gatling Recorder (All Paths)

The **Recorder** is a GUI tool that watches your browser traffic (via a local proxy) and auto-generates a simulation script from what you click through. It's most commonly launched via the standalone bundle or Maven, then the generated file is copied into whichever build tool's project you're actually using.

### 1. Launch the Recorder

| Path | Command |
|---|---|
| Bundle | `mvnw.cmd gatling:recorder` (Windows) / `./mvnw gatling:recorder` (macOS/Linux) |
| Maven | `mvn gatling:recorder` |
| Gradle | Not exposed as a task — launch via bundle/Maven instead, then copy the file into your `gatling` source set |
| Ant | Not applicable — launch via bundle/Maven instead |

### 2. Configure the Recorder

| Setting | Value |
|---|---|
| Recorder Mode | HTTP Proxy |
| Simulation Name | `LoginSimulation` (or any descriptive name) |
| Package | `simulations` |
| Language | Java (or Scala/Kotlin if using Maven/Gradle) |
| HTTP/HTTPS Proxy Port | `8000` (or any free port) |

Click **Start**.

### 3. Configure Your Browser Proxy

| Setting | Value |
|---|---|
| Host | `localhost` |
| Port | `8000` |

- **Chrome/Edge**: Settings → System → Open your computer's proxy settings, or use a proxy extension like *SwitchyOmega*.
- **Firefox**: Settings → Network Settings → Manual proxy configuration.
- **macOS system-wide**: System Settings → Network → your connection → Details → Proxies.

> **HTTPS tip:** you may need to trust the Recorder's certificate to avoid warnings when recording HTTPS traffic — see the [Recorder documentation](https://docs.gatling.io/reference/script/recorder/).

### 4. Record & Stop

1. Perform the user journey you want to test.
2. Click **Stop** in the Recorder window.
   > ⚠️ Closing the Recorder without clicking Stop means no simulation file is generated.
3. The simulation is written to `src/test/java/simulations/LoginSimulation.java` (bundle/Maven) or your configured `gatling` source set (Gradle).

---

## Running Simulations & Reading Reports

### Run

| Path | Command |
|---|---|
| Bundle (Windows) | `mvnw.cmd gatling:test` |
| Bundle (macOS/Linux) | `./mvnw gatling:test` |
| Maven | `mvn gatling:test` |
| Gradle | `./gradlew gatlingRun` |
| Ant | `ant run-gatling` |

If multiple simulations exist, you'll be prompted:
```
Choose a simulation

1) LoginSimulation
2) DemoSimulation

Select Simulation:
```

**Skip the prompt** (useful for CI/CD) by specifying the class directly, e.g.:
```bash
mvn gatling:test -Dgatling.simulationClass=simulations.LoginSimulation
```

### View the HTML Report

After the run finishes, Gatling generates a report at:
```
target/gatling/LoginSimulation-<timestamp>/index.html      # Maven / bundle
build/reports/gatling/LoginSimulation-<timestamp>/index.html # Gradle
```

Open `index.html` in a browser. It includes:
- Requests per second
- Response time distribution & percentiles (p50, p95, p99…)
- Error breakdown by request
- Active users over time
- Interactive charts and per-request statistics

---

## Command Cheat Sheet by Build Tool

| Purpose | Bundle (Win) | Bundle (macOS/Linux) | Maven | Gradle | Ant |
|---|---|---|---|---|---|
| Launch Recorder | `mvnw.cmd gatling:recorder` | `./mvnw gatling:recorder` | `mvn gatling:recorder` | — (use bundle/Maven) | — (use bundle/Maven) |
| Run Simulation | `mvnw.cmd gatling:test` | `./mvnw gatling:test` | `mvn gatling:test` | `./gradlew gatlingRun` | `ant run-gatling` |
| Run Specific Simulation | `mvnw.cmd gatling:test -Dgatling.simulationClass=...` | `./mvnw gatling:test -Dgatling.simulationClass=...` | `mvn gatling:test -Dgatling.simulationClass=...` | `./gradlew gatlingRun-<SimName>` | pass via `-Dgatling.simulationClass=` inside the exec target |
| Clean | `mvnw.cmd clean` | `./mvnw clean` | `mvn clean` | `./gradlew clean` | `ant clean` (if defined) |
| Compile | `mvnw.cmd compile` | `./mvnw compile` | `mvn compile` | `./gradlew compileGatlingJava` | `ant compile` (if defined) |
| Package | `mvnw.cmd package` | `./mvnw package` | `mvn package` | `./gradlew gatlingClasses` | n/a |
| Tool Version | `mvnw.cmd --version` | `./mvnw --version` | `mvn -version` | `./gradlew --version` | `ant -version` |

---

## Common Errors & Solutions

### 1. `'java' is not recognized` / `command not found: java`
**Cause:** Java isn't installed, or `JAVA_HOME`/`PATH` isn't set.
**Fix:** Reinstall the JDK, ensuring PATH is configured, then verify with `java -version`.

### 2. `'mvnw.cmd' is not recognized` / `./mvnw: Permission denied`
**Cause (Windows):** You're not inside the Gatling project folder — `cd` into it.
**Cause (macOS/Linux):** The wrapper script isn't executable — run `chmod +x mvnw` then retry.

### 3. Recorder opens but captures nothing
**Cause:** Browser proxy isn't pointed at the Recorder.
**Fix:** Confirm proxy settings match `localhost:8000` (or your configured port).

### 4. Simulation file not generated
**Cause:** The Recorder window was closed before clicking **Stop**.
**Fix:** Always click **Stop** before closing the Recorder.

### 5. HTML report not generated
**Cause:** The simulation failed to compile or errored at runtime.
**Fix:** Review console output for the actual error, fix it, and rerun the test command for your build tool.

### 6. `Unsupported major.minor version` error
**Cause:** `JAVA_HOME` points to an unsupported or mismatched JDK.
**Fix:** Confirm a 64-bit OpenJDK LTS (11–25) is installed and `JAVA_HOME` points to it.

### 7. Gradle: `Inconsistent JVM-target compatibility` or Kotlin/JDK mismatch errors
**Cause:** Your installed JDK is newer than what your Kotlin version supports.
**Fix:** Pin the bytecode target explicitly in `build.gradle.kts`:
```kotlin
tasks.withType(JavaCompile::class) {
    options.release.set(21)
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
```

### 8. Ant: `Execute failed: java.io.IOException: Cannot run program`
**Cause:** The `executable` path in your `<exec>` task is wrong for your OS, or you forgot the `osfamily` attribute when supporting both Windows and Unix targets.
**Fix:** Double-check the path to `mvnw`/`mvnw.cmd`, and keep separate `<exec>` blocks per `osfamily` as shown in [Path D](#path-d--apache-ant-unofficial).

### 9. Certificate warnings while recording HTTPS traffic
**Cause:** Your browser doesn't trust the Recorder's local certificate.
**Fix:** Follow the certificate-trust steps in the [Recorder documentation](https://docs.gatling.io/reference/script/recorder/).

---

## Tips for Advanced Users

- **Prefer Maven or Gradle over the standalone bundle for real projects.** The bundle is meant for quick starts and firewall-restricted environments; it only supports Java and isn't as IDE-friendly for larger codebases.
- **Avoid the Ant path for new projects.** Since there's no official plugin, you lose version-aware dependency management and take on manual classpath/version maintenance. Only use it to keep an existing Ant pipeline alive while you plan a migration.
- **Version control only your simulations**, not the whole bundle — `src/`, `pom.xml` (or `build.gradle`), and the wrapper files are enough.
- **Parameterize tests** using CSV feeders (`csv("users.csv").random`) instead of hardcoding data captured by the Recorder.
- **Use assertions** (e.g., `global().responseTime().max().lt(2000)`) to make simulations function as automated pass/fail gates in CI/CD — this works the same way whether you trigger the run from Maven, Gradle, or an Ant `<exec>` wrapper.
- **Scale beyond one machine** with Gatling Enterprise Edition for distributed load generation, historical dashboards, and team collaboration.
- **Multi-module Gradle builds:** only apply the `io.gatling.gradle` plugin to the subproject(s) containing simulations — it can still depend on other subprojects for shared code.

---

## References

- Gatling Community Edition download: https://gatling.io/download-gatling-community-edition
- Gatling Documentation home: https://docs.gatling.io/
- Installation reference (bundle, build tool, package manager): https://docs.gatling.io/reference/install/oss/
- Maven Plugin reference: https://docs.gatling.io (search "Maven Plugin Reference")
- Gradle Plugin reference: https://docs.gatling.io/reference/extensions/build-tools/gradle-plugin/
- Gatling Gradle Plugin source: https://github.com/gatling/gatling-gradle-plugin
- Official Java/Maven demo project: https://github.com/gatling/gatling-maven-plugin-demo-java
- Recorder documentation: https://docs.gatling.io/reference/script/recorder/
- Apache Ant manual (for `<exec>` task reference): https://ant.apache.org/manual/
