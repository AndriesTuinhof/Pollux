package betapanda.net;

import java.net.Socket;
import awesome.net.SocketConnection;

public class Network extends Thread
{
	private SocketConnection connection;
	public static int ping;
	private static Network network = new Network();
	private static boolean reconnect = true;
	
	// Prevent instancing
	private Network(){ };
	
	public static void open(){
		network.start();
	}
	
	public void run(){
		while(reconnect)
		{
			try{
				String ip ="192.168.0.117";
//				String ip ="192.168.1.104";
//				String ip = "localhost";
//				if(new File("D:/wamp").exists())
//				if(new File("D:/wamp").exists()) ip = "192.168.0.102";
				System.out.println("Connecting to "+ip);
				Socket s = new Socket(ip, 6141);
	//			Socket s = new Socket("localhost", 6141);
				s.getOutputStream().write(123);
				connection = new SocketConnection(s);
				System.out.println("Client: Connected to server.");
				
				while(network.connection.connected()) Thread.sleep(2000);
				if(!reconnect) break;
				System.out.println("Lost connection to server, reconnecting...");
			}
			catch(Exception e){
				System.out.println("Couldn't connect to server");
//				e.printStackTrace();
			}
		}
	}
	
	public static void close()
	{
		reconnect = false;
		if(network.connection!=null) network.connection.close();
	}
	
	public static void sendPacket(byte[] packetAsData)
	{
		if(network.connection!=null) network.connection.sendPacket(packetAsData);
	}
	public static byte[] fetchPacket() {
		if(network.connection!=null) return network.connection.fetchNextPacket();
		return null;
	}
}
