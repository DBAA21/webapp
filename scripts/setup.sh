#!/bin/bash

# ---------------------------------------------------------
# CSYE6225 Server Setup Script
# Purpose: Automate server configuration for application deployment
# Database: MySQL
# ---------------------------------------------------------

# Enable strict error handling: exit immediately if any command fails
set -e

# Define variables for easy maintenance
DB_NAME="csye6225_db"
DB_USER="csye6225"
DB_PASSWORD="csye6225_password"
APP_USER="csye6225"
APP_GROUP="csye6225"
APP_DIR="/opt/csye6225"

echo "Starting setup script..."

# ---------------------------------------------------------
# 1. Update system package lists
# ---------------------------------------------------------
echo "Updating package lists..."
sudo apt-get update

# ---------------------------------------------------------
# 2. Upgrade installed packages
# ---------------------------------------------------------
# DEBIAN_FRONTEND=noninteractive prevents interactive prompts during upgrade
echo "Upgrading installed packages..."
sudo DEBIAN_FRONTEND=noninteractive apt-get upgrade -y

# ---------------------------------------------------------
# 2.5 Install Java 17 (OpenJDK)
# ---------------------------------------------------------
echo "Installing Java 17..."
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-17-jdk

# Verify Java installation
java -version

# ---------------------------------------------------------
# 3. Install MySQL Server
# ---------------------------------------------------------
echo "Installing MySQL Server..."
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server

# Start MySQL service and enable it to start on boot
sudo systemctl start mysql
sudo systemctl enable mysql

# ---------------------------------------------------------
# 4. Configure Database (Idempotent: CREATE IF NOT EXISTS)
# ---------------------------------------------------------
echo "Configuring database..."
# Create database only if it doesn't already exist
sudo mysql -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME};"
echo "Database ${DB_NAME} created (if it didn't exist)."

# Create database user (idempotent with DROP USER IF EXISTS)
sudo mysql -e "DROP USER IF EXISTS '${DB_USER}'@'localhost';"
sudo mysql -e "CREATE USER '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';"
sudo mysql -e "GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';"
sudo mysql -e "FLUSH PRIVILEGES;"
echo "Database user ${DB_USER} configured with full access to ${DB_NAME}."

# ---------------------------------------------------------
# 5. Create Application Group (Idempotent)
# ---------------------------------------------------------
echo "Creating application group..."
# Check if group exists before creating
if ! getent group ${APP_GROUP} > /dev/null; then
    sudo groupadd ${APP_GROUP}
    echo "Group ${APP_GROUP} created."
else
    echo "Group ${APP_GROUP} already exists."
fi

# ---------------------------------------------------------
# 6. Create Application User (Idempotent)
# ---------------------------------------------------------
echo "Creating application user..."
# Check if user exists before creating
if ! id -u ${APP_USER} > /dev/null 2>&1; then
    # -r: system user, -g: primary group, -s: login shell (nologin for security)
    sudo useradd -r -g ${APP_GROUP} -s /usr/sbin/nologin ${APP_USER}
    echo "User ${APP_USER} created."
else
    echo "User ${APP_USER} already exists."
fi

# ---------------------------------------------------------
# 7. Setup Application Directory
# ---------------------------------------------------------
echo "Setting up application directory..."
# Create directory if it doesn't exist
sudo mkdir -p ${APP_DIR}

# Copy application JAR from /tmp (placed there by packer provisioner)
if [ -f "/tmp/webapp.jar" ]; then
    echo "Copying application JAR to ${APP_DIR}..."
    sudo cp /tmp/webapp.jar ${APP_DIR}/webapp.jar
    echo "Application JAR deployed."
else
    echo "ERROR: Application JAR not found at /tmp/webapp.jar"
    echo "This file should be provisioned by packer before running this script"
    exit 1
fi

# ---------------------------------------------------------
# 8. Create Application Configuration
# ---------------------------------------------------------
echo "Creating application configuration..."
sudo tee ${APP_DIR}/application.properties > /dev/null <<EOF
spring.datasource.url=jdbc:mysql://localhost:3306/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.application.name=csye6225-webapp
server.port=8080
server.servlet.context-path=/
EOF
echo "Application configuration created."

# ---------------------------------------------------------
# 9. Set File Permissions
# ---------------------------------------------------------
echo "Setting permissions..."
# Change ownership to application user and group
sudo chown -R ${APP_USER}:${APP_GROUP} ${APP_DIR}

# Set permissions: owner(rwx), group(rx), others(none) -> 750
sudo chmod -R 750 ${APP_DIR}

echo "Setup script completed successfully!"
