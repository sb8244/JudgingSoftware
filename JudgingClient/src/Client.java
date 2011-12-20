import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import packet.Packet;
import packet.ProblemPacket;
import packet.SourcePacket;
import packet.ResultPacket;
import packet.results.Result;
import java.util.ArrayList;

/**
 * Holds the Socket for the client/server connection and processes packets
 * @author Stephen Bussey
 *
 */
public class Client
{
	private Socket clientSocket;
	private PrintWriter out;
	private ClientThread ct;
	private ArrayList<Observer> observers = new ArrayList<Observer>(); 

	/**
	 * Create a new Client given a host name, port number, and parent to report to
	 * @param host The host name
	 * @param port The port number
	 * @param obs An observer that wants updates when packets are received
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client(String host, int port, Observer obs) throws UnknownHostException, IOException
	{
		clientSocket = new Socket(InetAddress.getByName(host), port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		ct = new ClientThread(clientSocket);
		observers.add(obs);
		ct.start();
	}
	
	/**
	 * 
	 * @return The Socket that is being used by this Client
	 */
	public Socket getClientSocket()
	{
		return clientSocket;
	}

	/**
	 * Update all the observers on the packet that was received
	 * @param p The Packet that was received
	 */
	protected void receivePacket(Packet p)
	{
		for(Observer o: observers)
			o.update(p);	
	}

	/**
	 * Send a packet to the server
	 * @param p The Packet to send
	 */
	public void sendPacket(Packet p)
	{
		Gson gson = new Gson();
		out.println(gson.toJson(p));
	}

	/**
	 * Keeps the client/server connection alive and reads from the Server
	 * @author Stephen Bussey
	 *
	 */
	class ClientThread extends Thread
	{
		Socket clientSocket;
		private Gson gson;

		public ClientThread(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
			gson = new Gson();
		}
		public void run()
		{
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String fromServer;
				//read from the server and process the packets
				while((fromServer = in.readLine()) != null)
				{
					Packet result = (Packet) gson.fromJson(fromServer, Packet.class);
					//System.out.println(result.type);
					Client.this.receivePacket(result.getPacket());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
