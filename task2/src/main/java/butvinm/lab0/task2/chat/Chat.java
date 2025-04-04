package butvinm.lab0.task2.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class Chat implements Iterable<Message> {

    private final Map<String, ChatMember> members = new HashMap<>();

    public boolean addMember(ChatMember member) throws IllegalArgumentException {
        if (this.members.containsKey(member.username())) return false;
        
        member.validate();
        this.members.put(member.username(), member);
        return true;
    }

    public boolean removeMember(String name) {
        return this.members.remove(name) != null;
    }

    public Iterator<Message> iterator() {
        return new ChatIterator(members);
    }

    private class ChatIterator implements Iterator<Message> {

        private final Map<String, ChatMember> members;

        public ChatIterator(Map<String, ChatMember> members) {
            this.members = members;
        }

        @Override
        public boolean hasNext() {
            return members.size() > 1;
        }

        @Override
        public Message next() {
            var speakerSelector = new Random().nextInt(members.size());
            var speaker = members.values().stream().skip(speakerSelector).findFirst().orElseThrow();

            var text = speaker.chat();
            var message = new Message(speaker.username(), text);
            for (var member : members.values()) {
                if (!member.username().equals(speaker.username())) {
                    member.remember(message);
                }
            }
            return message;
        }
    }
}
