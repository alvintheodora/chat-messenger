import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
public class ClientTest {
	public static void main (String[] args){
		//try{
			Client dude = new Client("127.0.0.1");
			dude.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			dude.startRunning();
//		}catch(UnknownHostException unknownHostException){
//			unknownHostException.printStackTrace();
//		}
		
	}
}
