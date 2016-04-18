package sleek.Provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;

public class DataFriendsProvider extends ContentProvider{
	private SQLiteOpenHelper mOpenHelper;
	private static final int ALARMS = 1;
	private static final int ALARMS_ID = 2;
    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final Uri CONTENT_URI = Uri.parse("content://sleek.Provider/friends"); 

    static {
        sURLMatcher.addURI("sleek.Provider", "friends", ALARMS);
        sURLMatcher.addURI("sleek.Provider", "friends/#", ALARMS_ID);
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper{
		 private static final String FRIENDS_DATABASE = "friends.db";
         private static final int VERSION = 1;
         
         public DatabaseHelper(Context context) {
 			super(context, FRIENDS_DATABASE, null, VERSION);
 			// TODO Auto-generated constructor stub
 		}
 		 

 		@Override
 		public void onCreate(SQLiteDatabase db) {	
 			//好友
 			String sqlfriend = "CREATE TABLE "+"friends"+" (" +
 					"_id INTEGER PRIMARY KEY," +
 					"nameFan varchar(64),"+ 
 					"remarknameFan varchar(64),"+ 
 					"firstnameFan varchar(32),"+ 
 					"lastnameFan varchar(32),"+ 
 					"phoneFan varchar(32),"+ 
 					"avatarFan varchar(64),"+ 
 					"postdateFan DATETIME,"+
 					"lastonlinedateFan DATETIME,"+
 					"deleteFan integer"+
 					 ");";
 			db.execSQL(sqlfriend);
 		}

 		@Override
 		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 			String sql = "DROP TABLE IF EXIST "+FRIENDS_DATABASE;
 			db.execSQL(sql);
 			onCreate(db);
 		}
    	
    }
    
    public DataFriendsProvider() {
    }
    
	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        long rowId = 0;
        switch (sURLMatcher.match(url)) {
            case ALARMS:
                count = db.delete("friends", where, whereArgs);
                break;
            case ALARMS_ID:
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete("friends", where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
	}

	@Override
	public String getType(Uri url) {
		int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		if (sURLMatcher.match(url) != ALARMS) {
            throw new IllegalArgumentException("Cannot insert into URL: " + url);
        }

        ContentValues values;
        if (initialValues != null) {
        	values = new ContentValues(initialValues);
        } else {
        	values = new ContentValues();
        }
            
        if (!values.containsKey("nameFan"))
            values.put("nameFan", "");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert("friends", null, values);
        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + url);
        }
        Uri newUrl = ContentUris.withAppendedId(CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri url, String[] projection, String where,
			String[] whereArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                qb.setTables("friends");
                break;
            case ALARMS_ID:
                qb.setTables("friends");
                qb.appendWhere("_id=");
                qb.appendWhere(url.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cur = qb.query(db, projection, where, whereArgs,null, null, sortOrder);
        if (cur != null) {
        	cur.setNotificationUri(getContext().getContentResolver(), url);
        	
        } 
        return cur;
	}

	@Override
	public int update(Uri url, ContentValues values, String where,String[] whereArgs) {
		int count;
        ContentValues values2;
        if (values != null) {
        	values2 = new ContentValues(values);
        } else {
        	values2 = new ContentValues();
        }
        //long rowId = 0;
        //int match = sURLMatcher.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
      //  switch (match) {
          //  case ALARMS_ID: {
                //String segment = url.getPathSegments().get(1);
                //rowId = Long.parseLong(segment);
                count = db.update("friends", values2, where, null);
              // break;
           // }
           // default: {
            //    throw new UnsupportedOperationException(
            //            "Cannot update URL: " + url);
           // }
      //  }
       if (count < 0) {
           throw new SQLException("Failed to update row "+url);
        }
        getContext().getContentResolver().notifyChange(url, null);
        return count;
	}

}
