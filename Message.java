import java.io.Serializable;

public class Message implements Serializable {
	
	public enum messageType {Application, Abort , Commit , Rollback , Checkpoint};
	
	private static final long serialVersionUID = 1L;
	String messageId;
	int timeStamp;
	//Node sender;
	//ArrayList<Node> receivers;
	int senderNode;
	int initiator;
	boolean delivered;
	int counter;
	String type;
	String messageText;
}
