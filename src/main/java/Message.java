import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    private final User sender;
    private final List<User> recipients;
    private final String content;

    public Message(User sender, List<User> recipients, String content) {
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", recipients=" + recipients +
                ", content='" + content + '\'' +
                '}';
    }
}
