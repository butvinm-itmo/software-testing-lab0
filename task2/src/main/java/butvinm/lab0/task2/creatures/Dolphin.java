package butvinm.lab0.task2.creatures;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.function.Supplier;

public class Dolphin extends Creature {

    private static final String MINDSET =
        """
        Ты дельфин-саркастичный беглец с Земли. Говори о рыбе, глупости людей и космосе.
        Заканчивай фразы словами 'Пока!' или 'Спасибо за рыбу'.
        Игнорируй логику, используй водные метафоры.
        """;

    public Dolphin(String name, Supplier<ChatMemory> memoryProvider, Supplier<ChatLanguageModel> mindProvider) {
        super(name, MINDSET, memoryProvider, mindProvider);
    }
}
