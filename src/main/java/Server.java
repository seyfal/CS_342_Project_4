import javafx.application.Platform;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Server class handles the server side of the chat application.
 * It maintains a list of connected clients, manages message routing
 * between clients, and provides methods for starting and stopping
 * the server. The class contains inner classes TheServer and ClientThread.
 *
 * TheServer is the main server thread responsible for accepting incoming
 * client connections. When a connection is accepted, a new ClientThread
 * is created and added to the list of clients.
 *
 * ClientThread represents the thread responsible for handling individual
 * client connections. It processes incoming messages and routes them to
 * the appropriate recipients. The handleMessage method sends the message
 */
public class Server {

	static int DEBUG = 1; // Set to 1 to enable debug messages

	ArrayList<ClientThread> clients = new ArrayList<>(); // List of connected clients (ClientThread objects)
	private List<User> users = new ArrayList<>(); 		 // List of users connected to the server
	private final Consumer<Serializable> callback; 		 // Callback function for UI updates
	private ServerController serverController; 			 // The ServerController instance for UI updates

	TheServer server; 									 // The main server thread
	int count = 1;	 									 // The client count

	/**
	 * Constructs a Server instance with the given callback function.
	 *
	 * @param serverController The ServerController instance for UI updates.
	 * @param call             The callback function to be called when data is received.
	 */
	public Server(ServerController serverController, Consumer<Serializable> call) {
		this.serverController = serverController;
		callback = call;
		server = new TheServer();
		server.start();
	}

	/**
	 * Closes the server's socket.
	 */
	public void close() {
		try {
			server.mysocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TheServer is an inner class representing the main server thread responsible for
	 * accepting incoming client connections.
	 */
	public class TheServer extends Thread {

		ServerSocket mysocket;

		/**
		 * The main server loop, accepting client connections and creating ClientThread instances
		 * to handle each connected client.
		 */
		public void run() {

			try {
				// Create a new ServerSocket instance on port 5555
				mysocket = new ServerSocket(5555);

				// Update the Terminal
				System.out.println("Server is waiting for a client!");

				// Accept incoming client connections
				while (true) {
					// Create a new ClientThread instance for the connected client
					ClientThread c = new ClientThread(mysocket.accept(), count);

					// Update the UI using the callback function
					callback.accept("client has connected to server: " + "client #" + count);

					// Add the ClientThread instance to the list of clients
					clients.add(c);

					// Start the ClientThread instance
					c.start();

					// Increment the client count
					count++;
				}
			} catch (Exception e) {
				callback.accept("Server socket did not launch");
			}
		}
	}

	/**
	 * ClientThread is an inner class representing the thread responsible for handling
	 * individual client connections, processing incoming messages, and routing messages
	 * to the appropriate recipients.
	 *
	 */
	class ClientThread extends Thread {

		Socket connection;  		// The client connection ( Socket instance )
		int count; 		   			// The client count
		ObjectInputStream in;
		ObjectOutputStream out;
		User user;

		/**
		 * Constructs a ClientThread instance with the given connection and client count.
		 *
		 * @param s     The Socket instance representing the client connection.
		 * @param count The client count.
		 */
		ClientThread(Socket s, int count) {
			this.connection = s;
			this.count = count;
		}

		/**
		 * Handles routing messages to the appropriate recipients.
		 *
		 * @param message The Message instance to be sent.
		 */
		public void handleMessage(Message message) {
			for (ClientThread client : clients) {
				if (message.getRecipients() == null || message.getRecipients().contains(client.user)) {
					try {
						client.out.writeObject(message);

						 if (DEBUG == 1) {
						 	System.out.println("Message sent to " + client.user.toString());
						 }

					} catch (Exception e) {
						System.out.println("Error in handleMessage");
					}
				}
				// other options
			}
		}

		public void handleUser(List<User> users) {
			// create an instance of UserManager
			UserManager userManager = new UserManager(users);
			// send the list of users to the client
			try {
				out.writeObject(userManager);
			} catch (Exception e) {
				System.out.println("Error in handleUser");
			}
		}

		/**
		 * The main loop for the ClientThread, processing incoming messages and handling
		 * client disconnections.
		 */
		public void run() {

			try {
				// Create input stream for the client connection
				in = new ObjectInputStream(connection.getInputStream());

				// Read the user object sent by the client
				try {
					user = (User) in.readObject();
					// Update the list of users
					users.add(user);
					// Add this line after adding the new user to the list of users
				} catch (Exception e) {
					System.out.println("Error reading user object from client");
				}

				// Create output stream for the client connection
				out = new ObjectOutputStream(connection.getOutputStream());
			} catch (Exception e) {
				System.out.println("Streams not open");
			}

			// Update the UI using the callback function
			callback.accept("client has connected to server: " + user);

			// Add the ClientThread instance to the list of clients
			clients.add(this);

			// Increment the client count
			count++;

			while (true) {
				try {
					// Read the message object sent by the client
					Message message = (Message) in.readObject();

					// Handle the message including routing it to the appropriate recipients and sending
					handleMessage(message);

					// handle sending the updated list of users to all clients
					handleUser(users);

					// Send the message to the UI using the callback function
					Platform.runLater(() -> {
						serverController.addMessage(message.toString());
					});
				} catch (Exception e) {
					// Update the UI using the callback function
					callback.accept("OOOPs...Something wrong with the socket from client: " + user + "....closing down!");

					// Remove the ClientThread instance from the list of clients
					clients.remove(this);

					// Remove the user from the list of users
					users.remove(user);
					break;
				}
			}
		}
	}
}
