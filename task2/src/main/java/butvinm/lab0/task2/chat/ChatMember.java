package butvinm.lab0.task2.chat;

public interface ChatMember {
    String username();

    String chat();

    void remember(Message message);

    default void validate() throws IllegalArgumentException {
        if (!this.username().matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException(
                "Username must match the pattern '^[a-zA-Z0-9_-]+$', but got '%s'".formatted(this.username())
            );
        }
    }
}
