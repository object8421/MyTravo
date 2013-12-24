package com.cobra.mytravo.data;

import java.util.ArrayList;
import java.util.List;

import com.cobra.mytravo.models.Travel;



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
	public TravelsDataHelper(Context context) {
		super(context);
//		this.user_id = user_id;
	}

	@Override
	protected Uri getContentUri() {
		// TODO Auto-generated method stub
		return DataProvider.TRAVELS_CONTENT_URI;
	}
	private ContentValues getContentValues(Travel travel) {
        ContentValues values = new ContentValues();
        values.put(TravelsDBInfo.ID, travel.getTravel_id());
        values.put(TravelsDBInfo.USER_ID, this.user_id);
        values.put(TravelsDBInfo.JSON, travel.toJson());
        values.put(TravelsDBInfo.TIME, travel.getCreated_time());
        return values;
    }
	public Travel queryById(long id) {
        Travel travel = null;
        Cursor cursor = query(null, TravelsDBInfo.USER_ID + "=?" + " AND " + TravelsDBInfo.ID + "= ?",
                new String[] {
                        String.valueOf(this.user_id), String.valueOf(id)
                }, null);
        if (cursor.moveToFirst()) {
        	travel = Travel.fromCursor(cursor);
        }
        cursor.close();
        return travel;
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
	public CursorLoader getCursorLoader() {
		Log.i("CursorLoader", "JFJJJJJJJJJJJJJJJJJJJJJJJJJJJ");
        return new CursorLoader(getContext(), getContentUri(), null, TravelsDBInfo.USER_ID + "=?",
                new String[] {
        	String.valueOf(this.user_id)
                }, TravelsDBInfo._ID + " ASC");
    }
	public static final class TravelsDBInfo implements BaseColumns {
        private TravelsDBInfo() {
        }

        public static final String TABLE_NAME = "travels";

        public static final String ID = "travel_id";
        
        public static final String USER_ID = "user_id";
        
        public static final String TIME = "created_time";

        public static final String JSON = "json";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(ID, Column.DataType.INTEGER)
                .addColumn(USER_ID, Column.DataType.INTEGER)
                .addColumn(TIME, Column.DataType.TEXT)
                .addColumn(JSON, Column.DataType.TEXT);
    }
}
