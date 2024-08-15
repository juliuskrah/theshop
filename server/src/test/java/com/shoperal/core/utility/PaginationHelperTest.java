package com.shoperal.core.utility;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * @author Julius Krah
 */
@TestInstance(Lifecycle.PER_CLASS)
public class PaginationHelperTest {
    private PaginationHelper helper;

    @BeforeAll
    void init() {
        helper = new PaginationHelper(new ObjectMapper());
    }

    @Test
    void whenInvalidDecodeTokenThrows() {
        var options = Map.of("after", "", "before", "");
        assertThrows(IllegalArgumentException.class, //
                () -> helper.decodeTokens(options));
    }

    @Test
    void whenLastValueMissingThrows() {
        var json = "{\"lastId\": \"46469376-2980-4a70-9a1a-95a11d8e399e\"}";
        var encoded = toBase64(json);
        var options = Map.of("after", encoded);
        assertThatThrownBy(() -> helper.decodeTokens(options)).isInstanceOf(IllegalArgumentException.class) //
                .hasMessage("'lastValue' must not be null");
    }

    @Test
    void testDecodeWhenBefore() {
        var json = "{\"lastId\": \"49fc6f0d-0440-4ebb-a506-b57fa6c17796\",\"lastValue\":" //
                + "{\"field\":\"name\", \"value\":\"Jordans\"}}";
        var encoded = toBase64(json);
        Map<String, String> options = new HashMap<>(Map.of("before", encoded));
        options = helper.decodeTokens(options);
        assertThat(options).allSatisfy((key, value) -> {
            assertThat(key).isIn("id", "name", "before");
            assertThat(value).isIn(encoded, "Jordans", "49fc6f0d-0440-4ebb-a506-b57fa6c17796");
        });
    }

    @Test
    void testDecodeWhenAfter() {
        var json = "{\"lastId\": \"958bb0f0-f703-4769-8874-f9c6ff2ddb25\",\"lastValue\":" //
                + "{\"field\":\"createdDate\", \"value\":\"2020-01-01T15:33:52\"}}";
        var encoded = toBase64(json);
        Map<String, String> options = new HashMap<>(Map.of("after", encoded));
        options = helper.decodeTokens(options);
        assertThat(options).allSatisfy((key, value) -> {
            assertThat(key).isIn("id", "after", "createdDate");
            assertThat(value).isIn("2020-01-01T15:33:52", "958bb0f0-f703-4769-8874-f9c6ff2ddb25", encoded);
        });
    }

    @Test
    void testGenerateBeforeOrAfterTokenThrows() {
        var options = Map.of("id", "12345678", "field", "createdDate", "value", "2019-01-01T12:00:00");
        assertThatIllegalArgumentException().isThrownBy(() -> helper.toBeforeOrAfterToken(options)) //
                .withMessage("Invalid arguments provided for cursor");
    }

    @Test
    void testGenerateBeforeOrAfterToken() {
        var options = Map.of("id", "9b1cde1d-12fa-4ef2-b204-d1a63d3d738f", //
                "field", "lastModifiedBy", //
                "value", "2021-01-01T13:00:59");
        var token = helper.toBeforeOrAfterToken(options);

        var json = "{\"lastId\":\"9b1cde1d-12fa-4ef2-b204-d1a63d3d738f\",\"lastValue\":" //
                + "{\"field\":\"lastModifiedBy\",\"value\":\"2021-01-01T13:00:59\"}}";
        var expected = toBase64(json);
        assertThat(token).isEqualTo(expected);
    }

    private String toBase64(String json) {
        var bytes = json.getBytes(UTF_16);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
