
package com.cobra.mytravo.data;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

import com.cobra.mytravo.models.Note;






/**
 * Created by L!ar 4/9/13
 */
public class NotesDataHelper extends BaseDataHelper {
    private int user_id;
    private String travel_created_time;
    public NotesDataHelper(Context context, int user_id, String travel_created_time) {
        super(context);
        this.user_id = user_id;
        this.travel_created_time = travel_created_time;
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.NOTES_CONTENT_URI;
    }
    
    private ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(NotesDBInfo.ID, note.getNote_id());
        values.put(NotesDBInfo.USER_ID, this.user_id);
        values.put(NotesDBInfo.TIME, note.getTime());
        values.put(NotesDBInfo.TRAVEL_CREATED_TIME, note.getTravel_created_time());
        return values;
    }
	
	
   

   
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, NotesDBInfo.USER_ID + "=?" + " AND " +NotesDBInfo.TRAVEL_CREATED_TIME + "=?",
                new String[] {
        	String.valueOf(user_id),travel_created_time
                }, NotesDBInfo._ID + " ASC");
    }

    public static final class NotesDBInfo implements BaseColumns {
        private NotesDBInfo() {
        }
        
        public static final String TABLE_NAME = "notes";

        public static final String ID = "note_id";

        public static final String USER_ID = "user_id";
        
        public static final String TRAVEL_CREATED_TIME = "travel_created_time";
        
        public static final String CONTENT = "content";
        
        public static final String TIME = "time";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(ID, Column.DataType.INTEGER)
                .addColumn(USER_ID, Column.DataType.INTEGER)
                .addColumn(TRAVEL_CREATED_TIME, Column.DataType.TEXT)
                .addColumn(CONTENT, Column.DataType.TEXT)
                .addColumn(TIME, Column.DataType.TEXT);
                
    }
}
