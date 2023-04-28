import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The ClientController class manages the user interface for a chat application.
 * It handles events such as button clicks and list view selections, manages
 * message sending and receiving, and updates the user interface accordingly.
 */
public class ClientController {

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
        clientList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        clientList.setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item.toString());
                        if (clientList.getSelectionModel().getSelectedItems().contains(item)) {
                            setStyle("-fx-control-inner-background: #007bff; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                clientList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (clientList.getSelectionModel().getSelectedIndices().contains(index)) {
                        clientList.getSelectionModel().clearSelection(index);
                        recipients.removeUser(cell.getItem());
                    } else {
                        clientList.getSelectionModel().select(index);
                        recipients.addUser(cell.getItem());
                    }
                    event.consume();
                    System.out.println("Current recipients list: " + recipients.getUsers());
                }
            });

            return cell;
        });

    }

    /**
     * Handles the sending of a message when the send button is clicked.
     */

    private void toggleButton() {
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

    /**
     * Handles received data, such as messages or user lists, and updates the
     * user interface accordingly.
     */
    public void handle(Serializable data) {
        if (data instanceof Message) {
            Message message = (Message) data;

            // message to string
            String messageString = message.printOut();

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

    /**
     * Sets the current user of the application.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Updates the message list view with the provided message.
     */
    public void updateMessageListView(String message) {
        messageListView.getItems().add(message);
    }

    /**
     * Updates the client list view with the provided list of users.
     */
    public void updateClientList(List<User> users) {
        // don't show the current user in the list
        users.remove(user);
        ObservableList<User> observableList = FXCollections.observableArrayList(users);
        clientList.setItems(observableList);
    }

}
