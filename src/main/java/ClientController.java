import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The ClientController class manages the user interface for a chat application.
 * It handles events such as button clicks and list view selections, manages
 * message sending and receiving, and updates the user interface accordingly.
 */
public class ClientController implements Initializable {

    @FXML
    private ListView<User> clientList;

    @FXML
    public Button sendMessageButton;

    @FXML
    ListView<String> messageListView;

    @FXML
    TextField messageTextField;

    public Client serverConnection;
    public UserManager recipients = new UserManager();
    public UserManager currentUsers = new UserManager();
    private User user;

    /**
     * Initializes the controller and sets up event handlers for button clicks
     * and list view selections.
     */
    public void initialize() {
        sendMessageButton.setOnAction(event ->toggleButton());
        clientList.setOnMouseClicked(event ->toggleListView());
    }

    /**
     * Handles the selection of a user from the client list.
     */
    private void toggleListView() {
        User selectedUser = clientList.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            System.out.println("No user selected");
            return;
        }

        recipients.addUser(selectedUser);
        System.out.println("Selected items: " + selectedUser);
    }

    /**
     * Handles the sending of a message when the send button is clicked.
     */
    private void toggleButton() {
        List<User> recipients = this.recipients.getUsers(); // Get the list of users for the recipients

        String content = messageTextField.getText(); // Get the content of the message

        if (content.isEmpty()) {
            System.out.println("Message is empty");
            return;
        }

        Message message = new Message(user, recipients, content); // Create a new message
        System.out.println("Sending message: " + message);
        serverConnection.send(message);
        messageTextField.clear();
    }

    public void handle(Serializable data) {
        if (data instanceof Message) {
            Message message = (Message) data;

            // message to string
            String messageString = message.toString();

            // function that will update the UI
            updateMessageListView(messageString);

            // Or you can log the message for debugging purposes
            System.out.println("Received message: " + message);
        } else if (data instanceof UserManager){
            currentUsers = (UserManager) data;
            updateClientList(currentUsers.getUsers());
        } else {
            System.out.println("Received unknown object");
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateMessageListView(String message) {
        messageListView.getItems().add(message);
    }

    public void updateClientList(List<User> users) {
        ObservableList<User> observableList = FXCollections.observableArrayList(users);
        clientList.setItems(observableList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: empty
    }

}
