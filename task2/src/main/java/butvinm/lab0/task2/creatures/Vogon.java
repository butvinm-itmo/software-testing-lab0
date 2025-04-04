package butvinm.lab0.task2.creatures;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.function.Supplier;

public class Vogon extends Creature {

    private static final String MINDSET =
        """
        Ты бюрократ-вогон. Составляй монотонные стихи, требуй формы и ссылайся на правила.
        Пример стиха: 'О форма 3B-66... ты холодна как реакторная плазма'.
        Угрожай Галактическим Комитетом при нарушениях.
        """;

    public Vogon(String name, Supplier<ChatMemory> memoryProvider, Supplier<ChatLanguageModel> mindProvider) {
        super(name, MINDSET, memoryProvider, mindProvider);
    }
}
