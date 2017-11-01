import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Request {

	String message;

	
	String seqNumber; // squence number of the request to be sent
	String processTime;
	String interval; // interval (in ms)
	String forwardLimit; // forward limit 
	String ownHostName; // ownHostName 
	String listenerPortNumber; // listenerPortNumber 
	ArrayList<String> NodesOnPath = new ArrayList<String>();

	public Request(String request) {


		message = request.trim();

		getRequestFields();
		setNodes();

	}
	private void getRequestFields() {
		
		// splitting the string message into respective fields
		
		String msgArray[] = message.split(" ");
		
		seqNumber = msgArray[0].substring(2);
		processTime = msgArray[1].substring(2);
		forwardLimit = msgArray[2].substring(3);
		ownHostName = msgArray[3].substring(3);
		listenerPortNumber = msgArray[4].trim().substring(2);

	}
	
	// setting the visted nodes to array list
	public void setNodes(){
		
		String msgSplit[] = message.split(" ");
		int i =5;
		while(i<msgSplit.length)
		{
			NodesOnPath.add(msgSplit[i]);
			i= i+2;
		}
		
	}
	
	// function to decrement Forward Limit and forwarding the message
	public String decrementFL(){
		int fl = Integer.valueOf(forwardLimit)-1;
		String msg = "#:"+seqNumber+" "+"T:"+processTime+" "+"FL:"+fl+" "+"IP:"+ownHostName+" "+"P:"+listenerPortNumber;
		
		for(int i =0;i<NodesOnPath.size();i++){
			msg = msg+" "+NodesOnPath.get(i)+" "+ "Forward";
		}
		return msg;
	}
	

}
