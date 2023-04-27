import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;

/**
 * The ServerController class handles the server-side interface of application.
 * It contains UI components like ListView, ToggleButton, and methods to start,
 * stop, and update the server.
 */
public class ServerController {

    @FXML
    public ListView<String> serverListView;

    @FXML
    public ToggleButton serverToggleButton;

    public Server serverConnection;
    public ObservableList<String> clientList; // ObservableList for the clients

    /**
     * Initializes the ServerController, setting up the UI components and actions.
     */
    @FXML
    public void initialize() {
        clientList = FXCollections.observableArrayList();        // Initialize the ObservableList
        serverListView.setItems(clientList);                     // Set the ListView to the ObservableList
        serverToggleButton.setSelected(false);                   // Set the ToggleButton to OFF
        serverToggleButton.setOnAction(event -> toggleServer()); // Set the action for the ToggleButton
    }

    /**
     * Toggles the server state (ON/OFF) and updates the UI accordingly.
     */
    public void toggleServer() {
        if (serverToggleButton.isSelected()) {
            startServer();
            serverToggleButton.setText("Server ON");
        } else {
            stopServer();
            serverToggleButton.setText("Server OFF");
        }
    }

    /**
     * Starts the server and initializes the connection.
     */
    public void startServer() {
        serverConnection = new Server(this, data -> { // data is the info from the client
            /**
             * Here, a Server object is created with a lambda function that will be executed as
             * the callback in the Server class. This lambda function is called every time the
             * server receives a new message from the clients.
             *
             * So, while startServer() is called only once, the lambda function containing the
             * code to update the clientList will be executed multiple times throughout the lifetime
             * of the server whenever there's an update (e.g., a new client connecting or disconnecting).
             */
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

    /**
     * Stops the server, closes the connection, and resets the user count.
     */
    public void stopServer() {
        if (serverConnection != null) {
            serverConnection.close();
            serverConnection = null;
        }
        clientList.clear();
        serverConnection.count = 0;
    }

    /**
     * Adds a message to the serverListView UI component.
     *
     * @param message The message to be added to the list
     */
    public void addMessage(String message) {
        serverListView.getItems().add(message);
    }

    /**
     * Updates the user list in the UI with a new user.
     *
     * @param newUser The new user to be added to the list
     */
    public void updateUserList(User newUser) {
        Platform.runLater(() -> {
            clientList.add(newUser.toString());
        });
    }

}
