package butvinm.lab0.task2;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

class RetryExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        context
            .getTestMethod()
            .ifPresent(method -> {
                System.err.println("Retrying test method %s".formatted(method.getName()));
                context.getExecutableInvoker().invoke(method, context.getRequiredTestInstance());
            });
    }
}
