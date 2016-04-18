package sleek.SocketChat;

import java.io.IOException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class myFunction{
	
	public void InitDatabase(){
		
        if(!config.path.exists()){  
        	config.path.mkdirs();    
        	Log.i("LogDemo", "mkdir");  
        }   
        if(!config.f.exists()){      
        	try{   
        		config.f.createNewFile();  
        		Log.i("LogDemo", "create a new database file");
        	}catch(IOException e){   
        		Log.i("LogDemo",e.toString());
        	}   
        }  
		try {
			if(tabIsExist("config")==false){
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(config.f, null);  
				db.execSQL("create table config(_id integer primary key autoincrement," +
						"ip varchar(128),port varchar(10),name varchar(32))");
				Log.i("LogDemo", "create a database");
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("LogDemo",e.toString());
		}
	}
	

	
	/**
	 * check the database is already exist
	 * @param tabName
	 * @return
	 */
	public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
                return false;
        }
        Cursor cursor = null;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(config.f, null); 
        try {
        	String sql = "select count(*) as c from sqlite_master where type ='table' " +
                		"and name ='"+tabName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
            	int count = cursor.getInt(0);
            	if(count>0){
            		result = true;
            	}
            }
                
        } catch (Exception e) {
                // TODO: handle exception
        }  
        cursor.close();
        db.close();
        return result;
	}
}