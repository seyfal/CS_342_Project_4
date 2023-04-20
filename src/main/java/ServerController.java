import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class ServerController {

    @FXML
    private ListView<String> serverListView;

    @FXML
    private ToggleButton serverToggleButton;

    private Server serverConnection;
    private ObservableList<String> clientList;

    @FXML
    public void initialize() {
        clientList = FXCollections.observableArrayList();
        serverListView.setItems(clientList);
        serverToggleButton.setSelected(false);
        serverToggleButton.setOnAction(event -> toggleServer());
    }

    private void toggleServer() {
        if (serverToggleButton.isSelected()) {
            startServer();
            serverToggleButton.setText("Server ON");
        } else {
            stopServer();
            serverToggleButton.setText("Server OFF");
        }
    }

    private void startServer() {
        serverConnection = new Server(data -> {
            Platform.runLater(() -> {
                String message = data.toString();
                if (message.startsWith("New client:")) {
                    clientList.add(message.substring(11));
                } else if (message.startsWith("Client disconnected:")) {
                    clientList.remove(message.substring(19));
                }
            });
        });
    }

    private void stopServer() {
        if (serverConnection != null) {
            serverConnection.close();
            serverConnection = null;
        }
        clientList.clear();
    }

    // Add any other controller logic here

}
