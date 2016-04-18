package sleek.SocketChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketThread{
	private Socket socket;
	private String ip,port;
	private InetSocketAddress isa; 
	private DataOutputStream DOS=null;
	private DataInputStream DIS=null;
	
	SocketMap smMap;
	
	public void SocketStart(String myip,String myport,String type){
		this.ip=myip;
		this.port=myport;
		
		//new Thread(){
			//public void run(){
				socket = new Socket();
				isa = new InetSocketAddress(ip,Integer.parseInt(port)); 
				try {
					socket.connect(isa,5000);
					System.out.println("连接成功"+socket);
					smMap = new SocketMap();
					smMap.setSocket(type, socket);
				}catch (SocketTimeoutException e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println("连接超时"+e.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("连接失败"+e.toString());
				} 
			//}
		//}.start();
	}
	
	public DataOutputStream getDOS() throws IOException{
		DOS = new DataOutputStream (this.socket.getOutputStream());
		return DOS;
	}
	
	public DataInputStream getDIS() throws IOException{
		//System.out.println("DIS:"+socket);
		DIS = new DataInputStream (this.socket.getInputStream());
		return DIS;
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public void setIP(String setip){
		this.ip=setip;
	}
	public void setPort(String setport){
		this.port=setport;
	}
	
	public boolean isConnected(){
		return socket.isConnected();
	}
	
	public void CloseSocket(String type){
		smMap.removeMap(type);
	}
	
	public void AllClose(){
		smMap.clearMap();
	}
}