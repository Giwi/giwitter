package org.giwi.giwitter.routes;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.giwi.giwitter.annotation.VertxRoute;
import org.giwi.giwitter.services.MessageService;

/**
 * The type Hello route.
 */
@VertxRoute(rootPath = "/api/1/hello")
public class HelloRoute  implements VertxRoute.Route {
    @Inject
    private MessageService messageService;
    private Vertx vertx;@Override

    public Router init(Vertx vertx) {
        this.vertx = vertx;
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/:name").handler(this::hello);
        return router;
    }
    /**
     * @api {get} /api/1/hello/:name Replies Hello
     * @apiName hello
     * @apiGroup Hello
     * @apiParam {string} name Name
     * @apiDescription Replies Hello
     * @apiSuccess {String} response
     */
    private void hello(RoutingContext routingContext) {
        routingContext.response().end("Hello " + routingContext.request().getParam("name"));
    }
}
