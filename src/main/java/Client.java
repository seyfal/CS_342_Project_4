import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread{

	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call){
		callback = call;
	}

	public void run() {

		try {
			socketClient= new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {
			System.out.println("Error in Client");
		}

		while (true) {
			try {
				String message = in.readObject().toString();
				callback.accept(message);
			} catch (EOFException | SocketException e) {
				System.out.println("Client disconnected");
				break;
			} catch (Exception e) {
				System.out.println("Error in Client");
			}
		}

	}

	public void close() {
		try {
			socketClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String data) {
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
