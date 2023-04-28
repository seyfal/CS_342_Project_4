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

	ArrayList<ClientThread> clients = new ArrayList<>(); // List of connected clients (ClientThread objects)
	private List<User> users = new ArrayList<>();          // List of users connected to the server
	private final Consumer<Serializable> callback;          // Callback function for UI updates
	private ServerController serverController;              // The ServerController instance for UI updates

	TheServer server;                                      // The main server thread
	int count = 1;                                          // The client count

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
	Boolean containsUser(Message message, User user) {
		for (User u : message.getRecipients()) {
			if (u.getId().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * ClientThread is an inner class representing the thread responsible for handling
	 * individual client connections, processing incoming messages, and routing messages
	 * to the appropriate recipients.
	 *
	 */
	class ClientThread extends Thread {

		Socket connection;          // The client connection ( Socket instance )
		int count;                        // The client count
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
		public synchronized void handleMessage(Message message) {
			// System.out.println("handleMessage called");
			List<User> recipients = message.getRecipients(); // Get the list of recipients from the UserManager
			System.out.println("Recipients: " + recipients);
			User sender = message.getSender(); // Get the sender of the message
			// System.out.println("Sender: " + sender);
			for (ClientThread client : clients) {
				// System.out.println("Checking client: " + client.user.toString());
				// TODO - Fix this!!!!!
				 if (!client.user.equals(sender) && (recipients == null || containsUser(message, client.user))){
					try {
						// Flush the stream
						client.out.flush();
						// Reset the ObjectOutputStream for the client
						client.out.reset();
						// Send the message to the client
						client.out.writeObject(message);
						// Update the Terminal
						// System.out.println("Message sent to " + client.user.toString());
					} catch (Exception e) {
						System.out.println("Error in handleMessage: " + e.getMessage());
					}
				 } else {
					 System.out.println("Message not intended for client: " + client.user.toString());
				 }
			}
		}


		public synchronized void handleUser(List<User> users) {
			// create an instance of UserManager
			UserManager userManager = new UserManager(users);
			// send the list of users to the client
			try {
				System.out.println("Sending user list to client: " + userManager);
				// Flush the stream
				out.flush();
				// Reset the ObjectOutputStream
				out.reset();
				// send the list of users to the client
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

			// Increment the client count
			count++;

			// handle sending the updated list of users to all clients
			for (ClientThread client : clients) {
				client.handleUser(users);
			}

			while (true) {
				try {
					// Read the message object sent by the client
					Message message = (Message) in.readObject();

					// TODO
					System.out.println("Received message on server: " + message.toString());

					// Handle the message including routing it to the appropriate recipients and sending
					handleMessage(message);

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

					// Remove the user from the list of users
					for (ClientThread client : clients) {
						client.handleUser(users);
					}

					// Close resources when the client disconnects
					closeResources();
					break;
				}
			}
		}

		// Method to close resources
		private void closeResources() {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				System.out.println("Error closing resources: " + e.getMessage());
			}
		}
	}
}
