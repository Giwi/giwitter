# Giwitter

Small messaging board web app based on [Angular 2](https://angular.io/),
[Bootstrap 4](http://v4-alpha.getbootstrap.com/),
[Vert.x 3](http://vertx.io/) and [MongoDB](https://www.mongodb.com)

- User registration
- Login
- Messages wall
- Add message
- Delete owned messages
- Update user name and profile

[Demo](http://vps288382.ovh.net/#/)

It comes with a Dockerfile witch runs this app with a mongo DB.

## Requirements

- NodeJS 5+
- NPM 3+
- Java JDK8+

## Setup (Ubuntu)

    $ ./script/setup.sh

## Run (Dev mode)

    $ ./gradlew r
    $ cd webroot
    $ ng serve

[http://localhost:4200/](http://localhost:4200/)


https://github.com/angular/angular-cli#generating-components-directives-pipes-and-services

## Mongo setup

    db.user.createIndex( { username: 1 }, { unique: true } )

## Dist

    $ ./script/build.sh

You can test locally with :

    $ ENV=PROD java -jar application/Giwitter-fat.jar

## Docker

See [https://hub.docker.com/r/giwi/giwitter/](https://hub.docker.com/r/giwi/giwitter/)

    $ docker run -ti -p 8181:8080 -p 27018:27017 giwi/giwitter:latest