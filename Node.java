
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;



/**
 * @author Rashmi Lengade
 */
public class Node {
	static int  NodeId;
	static String hostName;
	static int portNumber;
	static SctpServerChannel serverSocket;
	static LamportClock clock = new LamportClock();
	static HashMap<Integer,String> receiveConfiguration = new HashMap<Integer, String>();
	static HashMap<Integer,String> sendConfiguration = new HashMap<Integer, String>();
	
	static ConcurrentHashMap<Integer,SctpChannel> connectionDetails = new ConcurrentHashMap<Integer, SctpChannel>();
	static Queue<Message> msgQueue = new LinkedBlockingDeque<Message>();
	static int timer;
	static ArrayList<Integer> FLS = new ArrayList<Integer>();
	static ArrayList<Integer> LLR = new ArrayList<Integer>();
	static ArrayList<Integer> LLS = new ArrayList<Integer>();
	
	public int getNodeId() {
		return NodeId;
	}
	public void setNodeId(int nodeId) {
		NodeId = nodeId;
	}
	
	static class queueComparator implements Comparator<Message>  {

		@Override
		public int compare(Message o1, Message o2) {
			int i= o1.timeStamp - o2.timeStamp;
			if(i==0)
			{
				return o1.messageId.compareTo(o2.messageId);
			}
			return i;
		}

	}
}