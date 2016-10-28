package org.giwi.giwitter.services.impl;

import com.google.inject.Inject;
import com.mongodb.MongoWriteException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import org.apache.commons.io.FileUtils;
import org.giwi.giwitter.annotation.ProxyService;
import org.giwi.giwitter.beans.Utils;
import org.giwi.giwitter.constants.Roles;
import org.giwi.giwitter.exception.BusinessException;
import org.giwi.giwitter.services.MessageService;
import org.giwi.giwitter.services.UserService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The type Client service.
 */
@ProxyService(address = UserService.ADDRESS, iface = UserService.class)
public class UserServiceImpl implements UserService {

    @Inject
    private MongoClient mongoClient;
    @Inject
    private MongoAuth mongoAuth;
    @Inject
    private Utils utils;

    /**
     * Instantiates a new Client service.
     *
     * @param vertx the vertx
     */
    public UserServiceImpl(Vertx vertx) {
        super();
    }

    @Override
    public void insertUser(JsonObject document, Handler<AsyncResult<String>> resultHandler) {
        mongoClient.insert(COLLECTION, document, resultHandler);
    }

    @Override
    public void updateUser(JsonObject user, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoClient.updateCollection(COLLECTION, new JsonObject().put("_id", user.getString("_id")), new JsonObject().put("$set", user), saveRes -> {
            if (saveRes.succeeded()) {
                mongoClient.updateCollectionWithOptions(MessageService.COLLECTION,
                        new JsonObject().put("author._id", user.getString("_id")),
                        new JsonObject().put("$set", new JsonObject().put("author", user)),
                        new UpdateOptions().setMulti(true),
                        upd -> {
                            if (upd.succeeded()) {
                                getUser(new JsonObject().put("_id", user.getString("_id")), resultHandler);
                            } else {
                                resultHandler.handle(Future.failedFuture(new BusinessException(saveRes.cause(), 401)));
                            }
                        });
            } else {
                resultHandler.handle(Future.failedFuture(new BusinessException(saveRes.cause(), 401)));
            }
        });
    }

    @Override
    public void addAvatar(JsonObject fileUpload, String userId, Handler<AsyncResult<JsonObject>> resultHandler) {
        getUser(new JsonObject().put("_id", userId), res -> {
            if (res.succeeded()) {
                try {
                    byte[] img = FileUtils.readFileToByteArray(new File(fileUpload.getString("file")));
                    mongoClient.save("assets", new JsonObject().put("asset", img).put("type", fileUpload.getString("type")), ins -> {
                        if (ins.succeeded()) {
                            res.result().put("avatar", ins.result());
                            updateUser(res.result(), resultHandler);
                        } else {
                            resultHandler.handle(Future.failedFuture(new BusinessException(ins.cause(), 500)));
                        }
                    });
                } catch (IOException e) {
                    resultHandler.handle(Future.failedFuture(new BusinessException(e, 500)));
                }
            } else {
                resultHandler.handle(Future.failedFuture(new BusinessException(res.cause(), 404)));

            }
        });
    }

    @Override
    public void getAvatar(String id, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoClient.findOne("assets", new JsonObject().put("_id", id), new JsonObject(), resultHandler);
    }

    @Override
    public void getUser(JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoClient.findOne(COLLECTION, query, new JsonObject(), resultHandler);
    }

    @Override
    public void getUserList(JsonObject query, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        mongoClient.find(COLLECTION, query, resultHandler);
    }

    @Override
    public void register(JsonObject user, Handler<AsyncResult<JsonObject>> resultHandler) {
        utils.testMandatoryFields(user, mongoAuth.getPasswordField(), mongoAuth.getUsernameField(), "name", "firstname");
        mongoAuth.insertUser(user.getString(mongoAuth.getUsernameField()),
                user.getString(mongoAuth.getPasswordField()),
                Collections.singletonList(Roles.USER.name()),
                new ArrayList<>(),
                res -> {
                    if (res.succeeded()) {
                        user.put("_id", res.result());
                        user.remove("password");
                        updateUser(user, saveRes -> {
                            if (saveRes.succeeded()) {
                                resultHandler.handle(Future.succeededFuture(new JsonObject().put("status", true)));
                            } else {
                                resultHandler.handle(Future.failedFuture(new BusinessException(saveRes.cause(), 401)));
                            }
                        });
                        resultHandler.handle(Future.succeededFuture(new JsonObject().put("status", true)));
                    } else {
                        if (((MongoWriteException) res.cause()).getCode() == 11000) {
                            resultHandler.handle(Future.failedFuture(new BusinessException("Login déjà utilisé", 401)));
                        } else {
                            resultHandler.handle(Future.failedFuture(res.cause()));
                        }
                    }
                });
    }

    @Override
    public void login(JsonObject query, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoAuth.authenticate(query, res -> {
            if (res.succeeded()) {
                JsonObject secureToken = new JsonObject()
                        .put("token", UUID.randomUUID().toString())
                        .put("timestamp", System.currentTimeMillis());

                JsonObject response = new JsonObject()
                        .put("secureToken", secureToken.getString("token"))
                        .put("status", true);

                getUser(new JsonObject().put("_id", res.result().principal().getString("_id")), mongoRes -> {
                    if (mongoRes.succeeded() && mongoRes.result() != null) {
                        JsonObject u = mongoRes.result();
                        u.put("secureToken", secureToken);
                        updateUser(u, saveRes -> {
                            if (saveRes.succeeded()) {
                                resultHandler.handle(Future.succeededFuture(response));
                            } else {
                                resultHandler.handle(Future.failedFuture(new BusinessException(saveRes.cause(), 401)));
                            }
                        });
                    } else {
                        resultHandler.handle(Future.failedFuture(new BusinessException(mongoRes.cause(), 401)));
                    }
                });
            } else {
                resultHandler.handle(Future.failedFuture(new BusinessException(res.cause(), 401)));
            }
        });
    }
}
