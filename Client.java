import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;


public class Client implements Runnable{

	public int counter=0;
	HashMap<Integer, SctpChannel> connectionDetails;

	public Client(HashMap<Integer, SctpChannel> con) {
		this.connectionDetails = con;
	}
	@Override
	public void run() {

		while(true)
		{
			String ms = "Hello ....";
			Message msg = new Message();
			msg.messageText = ms;
			msg.senderNode = Node.NodeId;
			msg.initiator = Node.NodeId;
			msg.timeStamp = Node.clock.increment();
			msg.type = Message.messageType.Application.toString();
			sendAll(Node.connectionDetails,msg);
		}	
	}

	public void send (ConcurrentHashMap<Integer, SctpChannel> connectionDetails, Message message)
	{
		for(Entry<Integer , SctpChannel> entry : connectionDetails.entrySet())
		{
			if(entry.getKey().equals(message.senderNode))
			{
				try {
					message.senderNode = Node.NodeId;
					message.timeStamp = Node.clock.increment();
					sendMessage(entry.getValue(),message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public  void sendAll(HashMap<Integer, SctpChannel> connectionDetails, Message message) {

		for(Entry<Integer , SctpChannel> entry : connectionDetails.entrySet())
		{
			try {
				sendMessage(entry.getValue(),message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	private static  void sendMessage(SctpChannel clientSock, Message message) throws IOException
	{
		// prepare byte buffer to send massage
		ByteBuffer sendBuffer = ByteBuffer.allocate(60000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] yourBytes;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(message);
			yourBytes = bos.toByteArray();
		} finally {
			out.close();
			bos.close();
		}

		sendBuffer.clear();
		//Reset a pointer to point to the start of buffer 
		sendBuffer.put(yourBytes);
		sendBuffer.flip();
		try {
			//Send a message in the channel 
			MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
			clientSock.send(sendBuffer, messageInfo);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
