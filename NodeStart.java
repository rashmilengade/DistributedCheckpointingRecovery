import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;


public class NodeStart {

	public static void main(String[] args) throws IOException {
		Node node = new Node();
		HashMap<Integer, SctpChannel> connectionDetails = new HashMap<Integer,SctpChannel>();

		String fileName = null;
		if (0 < args.length) {
		    fileName = args[0];
		  }
		else
		{
			System.out.println("Invalid File Name.. !!! Please run again");
		}
		 MakeConfiguration(node,fileName);
				
		SctpServerChannel serverSocket;
		serverSocket = SctpServerChannel.open();
		InetSocketAddress serverAddress = new InetSocketAddress(node.portNumber);
		serverSocket.bind(serverAddress);
		Thread serverThread = new Thread(new Server(serverSocket));
		serverThread.start();
		System.out.println("Bound port : " + node.portNumber);
		/*System.out.println("Waiting for connection ...");*/

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ConnectAll();

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Thread clientThread = new Thread(new Client(connectionDetails));
		clientThread.start();
		
		Thread application = new Thread(new Application());
		application.start();
	}

	public static void ConnectAll() throws IOException {

		for(int i=1; i<Node.sendConfiguration.size(); i++)
		{
			String value = Node.sendConfiguration.get(i);
			String[] values = value.split(" ");
			String hostName = values[0];
			int portNumber = Integer.parseInt(values[1]);

			SctpChannel clientSocket;
			InetSocketAddress serverAddr = new InetSocketAddress(hostName,portNumber); 
			clientSocket = SctpChannel.open();
			clientSocket.connect(serverAddr, 0, 0);
			clientSocket.configureBlocking(false);
			Node.connectionDetails.put(i, clientSocket);
		}
	}


	public static void MakeConfiguration(Node node,String fileName) throws NumberFormatException, IOException {

		HashMap<Integer, String> configuration = new HashMap<Integer,String>();
		String myHostName;
		myHostName = java.net.InetAddress.getLocalHost().getHostName();

		FileReader file = new FileReader(fileName);
		BufferedReader br = new BufferedReader(file);
		String line;
		//ArrayList<String[]> neighboursList = new ArrayList<String[]>();
		String[] neighbours = null;

		while((line = br.readLine()) != null)
		{
			String[] token = line.split(" ");
			int key = Integer.parseInt(token[0]);
			String hostName = token[1];
			String portNumber = token[2];
			Node.timer = Integer.parseInt(token[3]);
			
			
			if(myHostName.equals(hostName))
			{
				node.hostName = hostName;
				node.portNumber = Integer.parseInt(portNumber);
				node.setNodeId(key);
				neighbours = token[4].split(",");
			}
			//neighboursList.add(neighbours);
			configuration.put(key, hostName + " " + portNumber);
		}
	
		System.out.println("My Node ID " + node.getNodeId());

		br.close();
		file.close();

		for(String n : neighbours)
		{
			if(Integer.parseInt(n) < node.getNodeId())
			{
				node.sendConfiguration.put(Integer.parseInt(n), configuration.get(Integer.parseInt(n)));
			}
			else
			{
				node.receiveConfiguration.put(Integer.parseInt(n), configuration.get(Integer.parseInt(n)));
			}
		}
		
		//return configuration;
	}
}
