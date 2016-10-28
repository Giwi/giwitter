package org.giwi.giwitter.routes.pub;

import com.google.inject.Inject;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.giwi.giwitter.annotation.VertxRoute;
import org.giwi.giwitter.beans.ResponseUtils;
import org.giwi.giwitter.exception.BusinessException;
import org.giwi.giwitter.services.UserService;

/**
 * The type Users route.
 */
@VertxRoute(rootPath = "/api/1/user")
public class UsersRoute implements VertxRoute.Route {

    @Inject
    private ResponseUtils responseUtils;
    @Inject
    private UserService userService;

    @Override
    public Router init(Vertx vertx) {
        Router router = Router.router(vertx);
        router.put("/register").handler(this::register);
        router.post("/login").handler(this::login);
        router.get("/avatar/:id").handler(this::getAvatar);
        return router;
    }

    private void getAvatar(RoutingContext rc) {
        userService.getAvatar(rc.request().getParam("id"), res -> {
            if (res.succeeded()) {
                rc.response()
                        .putHeader("Content-Type", res.result().getString("type"))
                        .end(Buffer.buffer(res.result().getBinary("asset")));
            } else {
                rc.fail(new BusinessException(res.cause()));
            }
        });
    }

    /**
     * @api {post} /api/1/user/login Login
     * @apiName login
     * @apiGroup Users
     * @apiDescription user login
     * @apiParam {String} username User username
     * @apiParam {String} password User password
     * @apiSuccess {String} secureToken Token of the User
     * @apiSuccess {Boolean} status Status
     */
    private void login(RoutingContext context) {
        userService.login(context.getBodyAsJson(), res -> {
            if (res.succeeded()) {
                context.response().end(res.result().encode());
            } else {
                context.fail(new BusinessException(res.cause(), 401));
            }
        });
    }

    /**
     * @api {put} /api/1/user/register Register
     * @apiName register
     * @apiGroup Users
     * @apiDescription Register a new user
     * @apiParam {String} username User username
     * @apiParam {String} password User password
     * @apiParam {String} name User name
     * @apiParam {String} firstname User firstname
     * @apiSuccess {Boolean} status Status
     */
    private void register(RoutingContext context) {
        userService.register(context.getBodyAsJson(), res -> {
            if (res.succeeded()) {
                responseUtils.sendStatus(context.response(), true);
            } else {
                context.fail(new BusinessException(res.cause()));
            }
        });
    }
}
