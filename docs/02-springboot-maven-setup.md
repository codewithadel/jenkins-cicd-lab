# Lab 02: Spring Boot Application Setup and Maven Configuration

## Overview

In this lab, I set up a Java Spring Boot REST API as the application we'll use throughout our Jenkins CI/CD project.

The objective was to prepare a working application that Jenkins can automatically build, test, package, and eventually deploy to Kubernetes.

Before introducing Jenkins automation, I wanted to make sure the application could build successfully in my local development environment. This is an important step because it helps us separate application-level issues from Jenkins pipeline configuration problems.

During the setup, I encountered a few challenges with Java, Maven, and the Maven Wrapper. This documentation walks through the setup process, explains the errors, and shows how I resolved them.

## 1. Understanding the Application Structure

The Spring Boot application is stored in the `app/` directory of my GitHub repository.

```text
jenkins-cicd-lab/
├── app/
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── cicd/
│   └── jenkins/
│       └── Jenkinsfile
├── docker/
├── kubernetes/
├── docs/
└── README.md
```

### What each component does

| File or Directory | Purpose |
|---|---|
| `src/main/java/` | Contains the Spring Boot application source code |
| `src/main/resources/` | Stores application configuration |
| `src/test/java/` | Contains automated application tests |
| `pom.xml` | Defines Maven dependencies, plugins, and build configuration |
| `mvnw` | Maven Wrapper script for macOS and Linux |
| `mvnw.cmd` | Maven Wrapper script for Windows |
| `.mvn/wrapper/` | Stores Maven Wrapper configuration |
| `Jenkinsfile` | Defines the Jenkins CI/CD pipeline |
| `docker/` | Contains Docker configuration |
| `kubernetes/` | Contains Kubernetes deployment manifests |

The goal is to keep the application code separate from the infrastructure and CI/CD configurations.

This structure also makes it easier to expand the project as we introduce additional DevOps tools.

---

## 2. Configuring Java 21

Spring Boot applications run on the Java platform, so the first requirement was to install and configure a compatible Java Development Kit (JDK).

For this project, I chose **Java 21**, a Long-Term Support (LTS) release.

Java 21 provides the compiler, runtime, and development tools needed to build and execute the application.

### Verify Java

```bash
java -version
```

To verify the Java compiler:

```bash
javac -version
```

### Issue encountered: OpenJDK installation failed

Initially, I attempted to install Java 21 through Homebrew:

```bash
brew install openjdk@21
```

The installation failed with the following error:

```text
openjdk@21: A full installation of Xcode.app
is required to compile this software.

Installing just the Command Line Tools
is not sufficient.
```

### Why this happened

Homebrew was attempting to compile OpenJDK from source instead of installing a precompiled binary.

Building OpenJDK from source requires additional development tools, including the full Xcode installation on macOS.

However, compiling Java from source was unnecessary for this project.

### Solution: Use a prebuilt Java distribution

Instead, I used Eclipse Temurin, a prebuilt OpenJDK distribution.

```bash
brew install --cask temurin@21
```

After installation, Java 21 can be selected using:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

Verify:

```bash
java -version
echo "$JAVA_HOME"
```

**Key takeaway:** When setting up Java for application development, using a supported prebuilt JDK is usually simpler than compiling OpenJDK from source.

---

## 3. Installing Apache Maven

After configuring Java, the next step was setting up Maven.

### What is Maven?

Apache Maven is a build automation and dependency management tool commonly used for Java applications.

Instead of manually downloading Java libraries, compiling source files, and packaging the application, Maven automates these tasks.

Maven reads the project's `pom.xml` file to determine:

- Which dependencies the application needs.
- Which Java version to use.
- How the application should be compiled.
- Which tests should run.
- How the final application artifact should be packaged.

### Initial issue: Maven command not found

When I attempted to run:

```bash
mvn clean verify
```

The terminal returned:

```text
zsh: command not found: mvn
```

This indicated that Maven was not installed or its executable was not available in the system's `PATH`.

### Solution: Install Maven using the official binary

Since Homebrew installation was unsuccessful in my environment, I used the official Apache Maven binary distribution.

Download Maven from:

https://maven.apache.org/download.cgi

I downloaded the Maven 3 binary archive and extracted it into a local tools directory.

```bash
mkdir -p "$HOME/tools"
```

Extract the downloaded archive:

```bash
tar -xzf ~/Downloads/apache-maven-3.*-bin.tar.gz \
  -C "$HOME/tools"
```

### Configure Maven environment variables

Maven must be available in the system's executable search path.

I configured the Maven installation directory using the `MAVEN_HOME` environment variable.

For example:

```bash
export MAVEN_HOME="$HOME/tools/apache-maven-3.9.x"
export PATH="$MAVEN_HOME/bin:$PATH"
```

Replace `3.9.x` with the actual installed Maven directory.

To make these settings persistent, add them to `~/.zshrc`.

```bash
nano ~/.zshrc
```

Add:

```bash
export MAVEN_HOME="$HOME/tools/apache-maven-3.9.x"
export PATH="$MAVEN_HOME/bin:$PATH"
```

Reload the shell configuration:

```bash
source ~/.zshrc
```

### Verify Maven installation

```bash
mvn -version
```

A successful output should display the installed Maven version, Maven home directory, and Java runtime.

**Key takeaway:** Installing software is only part of the setup. The shell must also know where to find the executable through the `PATH` environment variable.

---

## 4. Understanding the Maven Wrapper

After Maven was installed, I needed to make sure the project could use a consistent Maven version across different environments.

This is where the **Maven Wrapper** comes in.

### What is the Maven Wrapper?

The Maven Wrapper allows developers and CI/CD systems to run Maven using a project-defined version.

Instead of depending entirely on a globally installed Maven version, the project includes wrapper scripts that download and use the configured Maven distribution when necessary.

There are two common ways to run Maven:

**Using a globally installed Maven executable:**

```bash
mvn clean verify
```

**Using the Maven Wrapper:**

```bash
./mvnw clean verify
```

Both execute Maven goals, but the wrapper uses the Maven distribution specified by the project.

### Issue encountered: Maven Wrapper missing

Initially, when I tried:

```bash
./mvnw clean verify
```

The command failed because the Maven Wrapper files were missing from the application directory.

I checked the project structure using:

```bash
ls -la
```

The `src/` directory existed, but the expected `.mvn/` wrapper directory was missing.

### Solution: Generate the Maven Wrapper

Once Maven was installed globally, I generated the wrapper files.

From the application directory:

```bash
cd app
mvn wrapper:wrapper
```

This generated the wrapper scripts and configuration files.

The resulting structure included:

```text
app/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
└── pom.xml
```

On macOS, I made the wrapper executable:

```bash
chmod +x mvnw
```

Then verified it:

```bash
./mvnw -version
```

### Why use `./mvnw` instead of `mvnw`?

On Linux and macOS, the current directory is generally not included in the system's `PATH`.

Running:

```bash
mvnw
```

asks the shell to search for the command in directories listed in `PATH`.

Running:

```bash
./mvnw
```

explicitly tells the shell to execute the `mvnw` script in the current directory.

**Key takeaway:** The Maven Wrapper makes the project more portable and reduces differences between developer machines and CI/CD build environments.

---

## 5. Building the Spring Boot Application

With Java and Maven configured, the next step is to build and validate the application.

Navigate to the application directory:

```bash
cd app
```

Run:

```bash
./mvnw clean verify
```

### Understanding the command

**`clean`**

Removes files generated by previous builds, including the `target/` directory.

This helps prevent old build artifacts from affecting the current build.

**`verify`**

Runs the Maven lifecycle through the verification phase.

Depending on the project's configuration, this includes compiling the application, running tests, packaging the JAR, and performing additional verification checks.

### Expected result

A successful Maven build should end with:

```text
[INFO] BUILD SUCCESS
```

The packaged application is normally available in the `target/` directory.

For example:

```text
app/target/
└── product-api-0.0.1-SNAPSHOT.jar
```

The actual filename depends on the artifact name and version configured in `pom.xml`.

### Why validate locally before Jenkins?

A local build confirms that the application and its dependencies can be compiled and tested independently of Jenkins.

If the application fails locally, I can troubleshoot the source code, dependencies, or Java configuration first.

If it builds locally but fails in Jenkins, I can focus on differences in the build environment, permissions, installed tools, or pipeline configuration.

---

## 6. Committing the Maven Wrapper to GitHub

After generating the Maven Wrapper, I committed the files to the Git repository.

From the repository root:

```bash
git add app/mvnw app/mvnw.cmd app/.mvn/
```

Commit:

```bash
git commit -m "build: Add Maven Wrapper for reproducible Spring Boot builds"
```

Push:

```bash
git push origin main
```

### Why commit the Maven Wrapper?

The Maven Wrapper is part of the application's build configuration.

By committing it to GitHub, anyone cloning the repository can use the same configured Maven version without manually installing Maven globally.

This is especially important for Jenkins.

Once Jenkins checks out the repository, the pipeline can execute:

```bash
cd app
./mvnw clean verify
```

The Jenkins build agent still requires a compatible Java installation and network access to download the Maven distribution and dependencies if they are not already cached.

---

## 7. How This Fits Into Our Jenkins CI/CD Pipeline

Now that the Spring Boot application and Maven Wrapper are configured, we can integrate the build process into Jenkins.

The workflow will be:

```text
Developer
    |
    | Push changes
    v
GitHub Repository
    |
    v
Jenkins SCM Checkout
    |
    v
Java 21 Build Environment
    |
    v
Maven Wrapper
    |
    +-- Resolve Dependencies
    |
    +-- Compile Application
    |
    +-- Run Automated Tests
    |
    +-- Package JAR
    |
    v
Build Artifact
    |
    v
Docker Image Build
    |
    v
Docker Hub
    |
    v
Kubernetes Deployment
```

The immediate next step is to configure Jenkins to execute the Maven build automatically whenever the pipeline is triggered.

Later, we'll extend the pipeline to publish Docker images and deploy the application to Kubernetes.

---

## 8. Troubleshooting Reference

| Error | Possible Cause | Solution |
|---|---|---|
| `mvn: command not found` | Maven missing or not in `PATH` | Install Maven and configure `PATH` |
| `./mvnw: no such file or directory` | Maven Wrapper missing or incorrect directory | Verify files or generate wrapper |
| `permission denied: ./mvnw` | Wrapper script not executable | Run `chmod +x mvnw` |
| `JAVA_HOME is not defined correctly` | Invalid Java environment configuration | Set `JAVA_HOME` to the installed JDK |
| `invalid target release: 21` | Maven is using an older JDK | Configure Maven to use Java 21 |
| Dependency download failure | Network, repository, or proxy issue | Check Maven logs and repository connectivity |
| Maven tests fail | Application or test failure | Review test output and Surefire reports |

---

## 9. Lessons Learned

This lab reinforced several important concepts.

**1. Understand your development environment.**

Before building an application, verify that the required tools are installed and configured correctly.

**2. Java and Maven serve different purposes.**

Java provides the compiler and runtime, while Maven manages the application build lifecycle and dependencies.

**3. Environment variables matter.**

Tools may be installed correctly but remain inaccessible if the system's `PATH` or `JAVA_HOME` is misconfigured.

**4. Build reproducibility is important.**

The Maven Wrapper helps maintain a consistent Maven version across local machines and CI/CD environments.

**5. Validate before automating.**

A successful local build is an important checkpoint before integrating an application into Jenkins.

## Conclusion

In this lab, I prepared the Spring Boot application for automated builds by configuring Java, installing Apache Maven, generating the Maven Wrapper, and committing the build configuration to GitHub.

The initial installation challenges were also useful troubleshooting exercises because they helped me understand how Java, Maven, environment variables, and project build scripts work together.

With the application build environment prepared, the next phase is to integrate the Maven build and automated tests into the Jenkins pipeline.

**Next Lab: Jenkins Pipeline Integration — Automating Spring Boot Builds and Tests Using GitHub SCM.**