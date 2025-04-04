package butvinm.lab0.task2.creatures;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;

import butvinm.lab0.task2.chat.ChatMember;
import butvinm.lab0.task2.chat.Message;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.HashMap;
import java.util.function.Supplier;

public class Creature implements ChatMember {

    private final String name;

    private final String username;
    
    private final ChatMemory memory;

    private final ChatLanguageModel mind;

    public Creature(String name, String mindset, Supplier<ChatMemory> memoryProvider, Supplier<ChatLanguageModel> mindProvider) {
        this.name = name;
        this.username = createUsername(name);
        this.mind = mindProvider.get();
        this.memory = initMemory(name, mindset, memoryProvider);
    }

    private ChatMemory initMemory(String name, String mindset, Supplier<ChatMemory> memoryProvider) {
        var memory = memoryProvider.get();
        var prompt = "Тебя зовут %s.\n%s".formatted(this.name, mindset);
        memory.add(systemMessage(prompt));
        return memory;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String chat() {
        var answer = mind.chat(this.memory.messages()).aiMessage();
        memory.add(answer);
        return answer.text();
    }

    @Override
    public void remember(Message message) {
        memory.add(userMessage(message.username(), message.text()));
    }

    private String createUsername(String name) {
        // prettier-ignore
        Character[] cyrillic = {
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
            'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',
        };
        // prettier-ignore
        String[] latin = {
            "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "y", "", "e", "ju", "ja", 
            "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sch", "", "Y", "", "E", "Ju", "Ja",
        };
        assert(cyrillic.length == latin.length);
        
        var translationMap = new HashMap<Character, String>();
        for (int i = 0; i < cyrillic.length; i++) {
            translationMap.put(cyrillic[i], latin[i]);
        }
        
        StringBuilder builder = new StringBuilder();
        for (Character c : name.toCharArray()) {
            if (translationMap.containsKey(c)) {
                builder.append(translationMap.get(c));
            } else if (String.valueOf(c).matches("[\\w-]")) {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        return builder.toString();
    }
}
