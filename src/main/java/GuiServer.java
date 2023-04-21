
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class GuiServer extends Application {

	Button serverChoice, clientChoice;
	HBox buttonBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	ListView<String> listItems;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Pick");

		// Create the serverChoice button
		this.serverChoice = new Button();
		this.serverChoice.setMinSize(60, 60);
		this.serverChoice.setMaxSize(60, 60);

		// Set the style class for the serverChoice button
		serverChoice.getStyleClass().add("button");

		// Set the graphic for the serverChoice button
		Image serverImage = new Image(getClass().getResourceAsStream("server.png"));
		ImageView serverImageView = new ImageView(serverImage);
		serverImageView.setFitWidth(50); // Set the desired width
		serverImageView.setFitHeight(50); // Set the desired height
		serverChoice.setGraphic(serverImageView);

		// Handle the serverChoice button action
		this.serverChoice.setOnAction(e -> {
			try {
				// load the scene graph from the FXML file
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerGUI.fxml"));

				// serverRoot is the root of the scene graph
				Parent serverRoot = loader.load();

				// serverController is the controller of the scene graph
				ServerController serverController = loader.getController();

				// listItems is the ListView in the scene graph
				listItems = serverController.serverListView;

				// serverScene is the scene graph
				Scene serverScene = new Scene(serverRoot, 600, 600);

				// set the scene and title
				primaryStage.setScene(serverScene);
				primaryStage.setTitle("Server");

				// create the server connection
				serverConnection = new Server(serverController, data -> {
					// update the ListView on the JavaFX Application Thread
					Platform.runLater(() -> {
						// add the data to the ListView
						listItems.getItems().add(data.toString());
					});
				});

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		// Create the clientChoice button
		this.clientChoice = new Button();
		this.clientChoice.setMinSize(60, 60);
		this.clientChoice.setMaxSize(60, 60);

		// Set the style class for the clientChoice button
		clientChoice.getStyleClass().add("button");

		// Set the graphic for the clientChoice button
		Image clientImage = new Image(getClass().getResourceAsStream("client.png"));
		ImageView clientImageView = new ImageView(clientImage);
		clientImageView.setFitWidth(50); // Set the desired width
		clientImageView.setFitHeight(50); // Set the desired height
		clientChoice.setGraphic(clientImageView);

		// Handle the clientChoice button action
		this.clientChoice.setOnAction(e -> {
			try {
				// load the scene graph from the FXML file
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientGUI_test.fxml"));

				// clientRoot is the root of the scene graph
				Parent clientRoot = loader.load();

				// clientController is the controller of the scene graph
				ClientController clientController = loader.getController();

				// handle send button action
//				clientController.sendMessageButton.setOnAction(event -> {
//					clientController.serverConnection.send(clientController.messageTextField.getText());
//					clientController.messageTextField.clear();
//				});

				// clientScene is the scene graph
				Scene clientScene = new Scene(clientRoot, 517, 669);

				// set the scene and title
				primaryStage.setScene(clientScene);
				primaryStage.setTitle("This is a client");

				clientController.serverConnection = new Client(data -> {
					Platform.runLater(() -> {
						clientController.messageListView.getItems().add(data.toString());
					});
				});

				clientController.serverConnection.start();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		this.buttonBox = new HBox(50, serverChoice, clientChoice);
		startPane = new BorderPane();

		startPane.setStyle("-fx-background-color: #ffffff;");
		buttonBox.setStyle("-fx-background-color: #ffffff;");

		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		BorderPane.setAlignment(buttonBox, javafx.geometry.Pos.CENTER);
		BorderPane.setMargin(buttonBox, new Insets(50));
		startScene = new Scene(startPane, 400, 350);

		// Load the CSS file
		startScene.getStylesheets().add(getClass().getResource("GuiServer.css").toExternalForm());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				if (serverConnection != null) {
					serverConnection.close();
				}
				if (clientConnection != null) {
					clientConnection.close();
				}
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(startScene);
		primaryStage.show();
	}
}
