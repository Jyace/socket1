package sleek.SocketChat;

import java.io.File;

public class config{
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	public static File path = new File(SDCARD+"/RunChatDatabase/"); //数据库文件目录   
	public static File f = new File(SDCARD+"/RunChatDatabase/config.db"); //数据库文件  
	
	public static String PROTOCOL_FRIENDS_START = "〖";
	public static String PROTOCOL_FRIENDS_END = "〗";
	public static String PROTOCOL_FRIENDS_SEPARATE= "￠";
	
	public static String PROTOCOL_ONLINE = "※";
	public static String PROTOCOL_OFFLINE = "¤";
	
	public static String PROTOCOL_WAIT = "⊙";
	
	public static String PROTOCOL_FOR= "∈";
	public static String PROTOCOL_FOR_END= "§";
	
	public static String PROTOCOL_KEY = "SLEEKNETGEOCK4stsjeS";
}