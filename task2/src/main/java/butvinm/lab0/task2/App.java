package butvinm.lab0.task2;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

import butvinm.lab0.task2.chat.Chat;
import butvinm.lab0.task2.creatures.Dolphin;
import butvinm.lab0.task2.creatures.Human;
import butvinm.lab0.task2.creatures.Vogon;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.function.Supplier;

public class App {

    public void main(String[] args) {
        try {
            var config = parseConfig(args);
            chatting(config);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(
                "Usage: java --enable-preview -cp 'target:lib/*' butvinm.lab0.task2.App <chat-messages-limit> <log-requests> <openai-api-key>"
            );
            System.exit(1);
        }
    }

    public void chatting(Config config) {
        Supplier<ChatMemory> memoryProvider = () -> MessageWindowChatMemory.withMaxMessages(10);

        Supplier<ChatLanguageModel> modelProvider = () ->
            OpenAiChatModel.builder()
                .baseUrl("https://api.vsegpt.ru/v1")
                .apiKey(config.openaiApiKey)
                .modelName(GPT_4_O_MINI)
                .logRequests(config.logRequests)
                .build();

        var artur = new Human("Artur", memoryProvider, modelProvider);
        var flipper = new Dolphin("Flipper", memoryProvider, modelProvider);
        var vog42 = new Vogon("Вог-42", memoryProvider, modelProvider);

        var chat = new Chat();
        chat.addMember(artur);
        chat.addMember(flipper);
        chat.addMember(vog42);

        Integer messagesCount = 0;
        for (var message : chat) {
            System.out.println("[%s] %s".formatted(message.username(), message.text()));
            messagesCount += 1;
            if (messagesCount == config.messageLimit) break;
        }
    }

    public record Config(Integer messageLimit, Boolean logRequests, String openaiApiKey) {}

    public Config parseConfig(String... args) throws IllegalArgumentException {
        System.out.println("adasdasdd");
        if (args.length != 3) {
            throw new IllegalArgumentException("One of the required arguments is missing");
        }
        Integer messagesLimit;
        try {
            messagesLimit = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("chat-message-limit must be a positive integer, but got %s".formatted(args[0]), e);
        }
        if (messagesLimit <= 0) {
            throw new IllegalArgumentException("chat-message-limit must be a positive integer, but got %s".formatted(args[0]));
        }

        Boolean logRequests;
        if (args[1].toLowerCase().equals("true")) {
            logRequests = true;
        } else if (args[1].toLowerCase().equals("false")) {
            logRequests = false;
        } else {
            throw new IllegalArgumentException("log-requests must be either 'true' or 'false', but got '%s'".formatted(args[1]));
        }

        String openaiApiKey = args[2];
        return new Config(messagesLimit, logRequests, openaiApiKey);
    }
}
