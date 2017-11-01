import java.net.Socket;

public class NeighbourEntity {

	String ne_IP;  // neighbor fog IP
	int ne_TCP_PORT; // neighbor fog TCP Port
	Socket ne_TCP_SOCKET; // neighbor fog TCP Socket
	Long ne_IP_Number ;  // neighbor fog IP in long format
	Long ne_Processing_DELAY = 0L;  // neighbor fog node's processing delay
	
	// getter and setter synchronized methods for setting and getting the processing delay
	public synchronized void setDelay(Long d){
		
		ne_Processing_DELAY = d;
	}
	
	public synchronized long getDely(){
		return ne_Processing_DELAY;
	}
}
