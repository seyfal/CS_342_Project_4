// Import required packages
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

// Client class extending Thread to run in a separate thread

/*
This is a Client class that represents a client in a client-server chat application.
The class extends Thread, allowing it to run concurrently with other parts of the program.
The client connects to a server, receives messages from the server, and sends messages to
the server. A callback function is used to handle incoming messages.
*/
public class Client extends Thread {

	// Declare instance variables
	Socket socketClient; // Socket for the client
	ObjectOutputStream out; // Output stream for sending data
	ObjectInputStream in; // Input stream for receiving data
	private Consumer<Serializable> callback; // Callback function for receiving messages

	// Constructor taking a callback function as an argument
	Client(Consumer<Serializable> call) {
		callback = call;
	}

	// Run method is called when the thread starts
	public void run() {
		try {
			// Connect to the server at IP "127.0.0.1" and port 5555
			socketClient = new Socket("127.0.0.1", 5555);

			// Initialize ObjectOutputStream and ObjectInputStream for sending and receiving data
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());

			// Set TCP No Delay for faster response times
			socketClient.setTcpNoDelay(true);
		} catch (Exception e) {
			System.out.println("Error in Client");
		}

		// Keep running and listening for incoming messages
		while (true) {
			try {
				// Read the message from the server and convert it to a String
				String message = in.readObject().toString();

				// This is where we handle the message from the server

				// Call the callback function with the received message
				callback.accept(message);
			} catch (EOFException | SocketException e) {
				// Handle disconnection from the server
				System.out.println("Client disconnected");
				break;
			} catch (Exception e) {
				// Handle other exceptions
				System.out.println("Error in Client");
			}
		}
	}

	// Close the socket connection
	public void close() {
		try {
			socketClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Send a message to the server
	public void send(String data) {
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// The commented code below would be used for broadcasting client list updates
	// (if it were part of the Client class and not the Server class)
    /*
    public void broadcastClientList() {
        ArrayList<String> clientList = new ArrayList<>();
        for (Server.ClientThread client : clients) {
            clientList.add("Client #" + client.count);
        }

        for (Server.ClientThread client : clients) {
            try {
                client.out.writeObject(clientList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    */
}
