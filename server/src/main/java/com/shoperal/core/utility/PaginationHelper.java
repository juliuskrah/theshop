package com.shoperal.core.utility;

import static java.nio.charset.StandardCharsets.UTF_16;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoperal.core.dto.PaginationCursor;
import com.shoperal.core.dto.PaginationCursor.Property;

import org.springframework.util.Assert;

/**
 * Utility class for pagination
 * 
 * @author Julius Krah
 */
public class PaginationHelper {
    private final ObjectMapper mapper;

    public PaginationHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private PaginationCursor toCursor(String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String jsonString = new String(decodedBytes, UTF_16);
        try {
            return mapper.readValue(jsonString, PaginationCursor.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Malformed cursor", e);
        }
    }

    private Map<String, String> populateMap(String key, Map<String, String> target) {
        var token = target.get(key);
        var cursor = toCursor(token);
        Assert.notNull(cursor.getLastId(), "'lastId' must not be null");
        Assert.notNull(cursor.getLastValue(), "'lastValue' must not be null");
        target.put("id", cursor.getLastId().toString());
        target.put(cursor.getLastValue().getField(), cursor.getLastValue().getValue());
        return target;
    }

    /**
     * Decodes the after or before queryParams and expands it into the queryParams
     * 
     * @param queryParams the URL query parameters. Must not be {@literal null}
     * @return updated map containing the decoded after or before parameters
     * @throws IllegalArgumentException when the {@literal queryParams} contain both
     *                                  after and before
     */
    public Map<String, String> decodeTokens(Map<String, String> queryParams) {
        if (queryParams.containsKey("after") && queryParams.containsKey("before"))
            throw new IllegalArgumentException("Your request cannot contain both 'after' and 'before' queries");
        if (queryParams.containsKey("after")) {
            return populateMap("after", queryParams);
        } else if (queryParams.containsKey("before")) {
            return populateMap("before", queryParams);
        }
        return queryParams;
    }

    /**
     * Creates a base64 string using the example JSON representation below
     * 
     * <pre>
     * <code>
     * {
     *   "lastId": "66172841-c12c-4b6d-9827-37d7d9e86cad",
     *   "lastValue": { 
     *     "field": "name", // name, createdDate, lastModifiedDate
     *     "value": "<product name>" 
     * }
     * </code>
     * </pre>
     * 
     * @param options the options used for token
     * @return base64 token
     */
    public String toBeforeOrAfterToken(Map<String, String> options) {
        Assert.isTrue(options.containsKey("id"), "'id' must be present");
        Assert.isTrue(options.containsKey("field"), "'field' must be present");
        Assert.isTrue(options.containsKey("value"), "'value' must be present");
        var id = options.get("id");
        var field = options.get("field");
        var value = options.get("value");
        try {
            var cursor = new PaginationCursor(UUID.fromString(id), new Property(field, value));
            var json = mapper.writeValueAsString(cursor);
            return Base64.getEncoder().encodeToString(json.getBytes(UTF_16));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid arguments provided for cursor", e);
        }
    }
}
