package org.giwi.giwitter.routes.priv;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.giwi.giwitter.annotation.VertxRoute;
import org.giwi.giwitter.beans.AuthUtils;
import org.giwi.giwitter.beans.ResponseUtils;
import org.giwi.giwitter.exception.BusinessException;
import org.giwi.giwitter.services.UserService;

import java.util.Set;

/**
 * The type Users route.
 */
@VertxRoute(rootPath = "/api/1/private/user")
public class UsersRoute implements VertxRoute.Route {

    @Inject
    private AuthUtils authUtils;
    @Inject
    private ResponseUtils responseUtils;
    @Inject
    private UserService userService;
    private Vertx vertx;

    @Override
    public Router init(Vertx vertx) {
        this.vertx = vertx;
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/logout").handler(this::logout);
        router.get("/").handler(this::getCurrentUser);
        router.post("/").handler(this::updateUser);
        router.post("/avatar/:id").handler(this::uploadAvatar);
        return router;
    }

    private void uploadAvatar(RoutingContext rc) {
        Set<FileUpload> uploads = rc.fileUploads();
        FileUpload fu = uploads.iterator().next();
        JsonObject fuj = new JsonObject()
                .put("file", fu.uploadedFileName())
                .put("type", fu.contentType());
        userService.addAvatar(fuj, rc.request().getParam("id"), res -> {
            if (res.succeeded()) {
                vertx.eventBus().send("del-message", true);
                rc.response().end(cleanUser(res.result()).encode());
            } else {
                rc.fail(new BusinessException(res.cause()));
            }
        });
    }

    /**
     * @api {post} /api/1/private/user Update user
     * @apiName updateUser
     * @apiGroup Users
     * @apiParam {Object} user User
     * @apiDescription Update the user
     * @apiHeader {String} X-secure-Token Authorization token.
     * @apiSuccess {Object} user User
     */
    private void updateUser(RoutingContext context) {
        userService.updateUser(context.getBodyAsJson(), res -> {
            if (res.succeeded()) {
                vertx.eventBus().send("del-message", true);
                context.response().end(cleanUser(res.result()).encode());
            } else {
                context.fail(new BusinessException(res.cause()));
            }
        });
    }


    /**
     * @api {get} /api/1/private/user Get Current User
     * @apiName getCurrentUser
     * @apiGroup Users
     * @apiDescription Get the current logged user
     * @apiHeader {String} X-secure-Token Authorization token.
     * @apiSuccess {Object} user User
     */
    private void getCurrentUser(RoutingContext context) {
        authUtils.getClientFromToken(context.request(), res -> {
            if (res.succeeded()) {
                context.response().end(cleanUser(res.result()).encode());
            } else {
                context.fail(new BusinessException(res.cause()));
            }
        });
    }

    /**
     * @api {get} /api/1/private/user/logout Logout
     * @apiName logout
     * @apiGroup Users
     * @apiDescription Logout the user
     * @apiHeader {String} X-secure-Token Authorization token.
     * @apiSuccess {Boolean} status Status
     */
    private void logout(RoutingContext context) {
        authUtils.getClientFromToken(context.request(), res -> {
            res.result().getJsonObject("account").remove("secureToken");
            userService.updateUser(res.result(), upd -> {
                if (upd.succeeded()) {
                    responseUtils.sendStatus(context.response(), true);
                } else {
                    context.fail(new BusinessException(res.cause()));
                }
            });
        });
    }

    private JsonObject cleanUser(JsonObject user) {
        user.remove("secureToken");
        user.remove("salt");
        user.remove("password");
        return user;
    }
}
