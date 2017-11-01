import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

import org.omg.CORBA.portable.InputStream;

/*
 * This thread receives the messages or updates from the neighbors 
 */


public class ReceiveClient implements Runnable{

	Socket cSocket = null;
	FogNodeEntity fe = null;
	Hashtable<String, NeighbourEntity> nb_table = null;
	BlockingQueue<Request> process_queue = null;
	BlockingQueue<Request> cloud_queue =null;
	
	public void run() {
		
		nb_table=this.fe.neighbour_cache;
		process_queue = fe.getProcessQueue();
		cloud_queue = fe.getCloudQueue();
			try {
			
				String clientIp = cSocket.getInetAddress().toString();
				clientIp = clientIp.trim().substring(1);
				java.io.InputStream ip = cSocket.getInputStream();
				InputStreamReader ir = new InputStreamReader(ip);
				
				BufferedReader br = new BufferedReader(ir);
				String message;
				while( (message = br.readLine().trim())!=null){
				
		
				System.out.print("message received from NEIBR: "+ message+"\n");
				String strSplit[] = message.split(" ");
				if(strSplit.length==2){
				String neighbourKey = clientIp+"+"+strSplit[0];
				
				NeighbourEntity neighbour = fe.neighbour_cache.get(neighbourKey);
				neighbour.setDelay(new Long(strSplit[1]));
				if(neighbour.ne_TCP_SOCKET==null){
					neighbour.ne_TCP_SOCKET = cSocket;
				
				}
				fe.removeElementFromPQ(neighbour);
				fe.addElementToPQ(neighbour);
				}
				else
				{
					// Incoming packet
					 String packetProcessingTime = strSplit[1].substring(2);
		             Long pPTimeL = new Long(packetProcessingTime);
		             pPTimeL = pPTimeL+fe.getProcessingDelay();
		             Request receivedRequest = new Request(message);
		                if(fe.Max_Response_Time > pPTimeL){
		                	//Process the request;
		                	try {
		                		fe.setProcessingDelay(pPTimeL);
								process_queue.put(receivedRequest);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								System.out.println("problem with blocking queue ");
								e.printStackTrace();
							}
		                }else{
		                	//Forward packet case
		                	
		                	NeighbourEntity ne = fe.getBestNeighbour(receivedRequest);
		                	
		                	if(ne==null || receivedRequest.forwardLimit.equals(String.valueOf(0))){
		                		System.out.println("Forward to cloud-->"+receivedRequest.message);
		                		try {
									cloud_queue.put(receivedRequest);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                	}
		                	else{
		                		//forward to neighbour ne
		                		System.out.println("cuurent delay"+fe.getProcessingDelay());
		                		System.out.println("Forwarded to "+ne.ne_IP+"+"+
				                		ne.ne_TCP_PORT);
		                		Socket s = ne.ne_TCP_SOCKET;
		                		OutputStream op = s.getOutputStream();
		    					OutputStreamWriter os = new OutputStreamWriter(op);
		    					PrintWriter out = new PrintWriter(os);
		    					String sms = receivedRequest.decrementFL();
		    					sms = sms + (" "+fe.fog_ip+"+"+fe.fog_TCP+" "+"Forward");
		    					out.write(sms+"\n");
		    					out.flush();
		    					
		                		
		                	}
		                	
		                }
		                
					
				}
				}
			}	
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		}
		
	


