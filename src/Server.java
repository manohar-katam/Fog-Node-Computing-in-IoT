import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.omg.CORBA.portable.InputStream;

public class Server implements Runnable {

	FogNodeEntity fe = null;
	ServerSocket ss = null;
	
	int sPort;
	@Override
	public void run() {
		
		try{
			sPort = fe.fog_TCP;
			ss = new ServerSocket(sPort);
			
		}catch (Exception e) {
			
		}
		while(true){
			
			Socket cs = null;
			try{
				cs = ss.accept();
				
				// starting the receive client thread.
				ReceiveClient rc = new ReceiveClient();
				rc.cSocket = cs;
				rc.fe = fe;
				
				new Thread(rc).start();
				
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}
	
}
