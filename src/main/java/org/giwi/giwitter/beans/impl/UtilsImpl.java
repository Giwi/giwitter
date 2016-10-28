package org.giwi.giwitter.beans.impl;

import org.giwi.giwitter.beans.Utils;
import org.giwi.giwitter.exception.BusinessException;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Utils.
 */
public class UtilsImpl implements Utils {
    @Override
    public void testMandatoryFields(JsonObject body, String... fields) throws BusinessException {
        final List<String> missingFields =  Arrays.asList(fields).stream().filter(f->!body.containsKey(f)).collect(Collectors.toList());
        if (!missingFields.isEmpty()) {
            throw new BusinessException("Missing mandatory parameters : " + missingFields, 400);
        }
    }
}
