#!/bin/bash

# ---------------------------------------------------------
# CSYE 6225 Application Setup Script for Custom Image
# Purpose: Install dependencies and configure application
# Database: MySQL
# ---------------------------------------------------------

# Enable strict error handling
set -e

# Define variables
DB_NAME="csye6225_db"
DB_USER="csye6225"
DB_PASSWORD="csye6225_password"
APP_USER="csye6225"
APP_GROUP="csye6225"
APP_DIR="/opt/csye6225"
APP_JAR="webapp.jar"

echo "[INFO] Starting application setup..."

# ---------------------------------------------------------
# 1. Update system packages
# ---------------------------------------------------------
echo "[INFO] Updating package lists..."
sudo apt-get update

echo "[INFO] Upgrading installed packages..."
sudo DEBIAN_FRONTEND=noninteractive apt-get upgrade -y

# ---------------------------------------------------------
# 2. Install Java 21 (OpenJDK)
# ---------------------------------------------------------
echo "[INFO] Installing Java 21..."
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-21-jdk

# Verify Java installation
java -version

# ---------------------------------------------------------
# 3. Install MySQL Server
# ---------------------------------------------------------
echo "[INFO] Installing MySQL Server..."
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server

# Start MySQL service
sudo systemctl start mysql
sudo systemctl enable mysql

# ---------------------------------------------------------
# 4. Configure MySQL Database
# ---------------------------------------------------------
echo "[INFO] Configuring MySQL database..."

# Create database
sudo mysql -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME};"

# Create database user and grant privileges
sudo mysql -e "CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';"
sudo mysql -e "GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';"
sudo mysql -e "FLUSH PRIVILEGES;"

echo "[INFO] Database ${DB_NAME} created with user ${DB_USER}."

# ---------------------------------------------------------
# 5. Create Application Group (Idempotent)
# ---------------------------------------------------------
echo "[INFO] Creating application group..."
if ! getent group ${APP_GROUP} > /dev/null; then
    sudo groupadd ${APP_GROUP}
    echo "[INFO] Group ${APP_GROUP} created."
else
    echo "[INFO] Group ${APP_GROUP} already exists."
fi

# ---------------------------------------------------------
# 6. Create Application User (System user, no login)
# ---------------------------------------------------------
echo "[INFO] Creating application user..."
if ! id -u ${APP_USER} > /dev/null 2>&1; then
    # -r: system user, -g: primary group, -s: no login shell
    sudo useradd -r -g ${APP_GROUP} -s /usr/sbin/nologin ${APP_USER}
    echo "[INFO] User ${APP_USER} created."
else
    echo "[INFO] User ${APP_USER} already exists."
fi

# ---------------------------------------------------------
# 7. Create Application Directory
# ---------------------------------------------------------
echo "[INFO] Creating application directory..."
sudo mkdir -p ${APP_DIR}

# ---------------------------------------------------------
# 8. Copy Application JAR (Packer will provide this file)
# ---------------------------------------------------------
# Note: The JAR file should be present in /tmp/ when this script runs
# Packer will copy it there before executing this script

if [ -f "/tmp/${APP_JAR}" ]; then
    echo "[INFO] Copying application JAR to ${APP_DIR}..."
    sudo cp /tmp/${APP_JAR} ${APP_DIR}/${APP_JAR}
else
    echo "[WARN] Application JAR not found at /tmp/${APP_JAR}"
    echo "[WARN] This is expected if running setup manually"
fi

# ---------------------------------------------------------
# 9. Create Application Configuration (application.properties)
# ---------------------------------------------------------
echo "[INFO] Creating application configuration..."
sudo tee ${APP_DIR}/application.properties > /dev/null <<EOF
spring.datasource.url=jdbc:mysql://localhost:3306/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
server.port=8080
EOF

# ---------------------------------------------------------
# 10. Set File Permissions
# ---------------------------------------------------------
echo "[INFO] Setting file permissions..."
sudo chown -R ${APP_USER}:${APP_GROUP} ${APP_DIR}
sudo chmod -R 750 ${APP_DIR}
sudo chmod 640 ${APP_DIR}/application.properties

echo "[INFO] Application setup completed successfully!"

echo "Setup script completed successfully!"
