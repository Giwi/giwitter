package org.giwi.giwitter.beans;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

/**
 * The interface Auth utils.
 */
public interface AuthUtils {
    /**
     * The constant DEFAULT_SESSION_TIMEOUT.
     */
    long DEFAULT_SESSION_TIMEOUT = 1000L * 60 * 30;
    /**
     * The constant TOKEN.
     */
    String TOKEN = "X-secure-Token";

    /**
     * Gets client from token.
     *
     * @param req           the req
     * @param resultHandler the result handler
     */
    void getClientFromToken(HttpServerRequest req, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Test validity.
     *
     * @param user          the user
     * @param resultHandler the result handler
     */
    void testValidity(JsonObject user, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Gets user from client.
     *
     * @param user          the user
     * @param resultHandler the result handler
     */
    void getUserFromClient(JsonObject user, Handler<AsyncResult<JsonObject>> resultHandler);

}
