import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore.Entry;
import java.util.Hashtable;

public class PeridicUpdates implements Runnable {

	FogNodeEntity fe = null;
	Socket s = null;
	Hashtable<String, NeighbourEntity> neighbour_cache=null;
	public void run() {
		
		neighbour_cache = fe.neighbour_cache;
		int PortNo = fe.fog_TCP;
		long sleep_time = fe.t;
		while(true){
			
			for(java.util.Map.Entry<String, NeighbourEntity> ne:neighbour_cache.entrySet()){
				
				String key = ne.getKey();
				NeighbourEntity neighbour = ne.getValue();
				s = neighbour.ne_TCP_SOCKET;
				
				if(s!=null){
					try {
					OutputStream op = s.getOutputStream();
					OutputStreamWriter os = new OutputStreamWriter(op);
					PrintWriter out = new PrintWriter(os);
					//String message ="hello";
					String message = PortNo+""+" "+fe.getProcessingDelay()+""+"\n";
					
				    //String message = "12"+" "+number+""+"\n";
					//String[] message_split = message.split(" ");
					//System.out.println(x+")"+message);
					
					out.write(message);
					out.flush();
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						
					}	
				}
				}
			try {
				Thread.sleep(sleep_time);
				
			} catch (InterruptedException e) {
				 System.out.println("Thread cannot sleep!");
				e.printStackTrace();
			}
		}
		
	}

	
}
