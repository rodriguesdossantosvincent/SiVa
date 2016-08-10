#!/usr/bin/env bash

# Copy JAR files to apps directory
mv ci-build-info.yml /var/apps
mv *.jar /var/apps
ls -al /var/apps

# Remove keystore directory
rm -fr /var/apps/etc

# Run installation of new apps
chmod +x ./install.sh && ./install.sh
