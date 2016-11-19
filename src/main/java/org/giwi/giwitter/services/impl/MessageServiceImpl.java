package org.giwi.giwitter.services.impl;

import com.google.inject.Inject;
import io.vertx.core.Future;
import org.giwi.giwitter.annotation.ProxyService;
import org.giwi.giwitter.services.MessageService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import org.giwi.giwitter.services.UserService;

import java.util.List;

/**
 * The type Message service.
 */
@ProxyService(address = MessageService.ADDRESS, iface = MessageService.class)
public class MessageServiceImpl implements MessageService {

    @Inject
    private MongoClient mongoClient;
    @Inject
    private UserService userService;
    private Vertx vertx;

    /**
     * Instantiates a new Message service.
     *
     * @param vertx the vertx
     */
    public MessageServiceImpl(Vertx vertx) {
        super();
        this.vertx = vertx;
    }

    @Override
    public void getList(Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        mongoClient.find(COLLECTION, new JsonObject().put("$query", new JsonObject())
                .put("$orderby", new JsonObject().put("date", -1)), resultHandler);
    }

    @Override
    public void get(String id, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoClient.findOne(COLLECTION, new JsonObject().put("_id", id), new JsonObject(), resultHandler);
    }

    @Override
    public void addMessage(JsonObject message, Handler<AsyncResult<String>> resultHandler) {
        JsonObject json = new JsonObject();
        json.put("content", message.getString("content"));
        json.put("date", System.currentTimeMillis());
        userService.getUser(new JsonObject().put("_id", message.getString("user_id")), res -> {
            if (res.succeeded()) {
                json.put("author", res.result());
                mongoClient.insert(COLLECTION, json, resultHandler);
            } else {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void delete(String id, Handler<AsyncResult<MongoClientDeleteResult>> resultHandler) {
        mongoClient.removeDocument(COLLECTION, new JsonObject().put("_id", id), resultHandler);
    }
}
