import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map.Entry;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;


public class Server implements Runnable {
	public int count =0;
	SctpServerChannel serverSocket;
	public Server(SctpServerChannel serverSock) {
		this.serverSocket = serverSock;
	}
	@Override
	public void run() {
		ByteBuffer byteBuffer;
		byteBuffer = ByteBuffer.allocate(60000);
		String hostname = null;
		// Accept Connections from all other nodes
		SctpChannel[] clientSockets = new SctpChannel[Node.receiveConfiguration.size()];
		for(int i=1; i<Node.receiveConfiguration.size(); i++)
		{
			try {
				clientSockets[i] = serverSocket.accept();
				clientSockets[i].configureBlocking(false);
				Iterator<SocketAddress> it = clientSockets[i].getRemoteAddresses().iterator();
				boolean flag = false;
				while(it.hasNext())
				{
					InetSocketAddress sc = (InetSocketAddress) it.next();
					String hostName = sc.getHostName().toString();
					for(Entry<Integer,String> entry : Node.receiveConfiguration.entrySet())
					{
						String value = entry.getValue();
						String[] values = value.split(" ");
						if(hostName.equals(values[0]))
						{
							Node.connectionDetails.put(entry.getKey(),clientSockets[i]);
							System.out.println("Node"+Node.NodeId+" accepts connection from "+Node.sendConfiguration.get(entry.getKey()));
							flag = true;
							break;
						}
					}
					if(flag)
					{
						break;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		//Accept messages from all the nodes and process it
		boolean flag = true;
		while(flag)
		{
			for(Entry<Integer,SctpChannel> entry : Node.connectionDetails.entrySet())
			{
				try {
					byteBuffer.clear();
					MessageInfo msgInfo = entry.getValue().receive(byteBuffer,null,null);
					byteBuffer.flip();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(byteBuffer.remaining() >0)
				{

					byte[] yourBytes = byteBuffer.array();
					ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
					ObjectInput in = null;
					try {
						in = new ObjectInputStream(bis);
						Message messageInfo =(Message) in.readObject(); 
				System.out.println(messageInfo.messageText+", "+messageInfo.senderNode+", "+messageInfo.timeStamp);
			

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							bis.close();
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		}
	}
}
}
