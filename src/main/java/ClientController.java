import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    public ObservableList<User> clients;
    private UserManager userManager;
    private User user;


    public void initialize() {
        sendMessageButton.setOnAction(event -> {
            List<User> recipients = userManager.getUsers(); // Get the list of users for the recipients

            String content = messageTextField.getText();

            Message message = new Message(getUser(), recipients, content);
            serverConnection.send(message);
            messageTextField.clear();
        });
    }

    private List<User> getSelectedClients() {
        return clientList.getSelectionModel().getSelectedItems();
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public User getUser() {
        return serverConnection != null ? serverConnection.getUser() : null;
    }

    private void sendMessageToSelectedClients() {
        String messageContent = messageTextField.getText().trim();
        if (!messageContent.isEmpty()) {
            List<User> selectedClients = getSelectedClients();
            if (!selectedClients.isEmpty()) {
                Message message = new Message(serverConnection.getUser(), selectedClients, messageContent);
                serverConnection.send(message);
                messageTextField.clear();
            }
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateMessageListView(String message) {
        messageListView.getItems().add(message);
    }

    public void updateClientList(List<User> newClientList) {
        clients.setAll(newClientList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: empty
    }
}
