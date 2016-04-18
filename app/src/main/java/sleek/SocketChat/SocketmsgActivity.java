package sleek.SocketChat;
//package sleek.testsocket;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.SocketException;
//import java.net.SocketTimeoutException;
//import java.net.UnknownHostException;
//
//import java.net.Socket;
//import java.util.List;
//import java.util.Map;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.*;
//
//public class SocketmsgActivity extends Activity {
//    /** Called when the activity is first created. */
//	private SQLiteDatabase db;
//	
//	Thread thread = null;
//    Socket s = null;
//    private InetSocketAddress isa = null; 
//
//    DataInputStream dis = null;
//    DataOutputStream dos = null;
//    private String reMsg=null;
//    private Boolean isContect = false;
//    private EditText chattxt;
//    private EditText chatbox;
//    private Button chatok;
//    
//    private String chatKey="SLEEKNETGEOCK4stsjeS";
//    private String name=null,ip=null,port=null;
//    //private String friends="aaa,bbb,ccc,ddd";
//    
//    private List<Map<String, Object>> friendList;
//	private String getFriendString;
//	Friends friends;
//    @Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		chattxt = (EditText)findViewById(R.id.chattxt);
//		chatbox = (EditText)findViewById(R.id.chatbox);
//		chatok = (Button)findViewById(R.id.chatOk);
//		chatbox.setCursorVisible(false);
//		chatbox.setFocusable(false);
//		chatbox.setFocusableInTouchMode(false);
//		chatbox.setGravity(2);
//		
//		//初始化，创建数据库来储存用户信息
//		InitDatabase();
//		db = SQLiteDatabase.openOrCreateDatabase(config.f, null);
//		try {
//			Cursor cursor = db.query("config", new String[]{"ip","name","port"},null,null, null, null, null);
//			while(cursor.moveToNext()){
//				name = cursor.getString(cursor.getColumnIndex("name"));
//				ip = cursor.getString(cursor.getColumnIndex("ip"));
//				port = cursor.getString(cursor.getColumnIndex("port"));
//			}
//			cursor.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println(e.toString());
//		}
//		db.close();
//		
//		//设置连接
//		if(ip==null || port==null){
//			Intent intent = new Intent(SocketmsgActivity.this,IniActivity.class);
//			startActivity(intent);
//			SocketmsgActivity.this.finish();
//		}
//		//设置名称
//		else if(name==null){
//			Intent intent = new Intent(SocketmsgActivity.this,IniuserActivity.class);
//			startActivity(intent);
//			SocketmsgActivity.this.finish();
//		}else{
//			
//			connect();
//			chatok.setOnClickListener(new View.OnClickListener() {
//	
//				@Override
//				public void onClick(View v) {
//	
//					String str = chattxt.getText().toString().trim();
//					System.out.println(s);
//					try {
//						dos.writeUTF(chatKey+"name:"+name+"end;"+str);
//						chattxt.setText("");
//	
//					}catch (SocketTimeoutException  e) {
//			  			System.out.println("連接超時，服務器未開啟或IP錯誤");
//			  			Toast.makeText(SocketmsgActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
//			  			Intent intent = new Intent(SocketmsgActivity.this,IniActivity.class);
//						startActivity(intent);
//						SocketmsgActivity.this.finish();
//			  			e.printStackTrace();
//			  		} catch (IOException e) {
//						// TODO Auto-generated catch block
//			  			System.out.println("連接超時，服務器未開啟或IP錯誤");
//			  			Toast.makeText(SocketmsgActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
//			  			Intent intent = new Intent(SocketmsgActivity.this,IniActivity.class);
//						startActivity(intent);
//						SocketmsgActivity.this.finish();
//			  			e.printStackTrace();
//					}
//				}
//			});
//		}
//	}
//    
//	private Runnable doThread = new Runnable() {
//		public void run() {
//			System.out.println("running!");
//		    ReceiveMsg();
//		}
//	};   
//    
//    public void connect() {
//    	try {
//    		s = new Socket();
//    		isa = new InetSocketAddress(ip,Integer.parseInt(port)); 
//    		s.connect(isa,5000); 
//
//    		if(s.isConnected()){
//	    		dos = new DataOutputStream (s.getOutputStream());
//	    		dis = new DataInputStream (s.getInputStream());
//	    		dos.writeUTF(chatKey+"online:"+name);
//	    		/**
//	    		 * 这里是关键，我在此耗时8h+
//	    		 * 原因是 子线程不能直接更新UI
//	    		 * 为此，我们需要通过Handler物件，通知主线程Ui Thread来更新界面。
//	    		 * 
//	    		 */
//	    		thread = new Thread(null, doThread, "Message");
//	  			thread.start();
//	  			System.out.println("connect");
//	  			isContect=true;
//    		}
//  		}catch (UnknownHostException e) {
//  			System.out.println("連接失敗");
//			Toast.makeText(SocketmsgActivity.this, "連接失敗", Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(SocketmsgActivity.this,IniActivity.class);
//			startActivity(intent);
//			SocketmsgActivity.this.finish();
//  			e.printStackTrace();
//  		}catch (SocketTimeoutException  e) {
//  			System.out.println("連接超時，服務器未開啟或IP錯誤");
//  			Toast.makeText(SocketmsgActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(SocketmsgActivity.this,IniActivity.class);
//			startActivity(intent);
//			SocketmsgActivity.this.finish();
//  			e.printStackTrace();
//  		}catch (IOException e) {
//  			System.out.println("連接失敗");
//  			e.printStackTrace();
//  		}
//    }
//   
//    public void disConnect() {
//    	if(dos!=null){
//    	try {
//    		
//    			dos.writeUTF(chatKey+"offline:"+name);
//    		
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	try {
//            s.close();
//        } catch (IOException e) {
//              e.printStackTrace();
//        }
//    	}
//    }
//  
//    
//    /**
//     * 线程监视Server信息
//     */
//	private void ReceiveMsg() {
//		if (isContect) {
//			try {
//				while ((reMsg = dis.readUTF()) != null) {
//					//System.out.println(reMsg);
//					if(reMsg.startsWith("Friends:") && reMsg.endsWith(";")){
//						friends = new Friends(reMsg,name);
//						friendList = friends.getFriends();
//						System.out.println(reMsg);
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
//
//		}
//	}
//  
//	/**
//	 * 通过handler更新UI
//	 */
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
//	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		disConnect();
//		//System.exit(0);
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		menu.add(0, 1, 1, "初始化設置");
//		menu.add(0, 2, 2, "退出");
//		menu.add(0,3,3,"朋友");
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		if(item.getItemId()==1){
//			Intent intent = new Intent(SocketmsgActivity.this,IniActivity.class);
//			startActivity(intent);
//			SocketmsgActivity.this.finish();
//		}else if(item.getItemId()==2){
//			disConnect();
//			SocketmsgActivity.this.finish();  
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
//		}else if(item.getItemId()==3){
//			Intent intent = new Intent(SocketmsgActivity.this,FriendsListActivity.class);
//			intent.putExtra("friends", "aaa,bbb,ccc,dddd");
//			startActivity(intent);
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	public void InitDatabase(){
//		 
//        if(!config.path.exists()){  
//        	config.path.mkdirs();    
//        	Log.i("LogDemo", "mkdir");  
//        }   
//        if(!config.f.exists()){      
//        	try{   
//        		config.f.createNewFile();  
//        		Log.i("LogDemo", "create a new database file");
//        	}catch(IOException e){   
//        		Log.i("LogDemo",e.toString());
//        	}   
//        }  
//		try {
//			if(tabIsExist("config")==false){
//				db = SQLiteDatabase.openOrCreateDatabase(config.f, null);  
//				db.execSQL("create table config(_id integer primary key autoincrement," +
//						"ip varchar(128),port varchar(10),name varchar(32))");
//				Log.i("LogDemo", "create a database");
//				db.close();
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			Log.i("LogDemo",e.toString());
//		}
//	}
//	
//	/**
//	 * check the database is already exist
//	 * @param tabName
//	 * @return
//	 */
//	public boolean tabIsExist(String tabName){
//        boolean result = false;
//        if(tabName == null){
//                return false;
//        }
//        Cursor cursor = null;
//        db = SQLiteDatabase.openOrCreateDatabase(config.f, null); 
//        try {
//        	String sql = "select count(*) as c from sqlite_master where type ='table' " +
//                		"and name ='"+tabName.trim()+"' ";
//            cursor = db.rawQuery(sql, null);
//            if(cursor.moveToNext()){
//            	int count = cursor.getInt(0);
//            	if(count>0){
//            		result = true;
//            	}
//            }
//                
//        } catch (Exception e) {
//                // TODO: handle exception
//        }  
//        cursor.close();
//        db.close();
//        return result;
//	}
//}