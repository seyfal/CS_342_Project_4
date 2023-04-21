import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServerController {

    @FXML
    public ListView<String> serverListView;

    @FXML
    public ToggleButton serverToggleButton;

    public Server serverConnection;
    public ObservableList<String> clientList;

    @FXML
    public void initialize() {
        clientList = FXCollections.observableArrayList();
        serverListView.setItems(clientList);
        serverToggleButton.setSelected(false);
        serverToggleButton.setOnAction(event -> toggleServer());
    }

    public void toggleServer() {
        if (serverToggleButton.isSelected()) {
            startServer();
            serverToggleButton.setText("Server ON");
        } else {
            stopServer();
            serverToggleButton.setText("Server OFF");
        }
    }

    public void startServer() {
        serverConnection = new Server(this, data -> {
            Platform.runLater(() -> {
                // add message "Server was started" to the listview
                serverListView.getItems().add("Server was started");

                // data is the info from the client
                String message = data.toString();

                if (message.startsWith("New client:")) {
                    clientList.add(message.substring(11));
                } else if (message.startsWith("Client disconnected:")) {
                    clientList.remove(message.substring(19));
                }
            });
        });
    }

    public void stopServer() {
        if (serverConnection != null) {
            serverConnection.close();
            serverConnection = null;
        }
        clientList.clear();
    }

    public void addMessage(String message) {
        serverListView.getItems().add(message);
    }

    // Add any other controller logic here

}
