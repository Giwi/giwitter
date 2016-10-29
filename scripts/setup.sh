#!/usr/bin/env bash
rm -fr build
./gradlew clean g
cd webroot
rm -fr node_modules
sudo npm install -g angular-cli apidoc
npm install