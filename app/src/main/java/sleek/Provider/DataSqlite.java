package sleek.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sleek.SocketChat.config;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSqlite {
	DataSqliteHelper mDataHelper;
	Context mContext;
	SQLiteDatabase db;
	Cursor fcur=null;
	public DataSqlite(Context context){
		this.mContext = context;
		mDataHelper = new DataSqliteHelper(context);
	}
	
	public void saveData(String nameFan,String remarknameFan,String firstnameFan,String lastnameFan,String phoneFan,
			String avatarFan,String postdateFan,String lastonlinedateFan,String deleteFan){
		ContentValues conValues = new ContentValues();
		conValues.put("nameFan", nameFan);
		conValues.put("remarknameFan", remarknameFan);
		conValues.put("firstnameFan", firstnameFan);
		conValues.put("lastnameFan", lastnameFan);
		conValues.put("phoneFan", phoneFan);
		conValues.put("avatarFan", avatarFan);
		conValues.put("postdateFan", postdateFan);
		conValues.put("lastonlinedateFan", lastonlinedateFan);
		conValues.put("deleteFan", deleteFan);
		SQLiteDatabase db = mDataHelper.getWritableDatabase();
		db.insert("friends", null, conValues);
		db.close();
	}
	
	/**
	 * 更新好友数据库
	 * @param fans   好友字符串
	 * @param me     自己，从字符串中排除
	 */
	public void updateData(String fans,String me){
		String[] str;
		String[] getfans;
		getfans=getFans();
		//System.out.println("getfans: "+getfans);
		if(fans!=null){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = df.format(new Date());
            
			fans=fans.substring(config.PROTOCOL_FRIENDS_START.length(), fans.length()-config.PROTOCOL_FRIENDS_END.length());
			str = fans.split(config.PROTOCOL_FRIENDS_SEPARATE);
			for (int i = 0; i < str.length; i++) {
				if(!str[i].equals(me)){
					//int find = Arrays.binarySearch(getfans, str[i]); 
					//if(find<0){
					//	saveData(str[i],str[i],str[i],str[i],null,null,date,null,"0");
					//}
					int count=0;
					if(getfans!=null){
					for(int j=0;j<getfans.length;j++)
					{
						System.out.println("getfans["+j+"]=" +getfans[j]+"   str["+i+"]="+str[i]);
						if(getfans[j]!=null){
							if(getfans[j].equals(str[i])){
						        count++;
						    }
						}
					}
					}
					if(count==0){
						saveData(str[i].trim(),str[i],str[i],str[i],null,null,date,null,"0");
					}

				} 
			}
		}
	}
	
	/**
	 * 获取好友列表字符串
	 * @return
	 */
	public String getData(){
		Cursor cur=null;
		String fansString = "";
		try {
			cur = getCursor();
			//fcur.close();
			if(cur != null){
				if(cur.getCount() != 0){
					fansString+=config.PROTOCOL_FRIENDS_START;
					while(cur.moveToNext()){
						fansString+=cur.getString(cur.getColumnIndex("nameFan"))+config.PROTOCOL_FRIENDS_SEPARATE;
					}
					fansString+=config.PROTOCOL_FRIENDS_END;
					return fansString;
				}
				cur.close();
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
		
		return null;
		
	}
	
	/**
	 * 获取好友列表数组
	 * @return
	 */
	public String[] getFans(){
		Cursor cur=null;
		try {
			cur = getCursor();
			//fcur.close();
			if(cur != null){
				if(cur.getCount() != 0){
					String[] fans = new String[cur.getCount()];
					int i=0;
					while(cur.moveToNext()){
						fans[i] = cur.getString(cur.getColumnIndex("nameFan"));
						i++;
					}
					return fans;
				}
				
			}
		} finally{
			if(cur !=null){
				try {
					cur.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return null;
		
	}
	
	private Cursor getCursor(){
		SQLiteDatabase db = mDataHelper.getReadableDatabase();
		fcur = db.query("friends", new String[]{"_id","nameFan","remarknameFan","firstnameFan",
				"lastnameFan","phoneFan","avatarFan","postdateFan","lastonlinedateFan","deleteFan"},
				null, null, null, null, null);
		return fcur;
		
	}
	
	class DataSqliteHelper extends SQLiteOpenHelper{
        private static final String FRIENDS_DATABASE = "friends.db";
        private static final String FRIENDS_TABLE = "friends";
        private static final int VERSION = 1;
        
		public DataSqliteHelper(Context context) {
			super(context, FRIENDS_DATABASE, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//好友
 			String sqlfriend = "CREATE TABLE "+FRIENDS_TABLE+" (" +
 					"_id INTEGER PRIMARY KEY," +
 					"nameFan varchar(64) NOT NULL,"+ 
 					"remarknameFan varchar(64),"+ 
 					"firstnameFan varchar(32),"+ 
 					"lastnameFan varchar(32),"+ 
 					"phoneFan varchar(32),"+ 
 					"avatarFan varchar(64),"+ 
 					"postdateFan date,"+
 					"lastonlinedateFan date,"+
 					"deleteFan integer NOT NULL DEFAULT (1)"+
 					 ");";
 			db.execSQL(sqlfriend);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "DROP TABLE IF EXIST "+FRIENDS_TABLE;
			db.execSQL(sql);
		}
	}

}
