package butvinm.lab0.task2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestApp {

    @Test
    public void testParseConfig_ValidInputs() {
        var app = new App();
        assertEquals(new App.Config(10, true, "test-key"), app.parseConfig("10", "true", "test-key"));
        assertEquals(new App.Config(5, false, "another-key"), app.parseConfig("5", "false", "another-key"));
        assertEquals(new App.Config(100, true, ""), app.parseConfig("100", "True", ""));
    }

    @Test
    public void testParseConfig_CaseInsensitiveLogRequests() {
        var app = new App();
        assertEquals(new App.Config(10, true, "test-key"), app.parseConfig("10", "TRUE", "test-key"));
        assertEquals(new App.Config(10, true, "test-key"), app.parseConfig("10", "True", "test-key"));
        assertEquals(new App.Config(10, false, "test-key"), app.parseConfig("10", "FALSE", "test-key"));
        assertEquals(new App.Config(10, false, "test-key"), app.parseConfig("10", "False", "test-key"));
    }

    @Test
    public void testParseConfig_MissingArguments() {
        var app = new App();

        // Test with missing arguments
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("10", "true");
        });
        assertEquals("One of the required arguments is missing", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("10");
        });
        assertEquals("One of the required arguments is missing", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig();
        });
        assertEquals("One of the required arguments is missing", exception.getMessage());
    }

    @Test
    public void testParseConfig_TooManyArguments() {
        var app = new App();

        // Test with too many arguments (should ignore extras)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("10", "true", "api-key", "extra-arg");
        });
        assertEquals("One of the required arguments is missing", exception.getMessage());
    }

    @Test
    public void testParseConfig_InvalidMessageLimit() {
        var app = new App();

        // Test with non-integer message limit
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("abc", "true", "test-key");
        });
        assertTrue(exception.getMessage().contains("must be a positive integer"));

        // Test with zero message limit
        exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("0", "true", "test-key");
        });
        assertEquals("chat-message-limit must be a positive integer, but got 0", exception.getMessage());

        // Test with negative message limit
        exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("-5", "true", "test-key");
        });
        assertEquals("chat-message-limit must be a positive integer, but got -5", exception.getMessage());
    }

    @Test
    public void testParseConfig_InvalidLogRequests() {
        var app = new App();

        // Test with invalid logRequests value
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("10", "yes", "test-key");
        });
        assertEquals("log-requests must be either 'true' or 'false', but got 'yes'", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("10", "1", "test-key");
        });
        assertEquals("log-requests must be either 'true' or 'false', but got '1'", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            app.parseConfig("10", "", "test-key");
        });
        assertEquals("log-requests must be either 'true' or 'false', but got ''", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 5, 10, 100, Integer.MAX_VALUE })
    public void testParseConfig_ValidMessageLimits(int limit) {
        var app = new App();
        assertEquals(new App.Config(limit, true, "test-key"), app.parseConfig(String.valueOf(limit), "true", "test-key"));
    }
}
