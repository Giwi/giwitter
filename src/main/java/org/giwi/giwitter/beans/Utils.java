package org.giwi.giwitter.beans;

import org.giwi.giwitter.exception.BusinessException;
import io.vertx.core.json.JsonObject;

/**
 * The interface Utils.
 */
public interface Utils {
    /**
     * Test mandatory fields.
     *
     * @param body   the body
     * @param fields the fields
     * @throws BusinessException the business exception
     */
    void testMandatoryFields(JsonObject body, String... fields) throws BusinessException;
}
