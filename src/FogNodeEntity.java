import java.awt.List;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FogNodeEntity {
	
	int Max_Response_Time; // Max Response time of the fog node
	long t; // periodic update time or sleep time of thread
	String fog_ip; // current fog IP 
	int fog_udp;  // current fog UDP Port
	int fog_TCP; // current fog TCP Port
	Long fog_IP_Number; // fog IP in long format
	Long processing_delay = 0L; // processing delay of requests in the queue
	
	BlockingQueue<Request> cloudQueue = new ArrayBlockingQueue<Request>(4096);  // for synchronization between the ProcessPacket Thread and CloudNode Thread
	BlockingQueue<Request> processQueue = new ArrayBlockingQueue<Request>(3000); // for synchronization between the ProcessPacket Thread and UDP_Server Thread
	
	Hashtable<String, NeighbourEntity> neighbour_cache = new Hashtable<String, NeighbourEntity>();  // Hash Table for storing the neighbor IP addresses and TCP Port Numbers
	
	// Priority queue for placing the neighbor fog nodes in queue in the order of their queue delays
	PriorityQueue<NeighbourEntity> neighbourQueue = new PriorityQueue<NeighbourEntity>(
			// Comparator function of priority queue for comparing the queue delays
			new Comparator<NeighbourEntity>() {

				@Override
				public int compare(NeighbourEntity o1, NeighbourEntity o2) {
					if(o1.getDely()>o2.getDely()){
						return 1;
					}else if(o1.getDely()<o2.getDely()){
						return -1;
					}else{
					
					return 0;
					}
					}
				}
	);
	
	
	public void startClientSockets(){
		
		// For all the neighbor fog nodes in the hash table
		for(java.util.Map.Entry<String, NeighbourEntity> ne:neighbour_cache.entrySet()){
			
			String key = ne.getKey();
			NeighbourEntity neighbour = ne.getValue();
			neighbourQueue.add(neighbour);
			boolean startClient = compareIP(IPToLong(fog_ip),IPToLong(neighbour.ne_IP),fog_TCP,neighbour.ne_TCP_PORT);
			
			// If it is a server
			if(!startClient){
				try {
					// Open a socket with the client
					Socket clientSocket = new Socket(neighbour.ne_IP, neighbour.ne_TCP_PORT);
					neighbour.ne_TCP_SOCKET = clientSocket;
					// start a new thread for receiving the client messages or updates
					ReceiveClient cl = new ReceiveClient();
					cl.fe = this;
					cl.cSocket = clientSocket;
					new Thread(cl).start();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	// Determining the client and server through comparison of IP numbers and port numbers
	public boolean compareIP(long IPAddress_1, long IPAddress_2, int port_1, int port_2 ){
		if(IPAddress_1 < IPAddress_2){
			return true;
		}
		else if (IPAddress_1 > IPAddress_2){
			return false;
		}
		else{
			if(port_1 < port_2){
				return true;
			}
			else{
				return false;
			}
		}
		
	} 

	// Converting the IP numbers to long format by removing the dots and converting them to the base 256.
	 public  long IPToLong(String IPAddress) {

		String[] IPAddressInArray = IPAddress.split("\\.");

		long result = 0;
		for (int i = 0; i < IPAddressInArray.length; i++) {

			int power = 3 - i;
			int IP = Integer.parseInt(IPAddressInArray[i]);
			result += IP * Math.pow(256, power);

		}

		return result;
	}
	 
	 public synchronized BlockingQueue<Request> getProcessQueue() {
			return processQueue;
		}
	 public synchronized void setProcessingDelay(Long val){
		 processing_delay = val;
	 }
	 
	 public synchronized void subProcessingDelay(Long val){
		
		 processing_delay = processing_delay-val;
	 }
	 
	 public synchronized Long getProcessingDelay(){
		 return processing_delay;
	 }
	 
	 public synchronized NeighbourEntity getBestNeighbour(Request receivedRequest){
		 
		ArrayList<String> nodesOnPath = receivedRequest.NodesOnPath; // List of visited nodes on the received request
		NeighbourEntity returnNeighbour = null;
		ArrayList<NeighbourEntity> visitedNodes = new ArrayList<NeighbourEntity>(); // details of visited nodes
		
		for(int i=0;i<=neighbourQueue.size();i++){
			
		NeighbourEntity n = neighbourQueue.peek(); // getting the best neighbor
		
		String search = n.ne_IP+"+"+n.ne_TCP_PORT+""; 
		//searching the best neighbor in the nodes visited
		if(nodesOnPath.contains(search)){
			System.out.println("already visited neigbr "+search);
			visitedNodes.add(neighbourQueue.poll()); // if exists remove the best neighbor from the queue
			
		} else{
			returnNeighbour = neighbourQueue.poll(); // declare him as best neighbor
			System.out.println("best neigbr available"+returnNeighbour.ne_TCP_PORT);
			this.addElementToPQ(returnNeighbour); // place the best neighbor in the priority queue
			break;
		}
			
		}
		
		for(int i=0;i<visitedNodes.size();i++)
			neighbourQueue.offer(visitedNodes.get(i)); // append the visited nodes to queue
			
		 if(returnNeighbour!=null)
			 return returnNeighbour;
		 
		return null;
	 }
	 public synchronized void addElementToPQ(NeighbourEntity n){
		neighbourQueue.add(n);
	 }
	 public synchronized void removeElementFromPQ(NeighbourEntity n){
		neighbourQueue.remove(n);
	 }
	 
	 public synchronized BlockingQueue<Request> getCloudQueue() {
		 return cloudQueue;
	 }

}
