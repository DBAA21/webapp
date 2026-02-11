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

# --- IMPORTANT: Deploy your application files here ---
# Example: sudo unzip -o webapp.zip -d ${APP_DIR}
# Or: sudo cp -r ./app_code/* ${APP_DIR}
# For demonstration, create a placeholder file
sudo touch ${APP_DIR}/app-is-here.txt

echo "Application files deployed."

# ---------------------------------------------------------
# 8. Set File Permissions
# ---------------------------------------------------------
echo "Setting permissions..."
# Change ownership to application user and group
sudo chown -R ${APP_USER}:${APP_GROUP} ${APP_DIR}

# Set permissions: owner(rwx), group(rx), others(rx) -> 755
# For stricter security, use 750 (no access for others)
sudo chmod -R 755 ${APP_DIR}

echo "Setup script completed successfully!"
