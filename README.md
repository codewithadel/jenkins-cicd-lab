# Jenkins CI/CD Lab — Spring Boot Product API

An original, hands-on Java 21 Spring Boot application for learning **GitHub → Jenkins → Maven → Docker → Kubernetes**. The first Jenkins pipeline implements working source checkout, Maven build and tests, and JAR archiving. Docker and Kubernetes configurations are included for the next labs; they are **not yet executed by Jenkins**.

## Repository layout

```text
app/                   Spring Boot application, Maven project, JUnit tests
cicd/jenkins/Jenkinsfile  CI pipeline as code
docker/Dockerfile       Runtime image (built after Maven packaging)
kubernetes/            Deployment and Service manifests
docs/                  Future lab walkthroughs
```

## API endpoints

| Method | Path | Response |
|---|---|---|
| GET | `/api/health` | `{"status":"UP"}` |
| GET | `/api/version` | Application name and version |
| GET | `/api/products` | Three sample products |
| GET | `/actuator/health` | Spring Boot health status |

The product catalog is in memory and resets with each restart. There is no database in this introductory version.

## Prerequisites

- JDK 21, Maven 3.9+, Git
- Jenkins agent with JDK 21 and Maven available on `PATH`
- Jenkins Git, Pipeline, JUnit, and Artifact Archiver functionality
- Docker and Minikube for subsequent labs

## Run locally

```bash
cd app
mvn clean verify
mvn spring-boot:run
```

In another terminal:

```bash
curl -i http://localhost:8080/api/health
curl -s http://localhost:8080/api/version
curl -s http://localhost:8080/api/products
```

## Jenkins: Pipeline script from SCM

1. Push this project to your own GitHub repository.
2. Create a Jenkins **Pipeline** job.
3. Choose **Pipeline script from SCM**, SCM **Git**.
4. Set repository URL to `https://github.com/codewithadel/jenkins-cicd-lab.git`.
5. Set branch to `*/main` and script path to `cicd/jenkins/Jenkinsfile`.
6. Save and select **Build Now**.
7. Verify the checkout, Maven build/test, and JAR archive stages. The pipeline publishes JUnit results, even when tests fail.

Public repositories do not require GitHub credentials for HTTPS clone. Jenkins must still have Git and Maven installed on the agent.

## Docker: next lab

After a successful Maven package, from the repository root:

```bash
docker build -f docker/Dockerfile -t product-api:local .
docker run --rm -p 8080:8080 product-api:local
```

The Dockerfile copies the packaged JAR produced by Maven. Docker Hub publishing will require registry credentials in Jenkins, not hardcoded secrets.

## Kubernetes: next lab

The manifests are templates and currently use `YOUR_DOCKERHUB_USERNAME/product-api:1`. Replace this with an image that you actually pushed to a registry before applying them.

```bash
minikube start
kubectl apply -f kubernetes/
kubectl rollout status deployment/product-api
kubectl get pods,svc
minikube service product-api --url
```

To test locally without pushing to a registry, you can build the image inside Minikube and update the deployment image to the matching local tag. Do not apply the placeholder image as-is.

## Learning objectives

1. Understand Pipeline-as-Code and SCM checkout.
2. Build a real Java application with Maven.
3. Run and publish automated JUnit tests.
4. Archive reproducible application artifacts.
5. Build a Docker image and publish versioned tags (next lab).
6. Deploy and verify Kubernetes rolling updates (next lab).
7. Add GitHub webhooks, SonarQube, security scanning, Helm, and EKS in advanced labs.

## Troubleshooting

- **`mvn: not found`**: Install Maven on the Jenkins agent or configure a Jenkins Maven tool.
- **Wrong Java version**: Confirm `java -version` reports JDK 21 on the build agent.
- **Cannot find Jenkinsfile**: Confirm script path `cicd/jenkins/Jenkinsfile` and branch `main`.
- **ImagePullBackOff**: Check the registry image name, tag, visibility, and pull credentials.
- **Pod not ready**: Inspect `kubectl describe pod` and `kubectl logs`.

**Note:** Source files and pipeline configuration are provided as a learning starter. Run `mvn clean verify` in your environment to validate dependency resolution and tests before treating it as a verified build.
