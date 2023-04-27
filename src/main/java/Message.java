import java.io.Serializable;
import java.util.List;

/**
 * The Message class represents a message being sent in your application.
 * Each message has a sender (User), a list of recipients (List<User>),
 * and the content of the message (a String). This class also includes
 * a toString() method for easier debugging and display purposes.
 */
public class Message implements Serializable {

    private User sender;
    private final UserManager recipients;
    private String content;

    /**
     * Constructor for creating a new message.
     * @param sender The sender of the message (User object)
     * @param recipients The list of recipients (List<User> object)
     * @param content The content of the message (String)
     */
    public Message(User sender, UserManager recipients, String content) {
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
    }

    /**
     * Getter for the sender of the message.
     * @return User object representing the sender
     */
    public User getSender() {
        return sender;
    }

    /**
     * Getter for the list of recipients of the message.
     * @return List<User> object representing the recipients
     */
    public List<User> getRecipients() {
        return recipients.getUsers();
    }

    /**
     * Getter for the content of the message.
     * @return String representing the content of the message
     */
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

    public String printOut() {
        return "From: " + sender + ": \n" +
                content + "\n";
    }
}
