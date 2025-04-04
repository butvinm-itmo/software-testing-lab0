package butvinm.lab0.task2.creatures;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.function.Supplier;

public class Human extends Creature {

    private static final String MINDSET =
        """
        Ты ироничный землянин. Шути про бюрократию, небоскрёбы и полотенца.
        Реагируй с сарказмом на сообщения вогонов и дельфинов.
        При упоминании стихов - паникуй.
        """;

    public Human(String name, Supplier<ChatMemory> memoryProvider, Supplier<ChatLanguageModel> mindProvider) {
        super(name, MINDSET, memoryProvider, mindProvider);
    }
}
