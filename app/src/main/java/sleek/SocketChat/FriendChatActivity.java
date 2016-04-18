package sleek.SocketChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sleek.ChatWindow.ChatMsgViewAdapter;
import sleek.Provider.DataChangeProvider;
import sleek.Provider.DataUtils;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendChatActivity extends Activity{
	SocketThread st;
	Thread thread = null;
	private String ip,port,name,friendname;
	//private String reMsg=null;
	 
	DataInputStream dis = null;
    DataOutputStream dos = null;
    
    private EditText chattxt;
    private TextView Fname;
    private Button chatok;
    private ListView talkView;
    //DataSqlite mDataSqlite;
    
    private DisplayMetrics dm; 
    int dmwidth=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		//mDataSqlite = new DataSqlite(this);
		
		chattxt = (EditText)findViewById(R.id.chatedit);
		chatok = (Button)findViewById(R.id.send);
		talkView = (ListView) findViewById(R.id.list);
		Fname = (TextView) findViewById(R.id.name);
		
		//获取手机屏幕像素
		dm = new DisplayMetrics();   
		getWindowManager().getDefaultDisplay().getMetrics(dm);  
		dmwidth = dm.widthPixels;
	        
		//监视聊天数据的变化
		getContentResolver().registerContentObserver(DataChangeProvider.CONTENT_URI,true, cob);
		
		//接收好友对象资料
		Intent intent = getIntent();
		ip = intent.getStringExtra("ip");
		port = intent.getStringExtra("port");
		friendname = intent.getStringExtra("friendname");
		name = intent.getStringExtra("name");
		
		//对象称呼
		Fname.setText(friendname);
		
		//首次进入，先把聊天记录调出
		updateChatBox(friendname);	
				
		//已读
		readChat();
		
		//建立连接
		chatContent();
		
		//发送信息
		chatok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String str = chattxt.getText().toString().trim();
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	            String date = df.format(new Date());
				try {
					if(dos!=null){
						dos.writeUTF(config.PROTOCOL_KEY+config.PROTOCOL_FOR+friendname+config.PROTOCOL_FOR_END
								+name+"end;"+str);
						
						ContentValues con = new ContentValues();
						con.put("fromChat", friendname);
						con.put("nameChat", name);
						con.put("contentChat", str);
						con.put("typeChat", "text");
						con.put("postdateChat", date);
						con.put("isreadChat", "1");
						con.put("ismineChat", "1");
						con.put("deleteChat", "0");
						getContentResolver().insert(DataChangeProvider.CONTENT_URI, con);
						
						chattxt.setText("");
					}else{
						Toast.makeText(FriendChatActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_LONG).show();
					}
					
				}catch (SocketTimeoutException  e) {
		  			System.out.println("連接超時，服務器未開啟或IP錯誤");
		  			Toast.makeText(FriendChatActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
		  			Intent intent = new Intent(FriendChatActivity.this,IniActivity.class);
					startActivity(intent);
					FriendChatActivity.this.finish();
		  			e.printStackTrace();
		  		} catch (IOException e) {
					// TODO Auto-generated catch block
		  			System.out.println("連接超時，服務器未開啟或IP錯誤");
		  			Toast.makeText(FriendChatActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
		  			Intent intent = new Intent(FriendChatActivity.this,IniActivity.class);
					startActivity(intent);
					FriendChatActivity.this.finish();
		  			e.printStackTrace();
				}
			}
		});
	}
	
	private void updateChatBox(String name){
		//chatbox.setText(DataUtils.getChangeChat(getApplicationContext(),name));
		talkView.setAdapter(new ChatMsgViewAdapter(FriendChatActivity.this, 
				DataUtils.getChangeChat(getApplicationContext(),name),dmwidth));
		talkView.setSelection(talkView.getCount());
		//chatbox.setSelection(chatbox.length());
	}
	
	private void readChat(){
		ContentValues con = new ContentValues();
		con.put("isreadChat", "1");
		getContentResolver().update(DataChangeProvider.CONTENT_URI, con, "fromChat='"+friendname+"'",null);
			
	}
	
	private ContentObserver cob = new ContentObserver(new Handler()) {

		@Override
		public boolean deliverSelfNotifications() {
			return super.deliverSelfNotifications();
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			updateChatBox(friendname);
			//chatbox.setText(DataUtils.getChangeChat(getApplicationContext(),friendname));
			//chatbox.setSelection(chatbox.length());
		}
		 
	};

	
	public void chatContent(){
		try {
			st = new SocketThread();
			st.SocketStart(ip, port, friendname);
			if(st.isConnected()){
	    		dos = st.getDOS();
	    		dis = st.getDIS();
	    		dos.writeUTF(config.PROTOCOL_KEY+config.PROTOCOL_FOR+friendname);
	    		//thread = new Thread(null, doThread, "ReMessage");
	  			//thread.start();
	  			//System.out.println(st+"chat connect");
			}
		}catch (UnknownHostException e) {
  			System.out.println("連接失敗");
			Toast.makeText(FriendChatActivity.this, "連接失敗", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(FriendChatActivity.this,IniActivity.class);
			startActivity(intent);
			FriendChatActivity.this.finish();
  			e.printStackTrace();
  		}catch (SocketTimeoutException  e) {
  			System.out.println("連接超時，服務器未開啟或IP錯誤");
  			Toast.makeText(FriendChatActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(FriendChatActivity.this,IniActivity.class);
			startActivity(intent);
			FriendChatActivity.this.finish();
  			e.printStackTrace();
  		}catch (IOException e) {
  			System.out.println("連接失敗");
  			e.printStackTrace();
  		}
	}
	
//	private Runnable doThread = new Runnable() {
//		public void run() {
//			System.out.println("run");
//			try {
//				while ((reMsg = dis.readUTF()) != null) {
//					System.out.println("receive!"+reMsg);
//					if(reMsg.startsWith(config.PROTOCOL_FRIENDS_START) && reMsg.endsWith(config.PROTOCOL_FRIENDS_END)){
//						//System.out.println(reMsg);
//						try {
//							Message msgMessage = new Message();
//							msgMessage.what = 0x1981;
//							handler.sendMessage(msgMessage);
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//						
//					}
//					else if (reMsg != null) {
//
//						try {
//							Message msgMessage = new Message();
//							msgMessage.what = 0x1981;
//							handler.sendMessage(msgMessage);
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//				}
//			} catch (SocketException e) {
//				// TODO: handle exception
//				System.out.println("exit!");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}; 
	/**
	 * 通过handler更新UI
	 */
//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0x1981:
//				chatbox.setText(chatbox.getText() + reMsg + '\n');
//				chatbox.setSelection(chatbox.length());
//				break;
//			}
//		}
//	};
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		disConnect();
		getContentResolver().unregisterContentObserver(cob);
		//System.exit(0);
	}
	
    public void disConnect() {
    	if(dos!=null){
	    	try {
	    		dos.close();
	    		dis.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		try {
				st.CloseSocket(friendname);
			} catch (Exception e) {
				// TODO: handle exception
			}
	    	
    	}
    }
}