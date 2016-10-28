package org.giwi.giwitter.services;

import org.giwi.giwitter.services.impl.MessageServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.List;

/**
 * The interface Baab service.
 */
@ProxyGen
@VertxGen
public interface MessageService {
    /**
     * The constant ADDRESS.
     */
    String ADDRESS = "vertx.message.service";
    /**
     * The constant COLLECTION.
     */
    String COLLECTION = "messages";

    /**
     * Create message service.
     *
     * @param vertx the vertx
     *
     * @return the message service
     */
    static MessageService create(Vertx vertx) {
        return new MessageServiceImpl(vertx);
    }

    /**
     * Create proxy message service.
     *
     * @param vertx   the vertx
     * @param address the address
     *
     * @return the message service
     */
    static MessageService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(MessageService.class, vertx, address);
    }

    /**
     * Gets list.
     *
     * @param resultHandler the result handler
     */
    void getList(Handler<AsyncResult<List<JsonObject>>> resultHandler);

    /**
     * Get.
     *
     * @param id            the id
     * @param resultHandler the result handler
     */
    void get(String id, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Add message.
     *
     * @param message       the message
     * @param resultHandler the result handler
     */
    void addMessage(JsonObject message, Handler<AsyncResult<String>> resultHandler);

    /**
     * Delete.
     *
     * @param id            the id
     * @param resultHandler the result handler
     */
    void delete(String id, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler);
}
