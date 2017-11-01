import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

public class ProcessPacket implements Runnable {
	
	FogNodeEntity fe = null;
	BlockingQueue<Request> process_queue = null;
	
	public void run() {
	
		process_queue = fe.getProcessQueue();
		
		while(true){
			
			try {
				// Waiting for the request, once it comes it will put in the queue and intimates UDP server
				Request r = process_queue.take();
				Long processT = new Long(r.processTime);
				Thread.sleep(processT*1000); // processing time of the request
				fe.subProcessingDelay(processT);
				String msg = r.message+" "+fe.fog_ip+"+"+fe.fog_TCP+" "+"Processed";
				try {
					send(InetAddress.getByName(r.ownHostName), new Integer(r.listenerPortNumber), msg);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
	 private void send(InetAddress IPAddress, int port, String data) {

	        DatagramSocket clientSocket = null;
	        try {
	            clientSocket = new DatagramSocket(); // make a Datagram socket
	        } catch (SocketException ex) {
	            System.out.println("Cannot make Datagram Socket!");
	            ex.printStackTrace();
	        }

	        byte[] sendData = new byte[data.getBytes().length]; // make a Byte array of the data to be sent
	        sendData = data.getBytes(); // get the bytes of the message
	        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); // craft the message to be sent
	        try {
	            clientSocket.send(sendPacket); // send the message
	        } catch (IOException ex) {
	            System.out.println("I/O exception happened!");
	            ex.printStackTrace();
	        }

	    }

}
