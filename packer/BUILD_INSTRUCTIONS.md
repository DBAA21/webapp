# Packer Image Build Instructions

## Overview
This document provides instructions for building custom machine images using Packer for both AWS and GCP.

## Prerequisites
- Packer v1.9.4+ installed
- AWS CLI configured with credentials (for AWS builds)
- gcloud CLI configured with credentials (for GCP builds)
- Application JAR built at `target/webapp-0.0.1-SNAPSHOT.jar`

## Build Process

### Step 1: Prepare Application JAR
The template expects the JAR at `../target/webapp-0.0.1-SNAPSHOT.jar` relative to the packer directory.

```bash
cd ~/webapp
mvn clean package -DskipTests
```

### Step 2: Navigate to Packer Directory
```bash
cd ~/webapp/packer
```

### Step 3: Build AWS AMI Only
```bash
packer build -only='amazon-ebs.webapp' aws-gcp.pkr.hcl
```

Expected output:
- Creates an AMI with the template name pattern: `csye6225-webapp-TIMESTAMP`
- Shares AMI with demo account (506160144092)
- Stores AMI information in AWS account
- Build time: ~5-10 minutes

### Step 4: Build GCP Image Only
```bash
packer build -only='googlecompute.webapp' aws-gcp.pkr.hcl
```

Expected output:
- Creates a custom image in weihong-dev project
- Image name pattern: `csye6225-webapp-TIMESTAMP`
- Image family: `csye6225-webapp`
- Shares with demo project (weihong-demo)
- Build time: ~5-10 minutes

### Step 5: Build Both AWS and GCP
```bash
packer build aws-gcp.pkr.hcl
```

This runs both builders sequentially.
- Build time: ~10-20 minutes

## File Structure Verification

Before building, verify files are in correct locations:

```bash
# From ~/webapp directory
ls -lh target/webapp-0.0.1-SNAPSHOT.jar
ls -lh scripts/setup.sh
ls -lh scripts/webapp.service
ls -lh packer/aws-gcp.pkr.hcl
```

## Environment Variables

### AWS  Build
Set AWS profile if not using default:
```bash
export AWS_PROFILE=demo
packer build -only='amazon-ebs.webapp' aws-gcp.pkr.hcl
```

### GCP Build
Set GCP project if multiple projects configured:
```bash
gcloud config set project weihong-dev
packer build -only='googlecompute.webapp' aws-gcp.pkr.hcl
```

## Image Contents

Both images include the following:
- Ubuntu 24.04 LTS base OS
- Java 21 (OpenJDK)
- MySQL Server
- Application JAR at `/opt/csye6225/webapp.jar`
- Application configuration at `/opt/csye6225/application.properties`
- Systemd service configured for auto-start
- csye6225 user and group (system, no login)
- 25 GB root volume

## Verification After Build

### AWS AMI Verification
```bash
# List built AMIs (filter by name or tag)
aws ec2 describe-images \
  --owners self \
  --filters Name=name,Values=csye6225-webapp-* \
  --region us-east-1 \
  --profile demo
```

### GCP Image Verification
```bash
# List custom images
gcloud compute images list \
  --filter="name~csye6225-webapp-*" \
  --project=weihong-dev
```

## Launching Instances from Images

### From AWS AMI
```bash
aws ec2 run-instances \
  --image-id ami-xxxxxxxxx \
  --instance-type t2.micro \
  --region us-east-1 \
  --profile demo
```

### From GCP Image
```bash
gcloud compute instances create csye6225-test \
  --image=csye6225-webapp-timestamp \
  --machine-type=e2-medium \
  --zone=us-east1-b \
  --project=weihong-dev
```

## Troubleshooting

### Build Fails with "File not found"
- Verify JAR exists: `ls -lh target/webapp-0.0.1-SNAPSHOT.jar`
- Run from `~/webapp/packer` directory
- Check relative paths in template

### AWS Build Fails with "Unauthorized"
- Verify AWS credentials: `aws sts get-caller-identity`
- Ensure AWS profile has EC2 and AMI permissions
- Check boto3/botocore dependencies

### GCP Build Fails with "Invalid project"
- Verify GCP project: `gcloud config get-value project`
- Ensure project has Compute API enabled
- Check service account credentials

### MySQL Configuration Fails
- Setup script handles idempotent database creation
- Check MySQL service started on VM
- Verify MySQL user permissions in script

## Build Logs

Packer outputs detailed logs during build:
- `-debug` flag for verbose output
- Logs saved to `crash.log` if build crashes
- SSH logs available with `-debug`

Example with debugging:
```bash
packer build -debug -only='amazon-ebs.webapp' aws-gcp.pkr.hcl
```

## Important Notes

1. **Image Sharing**: 
   - AWS: Uses `ami_users` to share with demo account
   - GCP: Uses `image_project_id` to share with demo project

2. **Timestamp**: Template uses `{{timestamp}}` for unique image names

3. **Cost**: Image builds incur minimal cloud costs (~$0.02-0.05 per build)

4. **Idempotent**: All image build steps are idempotent and safe to repeat

5. **Security**: 
   - Application runs as csye6225 (non-root)
   - Systemd service has security hardening
   - CloudInit/setup scripts remove credentials from image

## CI/CD Integration

For automated builds in CI/CD pipeline:

```bash
#!/bin/bash
set -e

# Build JAR
cd ~/webapp
mvn clean package -DskipTests

# Build images
cd packer
packer build aws-gcp.pkr.hcl

# Optional: Tag and push results to artifact repository
```

See `.github/workflows/` for actual CI/CD configuration.
