package org.giwi.giwitter.routes.priv;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.giwi.giwitter.annotation.VertxRoute;
import org.giwi.giwitter.beans.ResponseUtils;
import org.giwi.giwitter.exception.BusinessException;
import org.giwi.giwitter.services.MessageService;


/**
 * The type Message route.
 */
@VertxRoute(rootPath = "/api/1/private/message")
public class MessageRoute implements VertxRoute.Route {

    @Inject
    private ResponseUtils responseUtils;
    @Inject
    private MessageService messageService;
    private Vertx vertx;

    /**
     * Init router.
     *
     * @param vertx the vertx
     *
     * @return the router
     */
    @Override
    public Router init(Vertx vertx) {
        this.vertx = vertx;
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/").handler(this::getList);
        router.get("/:start/:nb").handler(this::getPaginated);
        router.get("/:id").handler(this::get);
        router.post("/").handler(this::create);
        router.delete("/:id").handler(this::delete);
        return router;
    }

    /**
     * @api {get} /api/1/private/message/:start/:nb Get list of messages
     * @apiName getList
     * @apiGroup Message
     * @apiParam {number} start Start
     * @apiParam {number} nb Start
     * @apiDescription Get list of messages
     * @apiSuccess {Array} messages List of messages
     * @apiHeader {String} X-secure-Token Authorization token.
     */
    private void getPaginated(RoutingContext rc) {
        int start = Integer.parseInt(rc.request().getParam("start"));
        int nb = Integer.parseInt(rc.request().getParam("nb"));
        messageService.getList(cr -> {
            if (cr.succeeded()) {
                JsonArray jar = new JsonArray();
                cr.result().subList(start, start + nb).forEach(jar::add);
                rc.response().end(jar.encode());
            } else {
                rc.fail(new BusinessException(cr.cause()));
            }
        });
    }

    /**
     * @api {get} /api/1/private/message Get list of messages
     * @apiName getList
     * @apiGroup Message
     * @apiDescription Get list of messages
     * @apiSuccess {Array} messages List of messages
     * @apiHeader {String} X-secure-Token Authorization token.
     */
    private void getList(RoutingContext rc) {
        messageService.getList(cr -> {
            if (cr.succeeded()) {
                JsonArray jar = new JsonArray();
                cr.result().forEach(jar::add);
                rc.response().end(jar.encode());
            } else {
                rc.fail(new BusinessException(cr.cause()));
            }
        });
    }

    /**
     * @api {get} /api/1/private/message/:id Get a message
     * @apiName get
     * @apiGroup Message
     * @apiParam {number} id Message id
     * @apiDescription Get a message
     * @apiSuccess {Array} messages Message
     * @apiHeader {String} X-secure-Token Authorization token.
     */
    private void get(RoutingContext rc) {
        messageService.get(rc.request().getParam("id"), cr -> {
            if (cr.succeeded()) {
                rc.response().end(cr.result().encode());
            } else {
                rc.fail(new BusinessException(cr.cause()));
            }
        });
    }

    /**
     * @api {post} /api/1/private/message Add a message
     * @apiName create
     * @apiGroup Message
     * @apiParam {Object} message Message
     * @apiDescription Add a message
     * @apiSuccess {Object} message Message
     * @apiHeader {String} X-secure-Token Authorization token.
     */
    private void create(RoutingContext rc) {
        messageService.addMessage(rc.getBodyAsJson(), res -> {
            if (res.succeeded()) {
                messageService.get(res.result(), cr -> {
                    if (cr.succeeded()) {
                        vertx.eventBus().send("new-message", cr.result());
                        rc.response().end(cr.result().encode());
                    } else {
                        rc.fail(new BusinessException(cr.cause()));
                    }
                });
            } else {
                rc.fail(new BusinessException(res.cause()));
            }
        });
    }

    /**
     * @api {delete} /api/1/private/message Delete a message
     * @apiName delete
     * @apiGroup Message
     * @apiParam {number} id Message id
     * @apiDescription Delete a message
     * @apiSuccess {Object} status Status
     * @apiHeader {String} X-secure-Token Authorization token.
     */
    private void delete(RoutingContext rc) {
        messageService.delete(rc.request().getParam("id"), res -> {
            if (res.succeeded()) {
                vertx.eventBus().send("del-message", true);
                responseUtils.sendStatus(rc.response(), true);
            } else {
                rc.fail(new BusinessException(res.cause()));
            }
        });
    }
}
