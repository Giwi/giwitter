#!/usr/bin/env bash
/etc/init.d/mongodb.sh start
mongo giwitter --eval "db.user.createIndex( { username: 1 }, { unique: true } )"
export ENV=PROD
java -jar /opt/giwitter/Giwitter-fat.jar