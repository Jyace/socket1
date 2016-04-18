package sleek.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import sleek.ChatWindow.ChatMsgEntity;
import sleek.SocketChat.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

public class DataUtils {
	private static ArrayList<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
	static ChatMsgEntity newMessage;
	static DateFormat df = new SimpleDateFormat("hh:mm");
	static Cursor getCursor(Context context,String name){
		ContentResolver conre = context.getContentResolver();
		
		return conre.query(DataChangeProvider.CONTENT_URI, 
				new String[]{"_id","fromChat","nameChat","contentChat","typeChat","postdateChat",
				"isreadChat","ismineChat","deleteChat"}, "deleteChat=? and fromChat=?",
				new String[]{"0",name.trim()}, null);
	}
	
	public static ArrayList<ChatMsgEntity> getChangeChat(Context context,String name){
		int RId = R.layout.list_say_he_item;
		int LId = R.layout.list_say_me_item;
		
		list = new ArrayList<ChatMsgEntity>();
		newMessage = new ChatMsgEntity("mark date", "null", "null", RId);
        list.add(newMessage);
		Cursor cur=null;
		try {
			cur = getCursor(context,name);
			if(cur!=null && cur.getCount() != 0){
				//cur.moveToFirst();
				while(cur.moveToNext()){
					int itemID=RId;
//					chaString+=cur.getString(cur.getColumnIndex("nameChat"))+" ";
//					chaString+="["+cur.getString(cur.getColumnIndex("postdateChat"))+"]";
//					chaString+="\n";
//					chaString+=cur.getString(cur.getColumnIndex("contentChat"));
//					chaString+="\n";
					if(cur.getString(cur.getColumnIndex("ismineChat")).equals("1")){
						itemID = LId;
					}
	                newMessage = new ChatMsgEntity(
	                		cur.getString(cur.getColumnIndex("nameChat")), 
	                		cur.getString(cur.getColumnIndex("postdateChat")).substring(11, 16), 
	                		cur.getString(cur.getColumnIndex("contentChat")), 
	                		itemID);
	                list.add(newMessage);
				}
				return list;
			}
		} finally{
			if(cur!=null){
				try {
					cur.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return list;
	}

}
