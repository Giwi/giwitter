#!/usr/bin/env bash
/etc/init.d/mongodb.sh start
mongo giwitter --eval "db.user.createIndex( { username: 1 }, { unique: true } )"
export ENV=PROD
export LANG=fr_FR.UTF-8
export LANGUAGE=fr_FR
java -jar /opt/giwitter/Giwitter-fat.jar