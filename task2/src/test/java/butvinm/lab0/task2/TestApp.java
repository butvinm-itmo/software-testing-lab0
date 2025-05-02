package butvinm.lab0.task2;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RetryExtension.class)
public class TestApp {
    static final Supplier<Stream<Arguments>> testParseConfig_ValidInputs = () ->
        Stream.of(
            arguments(new App.Config(1, true, "key0"), new String[] { "1", "TrUe", "key0" }),
            arguments(new App.Config(2, false, "key1"), new String[] { "2", "FaLsE", "key1" })
        );

    @ParameterizedTest
    @FieldSource
    public void testParseConfig_ValidInputs(App.Config expected, String[] args) {
        var app = new App();
        assertEquals(expected, app.parseConfig(args));
    }

    @Test
    public void testParseConfig_BadNumberOfArguments() {
        var app = new App();

        var exception = assertThrows(IllegalArgumentException.class, () -> app.parseConfig());
        assertEquals("Unexpected number of arguments, expect exactly 3", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> app.parseConfig("_", "_", "_", "_"));
        assertEquals("Unexpected number of arguments, expect exactly 3", exception.getMessage());
    }

    @Test
    public void testParseConfig_InvalidMessageLimit() {
        var app = new App();

        var exception = assertThrows(IllegalArgumentException.class, () -> app.parseConfig("NaN", "_", "_"));
        assertEquals("chat-message-limit must be a positive integer, but got 'NaN'", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> app.parseConfig("0", "_", "_"));
        assertEquals("chat-message-limit must be a positive integer, but got '0'", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> app.parseConfig("-1", "_", "_"));
        assertEquals("chat-message-limit must be a positive integer, but got '-1'", exception.getMessage());
    }

    @Test
    public void testParseConfig_InvalidLogRequests() {
        var app = new App();

        var exception = assertThrows(IllegalArgumentException.class, () -> app.parseConfig("1", "NaB", "_"));
        assertEquals("log-requests must be either 'true' or 'false', but got 'NaB'", exception.getMessage());
    }
}
