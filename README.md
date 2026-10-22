# jenkins-cicd-lab
# Jenkins to Kubernetes: End-to-End CI/CD Pipeline

## Project Overview

This project demonstrates how to build and automate a complete CI/CD pipeline using **Jenkins, GitHub, Maven, Docker, and Kubernetes**.

The goal of this lab is to understand how these technologies work together to automate application delivery, from the moment a developer pushes code to GitHub to the point where the application is deployed and running in Kubernetes.

For this project, I'm using a Java Spring Boot application as our sample workload. Instead of manually building, testing, packaging, and deploying the application every time a change is made, Jenkins handles the process through an automated pipeline.

The pipeline is configured to check GitHub for changes using **Poll SCM**. When Jenkins detects a new commit, it executes the Jenkinsfile, builds and tests the application, creates a Docker image, pushes the image to Docker Hub, and deploys the updated application to a Kubernetes cluster running on Minikube.

This is a practical demonstration of how CI/CD automation reduces manual deployment tasks, improves consistency, and helps teams deliver application changes more efficiently.

## Technologies Used

| Technology | Purpose |
|---|---|
| GitHub | Source code management and version control |
| Jenkins | CI/CD pipeline orchestration and automation |
| Java / Spring Boot | Sample application |
| Maven | Dependency management, application build, and testing |
| Docker | Application containerization |
| Docker Hub | Container image registry |
| Kubernetes | Container orchestration and application deployment |
| Minikube | Local Kubernetes development environment |
| kubectl | Kubernetes cluster management |

## CI/CD Pipeline Architecture

The application follows this deployment workflow:

```text
Developer
    |
    | git push
    v
GitHub Repository
    |
    | Poll SCM detects changes
    v
Jenkins Pipeline
    |
    +-- Checkout Source Code
    |
    +-- Maven Build
    |
    +-- Run Automated Tests
    |
    +-- Build Docker Image
    |
    +-- Tag Docker Image
    |
    +-- Push Image to Docker Hub
    |
    +-- Deploy to Kubernetes
    |
    v
Minikube Cluster
    |
    +-- Kubernetes Deployment
    |
    +-- Application Pods
    |
    +-- Kubernetes Service
    |
    v
Spring Boot Application
```

## Understanding the Pipeline Workflow

### 1. Source Code Management — GitHub

GitHub serves as the central repository for the application source code and deployment configurations.

The repository contains the Spring Boot application, Maven configuration, Dockerfile, Jenkinsfile, and Kubernetes manifests.

Using Git allows us to track changes, maintain version history, and manage the application code alongside the infrastructure configurations needed to deploy it.

### 2. Continuous Integration — Jenkins

Jenkins is responsible for orchestrating the CI/CD workflow.

For this lab, I configured Jenkins to use **Pipeline Script from SCM**, which allows Jenkins to retrieve the pipeline definition directly from GitHub.

The Jenkinsfile is located at:

`jenkins/Jenkinsfile`

Rather than defining the entire pipeline
