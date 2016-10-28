package org.giwi.giwitter;

import org.giwi.giwitter.verticle.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
/**
 * The type Main.
 */
public class Main {
    private Main() {
        // empty
    }
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String... args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
        vertx.deployVerticle(MainVerticle.class.getName());
    }
}
