//import java.util.HashMap;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;
//
//public class GuiServer extends Application {
//
//	TextField c1;
//	Button serverChoice, clientChoice, b1;
//	HashMap<String, Scene> sceneMap;
//	HBox buttonBox;
//	VBox clientBox;
//	Scene startScene;
//	BorderPane startPane;
//	Server serverConnection;
//	Client clientConnection;
//	ListView<String> listItems, listItems2;
//
//	public static void main(String[] args) {
//		launch(args);
//	}
//
//	@Override
//	public void start(Stage primaryStage) {
//		primaryStage.setTitle("The Networked Client/Server GUI Chat");
//		this.serverChoice = new Button("Server");
//		this.serverChoice.setStyle("-fx-pref-width: 300px");
//		this.serverChoice.setStyle("-fx-pref-height: 300px");
//
//		this.serverChoice.setOnAction(e -> {
//			primaryStage.setScene(sceneMap.get("server"));
//			primaryStage.setTitle("This is the Server");
//			serverConnection = new Server(data -> {
//				Platform.runLater(() -> {
//					listItems.getItems().add(data.toString());
//				});
//			});
//		});
//
//		this.clientChoice = new Button("Client");
//		this.clientChoice.setStyle("-fx-pref-width: 300px");
//		this.clientChoice.setStyle("-fx-pref-height: 300px");
//
//		this.clientChoice.setOnAction(e -> {
//			primaryStage.setScene(sceneMap.get("client"));
//			primaryStage.setTitle("This is a client");
//			clientConnection = new Client(data -> {
//				Platform.runLater(() -> {
//					listItems2.getItems().add(data.toString());
//				});
//			});
//			clientConnection.start();
//		});
//		this.buttonBox = new HBox(400, serverChoice, clientChoice);
//		startPane = new BorderPane();
//		startPane.setPadding(new Insets(70));
//		startPane.setCenter(buttonBox);
//		startScene = new Scene(startPane, 800, 800);
//		listItems = new ListView<>();
//		listItems2 = new ListView<>();
//		c1 = new TextField();
//		b1 = new Button("Send");
//		b1.setOnAction(e -> {
//			clientConnection.send(c1.getText());
//			c1.clear();
//		});
//		sceneMap = new HashMap<>();
//		sceneMap.put("server", createServerGui());
//		sceneMap.put("client", createClientGui());
//
//		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//			@Override
//			public void handle(WindowEvent t) {
//				if (serverConnection != null) {
//					serverConnection.close();
//				}
//				if (clientConnection != null) {
//					clientConnection.close();
//				}
//				Platform.exit();
//				System.exit(0);
//			}
//		});
//		primaryStage.setScene(startScene);
//		primaryStage.show();
//	}
//
//	public Scene createServerGui() {
//		BorderPane pane = new BorderPane();
//		pane.setPadding(new Insets(70));
//		pane.setStyle("-fx-background-color: coral");
//		pane.setCenter(listItems);
//		return new Scene(pane, 500, 400);
//	}
//
//	public Scene createClientGui() {
//		clientBox = new VBox(10, c1,b1,listItems2);
//		clientBox.setStyle("-fx-background-color: blue");
//		return new Scene(clientBox, 400, 300);
//	}
//
//}

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class GuiServer extends Application {

	Button serverChoice, clientChoice, b1;
	HBox buttonBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	ListView<String> listItems, listItems2;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("The Networked Client/Server GUI Chat");

		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");

		this.serverChoice.setOnAction(e -> {
			try {
				Parent serverRoot = FXMLLoader.load(getClass().getResource("ServerGUI.fxml"));
				listItems = (ListView<String>) serverRoot.lookup("#listItems");
				Scene serverScene = new Scene(serverRoot, 500, 400);
				primaryStage.setScene(serverScene);
				primaryStage.setTitle("Server");
				serverConnection = new Server(data -> {
					Platform.runLater(() -> {
						listItems.getItems().add(data.toString());
					});
				});
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");

		this.clientChoice.setOnAction(e -> {
			try {
				Parent clientRoot = FXMLLoader.load(getClass().getResource("ClientGUI_test.fxml"));
				listItems2 = (ListView<String>) clientRoot.lookup("#listItems2");
				TextField c1 = (TextField) clientRoot.lookup("#c1");
				Button b1 = (Button) clientRoot.lookup("#b1");

				b1.setOnAction(event -> {
					clientConnection.send(c1.getText());
					c1.clear();
				});

				Scene clientScene = new Scene(clientRoot, 400, 300);
				primaryStage.setScene(clientScene);
				primaryStage.setTitle("This is a client");
				clientConnection = new Client(data -> {
					Platform.runLater(() -> {
						listItems2.getItems().add(data.toString());
					});
				});
				clientConnection.start();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		startScene = new Scene(startPane, 800, 800);

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
