package org.giwi.giwitter.verticle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.giwi.giwitter.annotation.ProxyService;
import org.giwi.giwitter.annotation.VertxRoute;
import org.giwi.giwitter.injection.GuiceModule;

/**
 * The type Main verticle.
 */
public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class.getName());

    @Override
    public void start() throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router mainRouter = Router.router(vertx);
        FileSystem fs = vertx.fileSystem();
        JsonObject config = new JsonObject(new String(fs.readFileBlocking("conf.json").getBytes()));
        Injector injector = Guice.createInjector(new GuiceModule(config, vertx));
        ProxyService.Loader.load("org.giwi.giwitter.services.impl", injector, vertx);

        VertxRoute.Loader.getRoutesInPackage("org.giwi.giwitter.routes")
                .entrySet().stream().sorted((e1, e2) -> Integer.compare(e1.getKey().order(), e2.getKey().order()))
                .forEachOrdered(item -> {
                            LOG.info("Injecting " + item.getKey());
                            injector.injectMembers(item.getValue());
                            mainRouter.mountSubRouter(item.getKey().rootPath(), item.getValue().init(vertx));
                        }
                );
        mainRouter.route("/*").handler(StaticHandler.create());
        mainRouter.route().last().handler(context ->
                context.response()
                        .setStatusCode(404)
                        .end(new JsonObject()
                                .put("message", "This is not the service you're looking for")
                                .encode())
        );
        server.requestHandler(mainRouter::accept).listen(8080);
    }
}
