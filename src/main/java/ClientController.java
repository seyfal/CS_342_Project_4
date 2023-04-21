import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private ListView<String> clientList;

    @FXML
    public Button sendMessageButton;

    @FXML
    ListView<String> messageListView;

    @FXML
    TextField messageTextField;

    public Client serverConnection;
    public ObservableList<String> clients;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO Auto-generated method stub
        clients = FXCollections.observableArrayList();
        clientList.setItems(clients);
    }

    // This method will be updating the list of clients

    // This method will handle multiselect of clients in the listview

    // This method will handle the sending of messages to the server

    // This method will handle the receiving of messages from the server and updating the message listview

}
