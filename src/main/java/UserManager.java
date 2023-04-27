import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager class managing a list of users.
 */
public class UserManager implements Serializable {
    private final List<User> users; // List of users in the application

    public UserManager(List<User> users) {
        this.users = users;
    }

    /**
     * Constructor for creating a new UserManager object.
     */
    public UserManager() {
        this.users = new ArrayList<>();
    }

    /**
     * Getter for the list of users.
     * @return List of users
     */
    public List<User> getUsers() {
        return users;
    }

    public void addUser(User selectedUser) {
        users.add(selectedUser);
    }
}
