package sleek.SocketChat;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SocketMap{
	
	public static Map<String, Socket> socketMap = new HashMap<String, Socket>();;
	private Socket smSocket;
	
	public SocketMap(){
		/**
		 * 保存管理所有连接Socket 
		 * 好友在线列表，聊天，聊天动作
		 */
		
	}
	
	public void setSocket(String str,Socket socket){
		socketMap.put(str, socket);	
	}
	
	public Socket getSocket(String key){
		smSocket = socketMap.get(key);
		return smSocket;
	}
	
	public Map<String, Socket> getMap(){
		return socketMap;
	}
	
	public void removeMap(String key){
		try {
			socketMap.get(key).close();
			socketMap.remove(key);
			System.out.println("remove socket");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void clearMap(){
		for (Iterator iterator=socketMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry elementEntry = (Map.Entry) iterator.next();
			Socket socketObject = (Socket) elementEntry.getValue();
			try {
				socketObject.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		socketMap.clear();
	}
}