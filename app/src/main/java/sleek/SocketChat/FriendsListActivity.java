package sleek.SocketChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import sleek.Provider.DataChangeProvider;
import sleek.Provider.DataSqlite;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsListActivity extends ListActivity{

	private List<Map<String, Object>> friendList;
	Friends friends;
	myFunction mf = new myFunction();
	public MyAdapter myAdapter=null;
	
    private String name=null,ip=null,port=null;
    private String[] fansArray;
    private SQLiteDatabase db;
    
	Thread thread = null,threadwait = null;
    SocketThread s,ws;

    DataInputStream dis = null;
    DataOutputStream dos = null;
    
    DataInputStream wdis = null;
    DataOutputStream wdos = null;
    private String reMsg=null,wreMsg;
    private Boolean isContect = false,iswContect = false;;
    
    SocketMap smMap;
    DataSqlite fanDS;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		//DataSqlite mDataSqlite = new DataSqlite(this);
		//初始化，创建数据库来储存用户信息
		try {
			mf.InitDatabase();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		
		db = SQLiteDatabase.openOrCreateDatabase(config.f, null);
		try {
			Cursor cursor = db.query("config", new String[]{"ip","name","port"},null,null, null, null, null);
			while(cursor.moveToNext()){
				name = cursor.getString(cursor.getColumnIndex("name"));
				ip = cursor.getString(cursor.getColumnIndex("ip"));
				port = cursor.getString(cursor.getColumnIndex("port"));
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		db.close();
		
		//设置连接
		if(ip==null || port==null){
			Intent intent = new Intent(FriendsListActivity.this,IniActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
		}
		//设置名称
		else if(name==null){
			Intent intent = new Intent(FriendsListActivity.this,IniuserActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
		}else{
			
			//获取好友列表
			fanDS = new DataSqlite(this);
			fansArray = fanDS.getFans();
			//System.out.println("fansString:"+fansString);
			friends = new Friends(fansArray,null,name);
			friendList = friends.getFriends();
			
			myAdapter = new MyAdapter(this);
			setListAdapter(myAdapter);
			
			connect();
			waitGod();
		}
		
		
	}
	private Runnable doThread = new Runnable() {
		public void run() {
			System.out.println("running!");
		    ReceiveMsg();
		}
	};   
	
	private Runnable waitThread = new Runnable() {
		public void run() {
			System.out.println("wait running!");
		    WaitMsg();
		}
	}; 
	
	/**
	 * 等候命令
	 */
	public void waitGod(){
		try {
    		ws = new SocketThread();
    		ws.SocketStart(ip,port,name);
    		if(ws.isConnected()){
	    		wdos = ws.getDOS();
	    		wdis = ws.getDIS();
	    		wdos.writeUTF(config.PROTOCOL_KEY+config.PROTOCOL_WAIT+name);

	    		threadwait = new Thread(null, waitThread, "waitGod");
	    		threadwait.start();
	  			System.out.println(ws+"connect");
	  			iswContect=true;
    		}
  		}catch (UnknownHostException e) {
  			System.out.println("連接失敗");
			Toast.makeText(FriendsListActivity.this, "連接失敗", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(FriendsListActivity.this,IniActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
  			e.printStackTrace();
  		}catch (SocketTimeoutException  e) {
  			System.out.println("連接超時，服務器未開啟或IP錯誤");
  			Toast.makeText(FriendsListActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(FriendsListActivity.this,IniActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
  			e.printStackTrace();
  		}catch (IOException e) {
  			System.out.println("連接失敗");
  			e.printStackTrace();
  		}
	}
    
    public void connect() {
    	try {
    		s = new SocketThread();
    		s.SocketStart(ip,port,"friends");
    		if(s.isConnected()){
	    		dos = s.getDOS();
	    		dis = s.getDIS();
	    		dos.writeUTF(config.PROTOCOL_KEY+config.PROTOCOL_ONLINE+name);
	    		/**
	    		 * 这里是关键，我在此耗时8h+
	    		 * 原因是 子线程不能直接更新UI
	    		 * 为此，我们需要通过Handler物件，通知主线程Ui Thread来更新界面。
	    		 * 
	    		 */
	    		thread = new Thread(null, doThread, "Message");
	  			thread.start();
	  			//System.out.println(s+"connect");
	  			isContect=true;
    		}
  		}catch (UnknownHostException e) {
  			System.out.println("連接失敗");
			Toast.makeText(FriendsListActivity.this, "連接失敗", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(FriendsListActivity.this,IniActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
  			e.printStackTrace();
  		}catch (SocketTimeoutException  e) {
  			System.out.println("連接超時，服務器未開啟或IP錯誤");
  			Toast.makeText(FriendsListActivity.this, "連接超時，服務器未開啟或IP錯誤", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(FriendsListActivity.this,IniActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
  			e.printStackTrace();
  		}catch (IOException e) {
  			System.out.println("連接失敗");
  			e.printStackTrace();
  		}
    }
   
    public void disConnect() {
    	if(dos!=null){
	    	try {
	    		dos.writeUTF(config.PROTOCOL_KEY+config.PROTOCOL_OFFLINE+name);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	s.AllClose();
    	}
    }
  
    
    /**
     * 线程监视Server信息
     */
	private void ReceiveMsg() {
//		for (Iterator iterator=SocketMap.socketMap.entrySet().iterator(); iterator.hasNext();) {
//			Map.Entry elementEntry = (Map.Entry) iterator.next();
//			String socketnameString = (String) elementEntry.getKey();
//			Socket socketObject = (Socket) elementEntry.getValue();
//			System.out.println(socketnameString+" : "+socketObject);
//		}
		if (isContect) {
			try {
				while ((reMsg = dis.readUTF()) != null) {
					System.out.println("friends list:"+reMsg);
					if(reMsg.startsWith(config.PROTOCOL_FRIENDS_START) && reMsg.endsWith(config.PROTOCOL_FRIENDS_END)){
						
						//更新好友数据库
						fanDS.updateData(reMsg,name);
						//获取好友列表
						fansArray = fanDS.getFans();
						
						friends = new Friends(fansArray,reMsg,name);
						friendList = friends.getFriends();
						
						
						//System.out.println(reMsg);
						try {
							Message msgMessage = new Message();
							msgMessage.what = 0x1981;
							handler.sendMessage(msgMessage);
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
					else if (reMsg != null) {

						try {
							Message msgMessage = new Message();
							msgMessage.what = 0x1981;
							handler.sendMessage(msgMessage);
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			} catch (SocketException e) {
				// TODO: handle exception
				System.out.println("exit!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
	}
	
	private void WaitMsg() {
		if (iswContect) {
			try {
				while ((wreMsg = wdis.readUTF()) != null) {
					if (wreMsg != null) {
						if(wreMsg.indexOf("[")>0){
							String subName=wreMsg.substring(0,wreMsg.indexOf("[")-1);
							String subDate=wreMsg.substring(wreMsg.indexOf("[")+1,wreMsg.indexOf("]")-1);
							String subContent=wreMsg.substring(wreMsg.indexOf("\n")+"\n".length());
							ContentValues con = new ContentValues();
							con.put("fromChat", subName.trim());
							con.put("nameChat", subName.trim());
							con.put("contentChat", subContent);
							con.put("typeChat", "text");
							con.put("postdateChat", subDate);
							con.put("isreadChat", "0");
							con.put("deleteChat", "0");
							getContentResolver().insert(DataChangeProvider.CONTENT_URI, con);
						}
					}
				}
			} catch (SocketException e) {
				// TODO: handle exception
				System.out.println("exit!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
  
	/**
	 * 通过handler更新UI
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x1981:
//				chatbox.setText(chatbox.getText() + reMsg + '\n');
//				chatbox.setSelection(chatbox.length());
				myAdapter = new MyAdapter(FriendsListActivity.this);
				setListAdapter(myAdapter);
				break;
			}
		}
	};
	

	
	public List<Map<String, Object>> setFriends(List<Map<String, Object>> sl){
		this.friendList= sl;
		return friendList;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(FriendsListActivity.this,FriendChatActivity.class);
		intent.putExtra("friendname", (String)friendList.get(position).get("friendname"));
		intent.putExtra("ip", ip);
		intent.putExtra("port", port);
		intent.putExtra("name", name);
		//System.out.println("s"+s.getSocket());
		startActivity(intent);
		//function.showDialog((String)mData.get(position).get("text"), MyListViewActivity.this);
		//Toast.makeText(MyListViewActivity.this, (String)mData.get(position).get("text"), Toast.LENGTH_LONG).show(); 
	}
	
	public final class ViewHolder{
		public ImageView img;
		public TextView text,ofline;	
	}
	
	public class MyAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		ViewHolder holder = null;
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return friendList.size();
		}
	
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder=new ViewHolder();  
				
				convertView = mInflater.inflate(R.layout.list, null);
				holder.img = (ImageView)convertView.findViewById(R.id.img);
				holder.text = (TextView)convertView.findViewById(R.id.text);
				holder.ofline = (TextView)convertView.findViewById(R.id.ofline);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
	
			holder.img.setBackgroundResource((Integer)friendList.get(position).get("avatar"));
			//把字符串设置在TextView上显示
			holder.text.setText((String)friendList.get(position).get("friendname"));
			holder.ofline.setText((String)friendList.get(position).get("ofline"));
			
			if(friendList.get(position).get("ofline")=="(online)"){
				//System.out.println("online");
				holder.ofline.setTextColor(Color.rgb(255, 255, 255));
			}
			return convertView;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		disConnect();
		db.close();
		//System.exit(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "初始化設置");
		menu.add(0, 2, 2, "退出");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1){
			Intent intent = new Intent(FriendsListActivity.this,IniActivity.class);
			startActivity(intent);
			FriendsListActivity.this.finish();
		}else if(item.getItemId()==2){
			disConnect();
			FriendsListActivity.this.finish();  
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
		return super.onOptionsItemSelected(item);
	}
}