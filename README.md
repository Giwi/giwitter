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

## setup

    $ [sudo] npm install -g angular-cli
    $ cd webroot
    $ npm install

## run

    $ ./gradlew r
    $ cd webroot
    $ ng serve

[http://localhost:4200/](http://localhost:4200/)


https://github.com/angular/angular-cli#generating-components-directives-pipes-and-services


dans mongo

    db.user.createIndex( { username: 1 }, { unique: true } )