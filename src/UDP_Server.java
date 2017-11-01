import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

/**
 * This class contains a method that listens for incoming UDP traffic on a
 * specified port number and prints the contents of the message to the console
 */
public class UDP_Server implements Runnable {
	FogNodeEntity fe = null;

	BlockingQueue<Request> process_queue = null;
	BlockingQueue<Request> cloud_queue = null;
	public void run() {

		int PortNo = fe.fog_udp;
		process_queue = fe.getProcessQueue();
		cloud_queue = fe.getCloudQueue();
		DatagramSocket serverSocket = null;

		try {
			serverSocket = new DatagramSocket(PortNo);
		} catch (SocketException ex) {
			System.out.println("Cannot make UDP Datagram Socket on port " + PortNo);
			ex.printStackTrace();
		} 

		byte[] receiveData = new byte[20000]; // can recieve UDP responses up to 20Kb
		while (true) {
			//System.out.println("Waiting for packet udp");
			Arrays.fill(receiveData, (byte) 0);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // receive the response
			try {
				serverSocket.receive(receivePacket);
				String packet = new String(receivePacket.getData());
				System.out.println("RECEIVED: " + packet);

				String[] packetarray = packet.split(" ");

				String packetProcessingTime = packetarray[1].substring(2);
				Long pPTimeL = new Long(packetProcessingTime);
				pPTimeL = pPTimeL+fe.getProcessingDelay();

				Request receivedRequest = new Request(packet);
				if(fe.Max_Response_Time > pPTimeL){
					//Process the request;
					try {
						fe.setProcessingDelay(pPTimeL);
						process_queue.put(receivedRequest);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						System.out.println("problem blocking queue ");
						e.printStackTrace();
					}
				}else{
					//Forward packet case

					NeighbourEntity ne = fe.getBestNeighbour(receivedRequest);

					if(ne==null || receivedRequest.forwardLimit.equals(String.valueOf(0)))
					{
						    
						System.out.println("Forward to cloud---"+receivedRequest.message);
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
                } catch (IOException ex) {
				System.out.println("I/O exception happened!");
				ex.printStackTrace();

			}
			// print contents of the response
		}
	}
}

