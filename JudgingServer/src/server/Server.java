package server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

import classRunner.ClassRunner;

import com.google.gson.Gson;

import packet.Packet;
import packet.ProblemPacket;
import packet.SourcePacket;
import packet.ResultPacket;
import packet.results.Result;

/**
 * Handles incoming requests and processes them
 * @author Stephen Bussey
 *
 */
public class Server
{
	public static final boolean DEBUG = false;
	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private ArrayList<Problem> problems;

	/**
	 * Create a new Server
	 * @param port The port to use for listening
	 * @param problemNames The names of all the problems in an array
	 * @throws IOException
	 */
	public Server(int port, String[] problemNames) throws IOException
	{
		serverSocket = new ServerSocket(port);
		serverThread = new ServerThread();
		problems = new ArrayList<Problem>();
		for(int i = 0; i < problemNames.length; i++)
		{
			//create each problem and add it to an ArrayList of Problems
			Problem problem = new Problem(problemNames[i]);
			if(problem.isValid())
				problems.add(problem);
		}
	}

	public void start()
	{
		serverThread.start();
	}

	public void stop()
	{
		serverThread.stopThis();
	}

	public boolean isAlive()
	{
		return serverThread.isAlive();
	}
	
	/**
	 * Internal class of Server that keeps a listening connection alive, and processes all requests
	 * @author Stephen Bussey
	 *
	 */
	class ServerThread extends Thread
	{
		private boolean shouldRun = true;
		private final int SLEEP_TIME = 100;
		public void run()
		{
			while(shouldRun)
			{
				try
				{
					Thread.sleep(SLEEP_TIME);
					//wait for connection
					Socket clientSocket = serverSocket.accept();
					//Start a thread to handle the connection
					ProcessClientThread pct = new ProcessClientThread(clientSocket);
					pct.start();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		public void stopThis()
		{
			shouldRun = false;
		}
	}

	/**
	 * This is where all the magic happens for actually grading a problem
	 * @author Stephen Bussey
	 *
	 */
	class ProcessClientThread extends Thread
	{
		private Socket clientSocket;
		private Gson gson;
		
		/**
		 * Create a new Thread 
		 * @param client Socket with a desired client connection
		 */
		public ProcessClientThread(Socket client)
		{
			clientSocket = client;
			gson = new Gson();
		}
		
		public void run()
		{
			//process client socket
			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String inputLine;
				//send the problem list to the client
				if(!clientSocket.isClosed() && clientSocket.isConnected())
				{
					ProblemPacket problemPacket = new ProblemPacket(Server.this.getProblemNames());
					out.println(gson.toJson(new Packet(problemPacket, null, "problem")));
					System.out.println("Packet sent: " + gson.toJson(new Packet(problemPacket, null, "problem")));
				}
				//accept submissions from the client 
				while(!clientSocket.isClosed() && clientSocket.isConnected() && (inputLine = in.readLine()) != null)
				{
					ResultPacket result = dumpPacket(gson.fromJson(inputLine, SourcePacket.class));
					out.println(gson.toJson(new Packet(null, result, "result")));
					System.out.println("Packet sent: " + gson.toJson(new Packet(null, result, "result")));
				}

			} catch (IOException e) {
				System.err.println("Client closed");
			}

		}

		/**
		 * Process a SourcePacket and get the Result of it
		 * @param p The SourcePacket received from JSON
		 * @return The ResultPacket that will be sent back to the client
		 * @throws IOException
		 */
		private ResultPacket dumpPacket(SourcePacket p) throws IOException
		{
			Result result = processFile(p);
			return new ResultPacket(result);
		}

		/**
		 * Given a SourcePacket, write the source to disc in TempFiles/ and process it
		 * @param p The SourcePacket to process
		 * @return The Result of running the Problem
		 */
		private Result processFile(SourcePacket p)
		{
			String className = p.getFileName();
			String appended = "";
			String fileName = className + "." + p.getLanguage();
			File f = new File("TempFiles//" + fileName);
			for(int i = 0; f.exists(); i++)
			{
				appended = i +"";
				fileName = className + appended +  "." + p.getLanguage();
				f = new File("TempFiles//" + fileName);
			}
			String source = p.getSource().replace(className, className + appended);
			try
			{
				FileWriter fstream = new FileWriter(f);
				fstream.write(source);
				fstream.close();
				Result result = process(className+appended, Server.this.getProblem(p.getProblemName()));
				f.delete();
				return result;
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * Given a class name and Problem, compile the class and run it with the problem data
		 * @param className The name of the client's class
		 * @param p The Problem that this class will be tested against
		 * @return The Result of processing the class
		 * @throws FileNotFoundException
		 */
		private Result process(String className, Problem p) throws FileNotFoundException
		{
			if(p == null)
				return null;
			ClassRunner classRunner = new ClassRunner(className, "TempFiles//"+className);
			Result result = classRunner.loadClass(classRunner.getLocation());
			if(result != null)
				return result;
			result = classRunner.runMainMethod(p.getInputFile(), p.getOutputFile());
			return result;
		}
	}

	/**
	 * Helper method to get a Problem from an ArrayList given it's name
	 * @param name
	 * @return
	 */
	private Problem getProblem(String name)
	{
		for(Problem p: problems)
		{
			if(p.getName().equals(name))
				return p;
		}
		return null;
	}

	/**
	 * Helper method to get all problem names from each Problem object
	 * @return
	 */
	private ArrayList<String> getProblemNames()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(Problem p: problems)
		{
			ret.add(p.getName());
		}
		return ret;
	}
}
