#!/usr/bin/env bash
cd webroot
rm -fr dist
ng build --prod
cp -R dist/* ../src/main/resources/webroot/.
cd ..
apidoc -i src/main/java
mv doc src/main/resources/webroot/api-doc
./gradlew shadowJar