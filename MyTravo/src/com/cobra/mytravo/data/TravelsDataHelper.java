package com.cobra.mytravo.data;

import java.util.ArrayList;
import java.util.List;

import com.cobra.mytravo.models.Travel;



import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Contacts.Intents.Insert;
import android.util.Log;

public class TravelsDataHelper extends BaseDataHelper{
	private int user_id = 0;
	public TravelsDataHelper(Context context, int user_id) {
		super(context);
		this.user_id = user_id;
	}

	@Override
	protected Uri getContentUri() {
		// TODO Auto-generated method stub
		return DataProvider.TRAVELS_CONTENT_URI;
	}
	private ContentValues getContentValues(Travel travel) {
        ContentValues values = new ContentValues();
        values.put(TravelsDBInfo.ID, travel.getId());
        values.put(TravelsDBInfo.USER_ID, this.user_id);
        values.put(TravelsDBInfo.JSON, travel.toJson());
        values.put(TravelsDBInfo.TIME, travel.getCreate_time());
        values.put(TravelsDBInfo.DIRTY, travel.getIs_sync());
        values.put(TravelsDBInfo.IS_DELETED, travel.getIs_deleted());
        return values;
    }
	public Travel queryById(long id) {
        Travel travel = null;
        Cursor cursor = query(null, TravelsDBInfo.USER_ID + "=?" + " AND " + TravelsDBInfo.ID + "= ?"
        		+ " AND " + TravelsDBInfo.IS_DELETED + "=?",
                new String[] {
                        String.valueOf(this.user_id), String.valueOf(id), String.valueOf(0)
                }, null);
        if (cursor.moveToFirst()) {
        	travel = Travel.fromCursor(cursor);
        }
        cursor.close();
        return travel;
    }
	
	public List<Travel> getNewTravels()
	{
		int i = 1;
		List<Travel> travels = new ArrayList<Travel>();
		Cursor cursor = query(null, TravelsDBInfo.USER_ID + "=? AND " + TravelsDBInfo.ID + "=?",
						new String[]{String.valueOf(this.user_id), String.valueOf(0)}, null);
		Log.i("cursor", cursor.getColumnName(4));
		Log.i("cursorsize", String.valueOf(cursor.getCount()));
		if(cursor.moveToFirst())
		{
			Travel t  = Travel.fromCursor(cursor);
			Log.i("dirty", String.valueOf(t.getIs_sync()));
			t.setTAG(i);
			travels.add(t);
		}
		while(cursor.moveToNext())
		{
			i++;
			Travel t  = Travel.fromCursor(cursor);
			Log.i("dirty", String.valueOf(t.getIs_sync()));
			t.setTAG(i);
			travels.add(t);
		}
		cursor.close();
		return travels;
	}
	
	public List<Travel> getDirtyTravels()
	{
		int i = 1;
		List<Travel> travels = new ArrayList<Travel>();
		Cursor cursor = query(null, TravelsDBInfo.USER_ID + "=? AND " + TravelsDBInfo.DIRTY + "=?",
						new String[]{String.valueOf(this.user_id), String.valueOf(1)}, null);
		Log.i("cursor", cursor.getColumnName(4));
		Log.i("cursorsize", String.valueOf(cursor.getCount()));
		if(cursor.moveToFirst())
		{
			Travel t  = Travel.fromCursor(cursor);
			Log.i("dirty", String.valueOf(t.getIs_sync()));
			t.setTAG(i);
			travels.add(t);
		}
		while(cursor.moveToNext())
		{
			i++;
			Travel t  = Travel.fromCursor(cursor);
			Log.i("dirty", String.valueOf(t.getIs_sync()));
			t.setTAG(i);
			travels.add(t);
		}
		cursor.close();
		return travels;
	}
	
	public Travel queryByTime(String time) {
        Travel travel = null;
        Cursor cursor = query(null, TravelsDBInfo.USER_ID + "=?" + " AND " + TravelsDBInfo.TIME + "= ?",
                new String[] {
                        String.valueOf(this.user_id), String.valueOf(time)
                }, null);
        if (cursor.moveToFirst()) {
        	travel = Travel.fromCursor(cursor);
        }
        cursor.close();
        return travel;
    }
	
	public void insert(Travel travel) {
		this.insert(getContentValues(travel));
	}
	public void bulkInsert(List<Travel> travels) {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (Travel travel : travels) {
            ContentValues values = getContentValues(travel);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }
	public int update(Travel newTravel){
		return this.update(getContentValues(newTravel), TravelsDBInfo.USER_ID + "=? AND "
	+ TravelsDBInfo.TIME + "=?", new String[]{String.valueOf(newTravel.getUser_id()),
			newTravel.getCreate_time()});
	}
	//notice that we only set the is_deleted value from 0 to 1 for deletion
	public int delete(Travel deleteTravel){
		return this.update(getContentValues(deleteTravel), TravelsDBInfo.USER_ID + "=? AND "
				+ TravelsDBInfo.TIME + "=?", new String[]{String.valueOf(deleteTravel.getUser_id()),
			deleteTravel.getCreate_time()});
	}
	public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, 
        		TravelsDBInfo.USER_ID + "=?"
        		+ " AND " + TravelsDBInfo.IS_DELETED + "=?",
                new String[] {
        	String.valueOf(this.user_id), String.valueOf(0)
                }, TravelsDBInfo.TIME + " DESC");
    }
	public static final class TravelsDBInfo implements BaseColumns {
        private TravelsDBInfo() {
        }

        public static final String TABLE_NAME = "travels";

        public static final String ID = "id";
        
        public static final String USER_ID = "user_id";
        
        public static final String TIME = "created_time";
        
        public static final String DIRTY = "is_sync";
        
        //begin add by L!ar 2013/12/29 
        public static final String IS_DELETED = "is_deleted";
        //end add by L!ar
        
        public static final String JSON = "json";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(ID, Column.DataType.INTEGER)
                .addColumn(USER_ID, Column.DataType.INTEGER)
                .addColumn(TIME, Column.DataType.TEXT)
                .addColumn(DIRTY, Column.DataType.INTEGER)
                .addColumn(IS_DELETED, Column.DataType.INTEGER)
                .addColumn(JSON, Column.DataType.TEXT);
    }
}