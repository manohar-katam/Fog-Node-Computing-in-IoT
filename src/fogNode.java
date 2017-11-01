import java.net.InetAddress;
import java.net.UnknownHostException;


public class fogNode {
	
	
	
	public static void main(String[] args){
		
		if(args.length<0){
			System.out.println("Provide arguments");
			return;
		}
		else {
					
			FogNodeEntity f = new FogNodeEntity();
			
			
			f.Max_Response_Time = Integer.parseInt(args[0]);  // Max Response time of the fog node
			f.t = Integer.parseInt(args[1]); // periodic update time
			
			
			f.fog_ip = args[2]; // Current fog IP Address
			f.fog_udp = Integer.parseInt(args[3]); // Current fog node's UDP Port
			f.fog_TCP = Integer.parseInt(args[4]); // Current fog node's TCP Port
			
			/*
			 * Read all the neighbor fog node's IP Address and TCP Port Number
			 */
			int i =5;
			while(i<args.length)
			{
				NeighbourEntity ne = new NeighbourEntity();
				
				ne.ne_IP = args[i];  // Neighbor fog node IP
				ne.ne_TCP_PORT = Integer.parseInt(args[i+1]);  // Neighbor fog node's TCP Port
				f.neighbour_cache.put(ne.ne_IP+"+"+ne.ne_TCP_PORT,ne);  // put this Neighbor fog IP and TCP Port in Hash Table
				i = i+2;
			}
			
			// Starting the client Sockets
			f.startClientSockets();
			System.out.println("Starting require threads");
			
			// Start the UDP Listener Thread
			UDP_Server uServer = new UDP_Server();
			uServer.fe = f;
			new Thread(uServer).start();
			
			// Start the TCP Listener Thread
			Server listen = new Server();
			listen.fe = f;
			new Thread(listen).start();
			
			// Start the Periodic Updated Thread
			PeridicUpdates pu = new PeridicUpdates();
			pu.fe =f;
			new Thread(pu).start();
	        
			// Start the Packet processing Thread
			ProcessPacket processPacket = new ProcessPacket();
			processPacket.fe = f;
			new Thread(processPacket).start();
			
			// Start the cloud thread
			CloudNode cn = new CloudNode();
			cn.fe = f;
			new Thread(cn).start();
		}
		
	}

}
