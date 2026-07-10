# Gatling Installation & Recorder Guide (Windows, macOS, Linux)

A complete, beginner-friendly walkthrough for installing **Gatling Community Edition**, extracting the bundle, using the **Gatling Recorder** to auto-generate test scripts, running simulations, and reading the HTML report — with notes for more advanced users along the way.

> **New to load testing?** Read the [What is Gatling?](#what-is-gatling) section first.
> **Already comfortable with Java/Maven?** Skip to [Step 1 — Install Prerequisites](#step-1--install-prerequisites).

---

## Table of Contents

1. [What is Gatling?](#what-is-gatling)
2. [Two Ways to Use Gatling](#two-ways-to-use-gatling)
3. [Step 1 — Install Prerequisites](#step-1--install-prerequisites)
4. [Step 2 — Download the Gatling Bundle](#step-2--download-the-gatling-bundle)
5. [Step 3 — Extract the Bundle](#step-3--extract-the-bundle)
6. [Step 4 — Understand the Folder Structure](#step-4--understand-the-folder-structure)
7. [Step 5 — Launch the Gatling Recorder](#step-5--launch-the-gatling-recorder)
8. [Step 6 — Configure the Recorder](#step-6--configure-the-recorder)
9. [Step 7 — Configure Your Browser Proxy](#step-7--configure-your-browser-proxy)
10. [Step 8 — Record & Stop](#step-8--record--stop)
11. [Step 9 — Run the Simulation](#step-9--run-the-simulation)
12. [Step 10 — View the HTML Report](#step-10--view-the-html-report)
13. [Complete Workflow Diagram](#complete-workflow-diagram)
14. [Useful Commands Cheat Sheet](#useful-commands-cheat-sheet)
15. [Common Errors & Solutions](#common-errors--solutions)
16. [Tips for Advanced Users](#tips-for-advanced-users)
17. [References](#references)

---

## What is Gatling?

**Gatling** is an open-source load and performance testing tool. It lets you simulate hundreds, thousands, or millions of virtual users hitting your application, and then gives you detailed reports on response times, throughput, and errors.

Unlike thread-per-user tools (e.g., JMeter), Gatling uses an asynchronous, non-blocking architecture, so a single machine can simulate a very large number of concurrent users efficiently.

There are two editions:

| Edition | Best for |
|---|---|
| **Community Edition (Free, Open Source)** | Individuals, small teams, local/CI testing |
| **Enterprise Edition** | Distributed cloud runs, dashboards, team collaboration, integrations |

This guide covers the **Community Edition**, run locally.

---

## Two Ways to Use Gatling

Gatling can be set up in two ways. Understanding the difference will save you confusion later:

| Method | Description | Best for |
|---|---|---|
| **Maven / Gradle plugin** | Add Gatling as a dependency in an existing Java/Kotlin/Scala project | Developers already using Maven or Gradle, teams pushing scripts to Git |
| **Standalone Bundle (ZIP)** | A self-contained ZIP with everything pre-configured, including a bundled Maven Wrapper | Beginners, QA engineers without a dev setup, or anyone behind a restrictive corporate firewall with no Maven access |

> **Good to know:** Since Gatling 3.11, the standalone bundle is itself built on a Maven Wrapper (`mvnw` / `mvnw.cmd`) rather than being a fully separate tool. This means the "standalone" bundle only supports **Java** simulations — not Scala or Kotlin. If you need Scala or Kotlin, use the Maven or Gradle plugin route instead.

This guide follows the **Standalone Bundle** route, since it's the fastest way for beginners to get started.

---

## Step 1 — Install Prerequisites

### 1.1 Install a Java JDK

Gatling runs on the JVM and requires a **64-bit OpenJDK LTS version** (11 through 25). JDK 17 or 21 LTS is recommended. 32-bit systems and OpenJ9 are **not** supported.

<details>
<summary><strong>Windows</strong></summary>

1. Download an OpenJDK build (e.g., [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/)).
2. Run the installer, making sure "Set JAVA_HOME" and "Add to PATH" are checked.
3. Verify in Command Prompt or PowerShell:
   ```cmd
   java -version
   ```
</details>

<details>
<summary><strong>macOS</strong></summary>

Using [Homebrew](https://brew.sh/) is the easiest route:
```bash
brew install openjdk@17
```
Then link it and confirm your shell can find it:
```bash
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
java -version
```
</details>

<details>
<summary><strong>Linux</strong></summary>

Debian/Ubuntu:
```bash
sudo apt update
sudo apt install openjdk-17-jdk
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

**Expected output (version number may differ):**
```text
openjdk version "17.0.x"
```

### 1.2 A Terminal

- **Windows**: Command Prompt, PowerShell, or Git Bash
- **macOS**: Terminal or iTerm2
- **Linux**: Your default shell (bash/zsh)

### 1.3 (Optional but recommended) An IDE

While you can edit Gatling simulations in any text editor, an IDE gives you autocompletion and error-checking. **IntelliJ IDEA Community Edition** (free) works out of the box with Java, Maven, and Gradle. VS Code is also a popular lightweight alternative.

### 1.4 Internet Connection

Needed to download Gatling and (the first time) any Maven Wrapper dependencies.

---

## Step 2 — Download the Gatling Bundle

Download the latest **Gatling Community Edition** standalone bundle from the official site:

- https://gatling.io/download-gatling-community-edition
- Alternative reference: https://docs.gatling.io/reference/install/oss/

Always download the **latest stable release** — avoid milestone/preview ("M") builds, which are undocumented and intended for internal or Enterprise use only.

> **Tip:** Save the ZIP somewhere simple, like `Downloads`, so the extraction paths in this guide match up.

---

## Step 3 — Extract the Bundle

> ⚠️ **Windows users:** the built-in Windows Explorer "Extract All" tool can fail or produce broken paths with this bundle. Use one of the methods below instead — **7-Zip** or `tar` are the safest choices.

Create a destination folder first:

**Command Prompt (Windows)**
```cmd
mkdir C:\Tools\Gatling
```
**macOS / Linux**
```bash
mkdir -p ~/Tools/Gatling
```

> **Avoid installing under `C:\Program Files`** — Windows permission restrictions there can cause issues with Gatling's scripts and file writes.

Choose whichever extraction method fits your workflow:

### Option 1 — PowerShell `Expand-Archive` (Windows)
```powershell
powershell -Command "Expand-Archive -Path '<zip-file>' -DestinationPath '<destination-folder>' -Force"
```
Example:
```powershell
powershell -Command "Expand-Archive -Path 'C:\Downloads\gatling.zip' -DestinationPath 'C:\Tools\Gatling' -Force"
```

### Option 2 — `tar` (built into Windows 10/11, macOS, and Linux)
```bash
tar -xf <zip-file> -C <destination-folder>
```
Example:
```cmd
tar -xf C:\Downloads\gatling.zip -C C:\Tools\Gatling
```

### Option 3 — `unzip` (macOS Terminal, Linux, or Git Bash on Windows)
```bash
unzip <zip-file> -d <destination-folder>
```
Example:
```bash
unzip ~/Downloads/gatling.zip -d ~/Tools/Gatling
```

### Option 4 — 7-Zip Command Line (recommended on Windows)
```cmd
7z x <zip-file> -o<destination-folder>
```
Example:
```cmd
7z x C:\Downloads\gatling.zip -oC:\Tools\Gatling
```
*(No space after `-o`.)*

### Option 5 — `cd` into the folder first, then extract
**Command Prompt**
```cmd
cd C:\Tools\Gatling
tar -xf C:\Downloads\gatling.zip
```
**macOS / Linux**
```bash
cd ~/Tools/Gatling
unzip ~/Downloads/gatling.zip
```

### Verify the Extraction
**Windows**
```cmd
dir C:\Tools\Gatling
```
**macOS / Linux**
```bash
ls -la ~/Tools/Gatling
```

---

## Step 4 — Understand the Folder Structure

Navigate into the extracted folder:

```bash
cd C:\Tools\Gatling      # Windows
cd ~/Tools/Gatling       # macOS/Linux
```

You'll see a Maven-project-style layout:

```
gatling
│
├── mvnw.cmd        # Maven Wrapper launcher (Windows)
├── mvnw            # Maven Wrapper launcher (macOS/Linux)
├── pom.xml         # Project configuration
├── src
│   └── test
│       └── java
│           └── simulations   # Your simulation scripts live here
├── target           # Compiled output & HTML reports appear here
└── ...
```

Because it's Maven-based, you can open this folder directly in IntelliJ IDEA or another IDE, and it will be recognized as a normal Maven project — no manual configuration needed.

---

## Step 5 — Launch the Gatling Recorder

The **Recorder** is a GUI tool that watches your browser traffic (via a local proxy) and auto-generates a Java simulation script from what you click through.

**Windows**
```cmd
mvnw.cmd gatling:recorder
```

**macOS / Linux**
```bash
./mvnw gatling:recorder
```

The first run may take a little longer as the Maven Wrapper downloads dependencies. A Recorder window will then open.

---

## Step 6 — Configure the Recorder

In the Recorder window, set:

| Setting | Value |
|---|---|
| Recorder Mode | HTTP Proxy |
| Simulation Name | `LoginSimulation` (or any name describing your test) |
| Package | `simulations` |
| Language | Java |
| HTTP/HTTPS Proxy Port | `8000` (or any free port) |

Click **Start**.

---

## Step 7 — Configure Your Browser Proxy

Point your browser at the same proxy the Recorder is listening on:

| Setting | Value |
|---|---|
| Host | `localhost` |
| Port | `8000` |

**Where to set this:**
- **Chrome/Edge (all OSes)**: Settings → System → Open your computer's proxy settings, or use a proxy extension like *SwitchyOmega* for quick toggling.
- **Firefox**: Settings → Network Settings → Manual proxy configuration.
- **macOS system-wide**: System Settings → Network → your connection → Details → Proxies.

> **HTTPS tip:** If you're recording HTTPS traffic, you may need to trust the Recorder's certificate in your browser/OS to avoid certificate warnings. See the [Recorder documentation](https://docs.gatling.io/reference/script/recorder/) for details.

Now browse your application normally — every HTTP(S) request you make is captured by the Recorder.

---

## Step 8 — Record & Stop

1. Perform the user journey you want to test (e.g., log in, search, add to cart).
2. Click **Stop** in the Recorder window when you're done.

   > ⚠️ If you close the Recorder without clicking **Stop**, no simulation file will be generated.

3. Gatling automatically writes a Java simulation file to:
   ```
   src/test/java/simulations/LoginSimulation.java
   ```

You can now open this file in your editor and refine it — add assertions, parameterize data, add think-time/pauses, or combine it with other scenarios.

---

## Step 9 — Run the Simulation

**Windows**
```cmd
mvnw.cmd gatling:test
```

**macOS / Linux**
```bash
./mvnw gatling:test
```

If you have multiple simulations, Gatling will prompt you to choose one:

```
Choose a simulation

1) LoginSimulation
2) DemoSimulation

Select Simulation:
```

Type the number and press Enter.

**Advanced tip:** skip the interactive prompt and run a specific simulation directly — useful for CI/CD pipelines:
```bash
mvnw.cmd gatling:test -Dgatling.simulationClass=simulations.LoginSimulation
```

---

## Step 10 — View the HTML Report

After the run finishes, Gatling generates a full HTML report at:

```
target/gatling/LoginSimulation-<timestamp>/index.html
```

Open `index.html` in any browser. The report includes:

- Requests per second
- Response time distribution
- Percentiles (p50, p95, p99, etc.)
- Error breakdown by request
- Active users over time
- Interactive charts and detailed per-request statistics

This report is the core deliverable of a Gatling run — it's what you'll share with your team or attach to a performance test sign-off.

---

## Complete Workflow Diagram

```
Install Java (JDK 17/21 LTS)
        │
        ▼
Download Gatling Bundle
        │
        ▼
Extract ZIP (7-Zip / tar / unzip)
        │
        ▼
Open Gatling Folder (Maven project)
        │
        ▼
Launch Recorder
 (mvnw gatling:recorder)
        │
        ▼
Configure Browser Proxy
        │
        ▼
Record User Actions
        │
        ▼
Click Stop → Simulation Generated
        │
        ▼
Run Simulation
 (mvnw gatling:test)
        │
        ▼
HTML Report Generated
```

---

## Useful Commands Cheat Sheet

| Purpose | Windows | macOS/Linux |
|---|---|---|
| Start Recorder | `mvnw.cmd gatling:recorder` | `./mvnw gatling:recorder` |
| Run Simulation | `mvnw.cmd gatling:test` | `./mvnw gatling:test` |
| Run a Specific Simulation | `mvnw.cmd gatling:test -Dgatling.simulationClass=simulations.LoginSimulation` | `./mvnw gatling:test -Dgatling.simulationClass=simulations.LoginSimulation` |
| Clean Project | `mvnw.cmd clean` | `./mvnw clean` |
| Compile Project | `mvnw.cmd compile` | `./mvnw compile` |
| Package Project | `mvnw.cmd package` | `./mvnw package` |
| Run All Tests | `mvnw.cmd test` | `./mvnw test` |
| Show Maven Wrapper Version | `mvnw.cmd --version` | `./mvnw --version` |

---

## Common Errors & Solutions

### 1. `'java' is not recognized as an internal or external command`
**Cause:** Java isn't installed, or `JAVA_HOME`/`PATH` isn't set correctly.
**Fix:** Reinstall the JDK making sure to check "Add to PATH", then verify:
```cmd
java -version
```

### 2. `'mvnw.cmd' is not recognized` / `./mvnw: Permission denied`
**Cause (Windows):** You're not inside the Gatling project folder.
**Fix:**
```cmd
cd C:\Tools\Gatling
```
**Cause (macOS/Linux):** The wrapper script isn't executable.
**Fix:**
```bash
chmod +x mvnw
./mvnw gatling:recorder
```

### 3. Recorder opens but captures nothing
**Cause:** Browser proxy isn't pointed at the Recorder.
**Fix:** Double-check your browser/OS proxy settings match `localhost:8000` (or whichever port you set).

### 4. Simulation file not generated
**Cause:** The Recorder window was closed before clicking **Stop**.
**Fix:** Always click **Stop** in the Recorder before closing it.

### 5. HTML report not generated
**Cause:** The simulation failed to compile or errored at runtime.
**Fix:** Review the console output for stack traces, fix the reported issue, and rerun:
```bash
mvnw.cmd gatling:test   # Windows
./mvnw gatling:test     # macOS/Linux
```

### 6. `Unsupported major.minor version` error
**Cause:** `JAVA_HOME` points to an unsupported or mismatched JDK version.
**Fix:** Confirm you're on a 64-bit OpenJDK LTS release (11–25) and that `JAVA_HOME` points to it.

### 7. Certificate warnings while recording HTTPS traffic
**Cause:** Your browser doesn't trust the Recorder's local certificate.
**Fix:** Follow the certificate-trust steps in the [Recorder documentation](https://docs.gatling.io/reference/script/recorder/), or temporarily record over HTTP-only flows for practice.

---

## Tips for Advanced Users

- **Migrate off the standalone bundle for real projects.** The bundle is meant for quick starts and firewall-restricted environments. For serious/CI usage, add Gatling as a Maven or Gradle dependency in your own project — it's lighter, integrates cleanly with IDEs, and supports Scala/Kotlin if you need them.
- **Version control your simulations**, not the whole bundle. Since the bundle follows a standard Maven layout, you can `git init` inside it and just track `src/`, `pom.xml`, and the wrapper files.
- **Parameterize your tests** using CSV feeders (`csv("users.csv").random`) instead of hardcoding credentials/data from the recording.
- **Use assertions** (`.assertions(global().responseTime().max().lt(2000))`) to turn your simulations into automated pass/fail gates for CI/CD.
- **Scale beyond one machine** with Gatling Enterprise Edition if you need distributed load generation, historical trend dashboards, or team collaboration features.
- **Explore the Gatling MCP Server** if you use MCP-compatible AI clients and want to connect them to a Gatling Enterprise account for AI-assisted test authoring.

---

## References

- Gatling Community Edition download: https://gatling.io/download-gatling-community-edition
- Gatling Documentation home: https://docs.gatling.io/
- Installation reference (bundle, build tool, package manager): https://docs.gatling.io/reference/install/oss/
- Recorder documentation: https://docs.gatling.io/reference/script/recorder/
