import java.io.Serializable;
import java.util.UUID;

/**
 * User class representing a user in the application. Each user has a unique
 * identifier (UUID) that is generated when a new user is created.
 */
public class User implements Serializable {
    private final UUID id; // Unique identifier for each user

    /**
     * Constructor creating a new user with a randomly generated UUID.
     */
    public User() {
        this.id = UUID.randomUUID(); // Generate a random UUID
    }

    /**
     * Copy constructor creating a new user with the same UUID as the given user.
     * @param user User object to copy the UUID from
     */
    public User(User user) {
        this.id = user.getId(); // Copy the UUID from the given user
    }

    public User(UUID id) {
        this.id = id;
    }

    /**
     * Getter for the user's UUID.
     * @return UUID of the user
     */
    public UUID getId() {
        return id;
    }

    // String to UUID conversion
    public static UUID stringToUUID(String uuid) {
        return UUID.fromString(uuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }
}
