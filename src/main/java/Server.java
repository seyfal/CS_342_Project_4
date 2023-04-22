import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {

	ArrayList<ClientThread> clients = new ArrayList<>();
	private final Consumer<Serializable> callback;
	private ServerController serverController;

	TheServer server;
	int count = 1;

	public Server(ServerController serverController, Consumer<Serializable> call) {
		this.serverController = serverController;
		callback = call;
		server = new TheServer();
		server.start();
	}

	public void close() {
		try {
			server.mysocket.close();
		} catch (Exception e) {
			// Handle the exception
		}
	}

	public class TheServer extends Thread {

		ServerSocket mysocket;

		public void run() {

			try {
				mysocket = new ServerSocket(5555);

				System.out.println("Server is waiting for a client!");

				while (true) {
					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();
					count++;
				}
			} catch (Exception e) {
				callback.accept("Server socket did not launch");
			}
		}
	}

	class ClientThread extends Thread {

		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
		User user;

		ClientThread(Socket s, int count) {
			this.connection = s;
			this.count = count;
		}

		public void handleMessage(Message message) {
			for (ClientThread client : clients) {
				if (message.getRecipients() == null || message.getRecipients().contains(client.user)) {
					try {
						client.out.writeObject(message);
					} catch (Exception e) {
						System.out.println("Error in handleMessage");
					}
				}
			}
		}

		public void run() {

			try {
				in = new ObjectInputStream(connection.getInputStream());
				try {
					user = (User) in.readObject(); // Read the user object sent by the client
				} catch (Exception e) {
					System.out.println("Error reading user object from client");
				}
				out = new ObjectOutputStream(connection.getOutputStream());
			} catch (Exception e) {
				System.out.println("Streams not open");
			}

			callback.accept("client has connected to server: " + user);
			clients.add(this);
			count++;

			while (true) {
				try {
					Message message = (Message) in.readObject();
					handleMessage(message);
					Platform.runLater(() -> {
						serverController.addMessage(message.toString());
					});
				} catch (Exception e) {
					callback.accept("OOOPs...Something wrong with the socket from client: " + user + "....closing down!");
					clients.remove(this);
					break;
				}
			}
		}
	}
}
