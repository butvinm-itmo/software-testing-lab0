package butvinm.lab0.task2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestApp {

    @Test
    void testParseConfig() {
        var app = new App();
        assertEquals(new App.Config(10, true, "test-key"), app.parseConfig("10", "true", "test-key"));
    }
}
