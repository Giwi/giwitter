#!/usr/bin/env bash
cd webroot
rm -fr dist
ng build --prod
cp -R dist/* ../src/main/resources/webroot/.
cd ..
apidoc -i src/main/java
mv doc/index.html doc/apidoc.html
mv doc/* src/main/resources/webroot/.
mv webroot/src/favicon.ico src/main/resources/webroot/.
./gradlew shadowJar