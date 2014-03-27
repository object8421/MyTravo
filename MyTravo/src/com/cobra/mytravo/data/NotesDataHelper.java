package com.cobra.mytravo.data;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

import com.cobra.mytravo.data.TravelsDataHelper.TravelsDBInfo;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;

/**
 * Created by L!ar 4/9/13
 */
public class NotesDataHelper extends BaseDataHelper
{
	private int user_id;
//	private String travel_created_time;

	public NotesDataHelper(Context context, int user_id)
	{
		super(context);
		this.user_id = user_id;
//		this.travel_created_time = travel_created_time;
	}

	@Override
	protected Uri getContentUri()
	{
		return DataProvider.NOTES_CONTENT_URI;
	}

	private ContentValues getContentValues(Note note)
	{
		ContentValues values = new ContentValues();
		values.put(NotesDBInfo.ID, note.getId());
		values.put(NotesDBInfo.USER_ID, this.user_id);
		values.put(NotesDBInfo.TIME, note.getCreate_time());
		values.put(NotesDBInfo.TRAVEL_CREATED_TIME, note.getTravel_created_time());
		values.put(NotesDBInfo.TRAVEL_ID, note.getTravel_id());
		values.put(NotesDBInfo.IS_DELETED, note.getIs_deleted());
		values.put(NotesDBInfo.DIRTY, note.getIs_sync());
		values.put(NotesDBInfo.JSON, note.toJson());
		return values;
	}

	public void insert(Note note)
	{
		this.insert(getContentValues(note));
	}

	public void bulkInsert(List<Note> notes)
	{
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
		for (Note note : notes)
		{
			ContentValues values = getContentValues(note);
			contentValues.add(values);
		}
		ContentValues[] valueArray = new ContentValues[contentValues.size()];
		bulkInsert(contentValues.toArray(valueArray));
	}
	
	public List<Note> getDirtyNotes()
	{
		int i = 1;
		List<Note> notes = new ArrayList<Note>();
		Cursor cursor = query(null, NotesDBInfo.USER_ID + "=? AND " 
							+ NotesDBInfo.DIRTY + "=?"
							, new String[]{String.valueOf(user_id), "1"}, null);
		if(cursor.moveToFirst())
		{
			Note note = Note.fromCursor(cursor);
			note.setTAG(i);
			notes.add(note);
		}
		while(cursor.moveToNext())
		{
			i++;
			Note note = Note.fromCursor(cursor);
			note.setTAG(i);
			notes.add(note);
		}
		Log.i("notes", String.valueOf(notes.size()));
		return notes;    
	}
	
	public Note getNoteById(int id)
	{
		Cursor cursor = query(null, NotesDBInfo.ID + "=?", new String[]{String.valueOf(id)}, null);
		if(cursor.moveToFirst())
		{
			Note note = Note.fromCursor(cursor);
			return note;
		}
		return null;
	}
	
	public List<Note> getNewNotes()
	{
		int i = 1;
		List<Note> notes = new ArrayList<Note>();
		Cursor cursor = query(null, NotesDBInfo.USER_ID + "=? AND "
							+ NotesDBInfo.ID + "=?", new String[]{String.valueOf(user_id)
							, "0"}, null);
		if(cursor.moveToFirst())
		{
			Note note = Note.fromCursor(cursor);
			note.setTAG(i);
			notes.add(note);
		}
		while(cursor.moveToNext())
		{
			i++;
			Note note = Note.fromCursor(cursor);
			note.setTAG(i);
			notes.add(note);
		}
		Log.i("notes", String.valueOf(notes.size()));
		return notes;
	}
	
	public List<Note> getNotesByTravel(String travel_created_time)
	{
		List<Note> notes = new ArrayList<Note>();
		Cursor cursor = query(null, NotesDBInfo.USER_ID + "=? AND " 
							+ NotesDBInfo.TRAVEL_CREATED_TIME + "=?",
							new String[]{String.valueOf(user_id), travel_created_time}, 
							NotesDBInfo.TIME + " ASC");
		if(cursor.moveToFirst())
		{
			Note note = Note.fromCursor(cursor);
			notes.add(note);
		}
		while(cursor.moveToNext())
		{
			Note note = Note.fromCursor(cursor);
			notes.add(note);
		}
		Log.i("notes", String.valueOf(notes.size()));
		return notes;
	}
	
	public int update(Note newNote)
	{
		return this.update(
				getContentValues(newNote),
				NotesDBInfo.USER_ID + "=? AND "
						+ NotesDBInfo.TIME + "=?",
				new String[] { String.valueOf(newNote.getUser_id()),
						 newNote.getCreate_time() });
	}
	
	// notice that we only set the is_deleted value from 0 to 1 for deletion
	public int delete(Note deleteNote)
	{
		return this.update(
				getContentValues(deleteNote),
				NotesDBInfo.USER_ID + "=? AND "
						+ NotesDBInfo.TRAVEL_CREATED_TIME + "=?"
						+ NotesDBInfo.TIME + "=?",
				new String[] { String.valueOf(deleteNote.getUser_id()),
						deleteNote.getTravel_created_time(),
						deleteNote.getCreate_time() });
	}

	public int getCountByTravel(String travel_created_time)
	{
		return DatabaseUtils.queryCount(DataProvider.getDBHelper()
				.getReadableDatabase(), NotesDBInfo.TABLE_NAME,
				NotesDBInfo.USER_ID + "=?" + " AND "
						+ NotesDBInfo.TRAVEL_CREATED_TIME + "=?" + " AND "
						+ TravelsDBInfo.IS_DELETED + "=?",
				new String[] { String.valueOf(user_id), travel_created_time,
						String.valueOf(0) });
	}
	
	public CursorLoader getCursorLoader(int travel_id, String travel_created_time)
	{
		if(travel_id != 0)
		{
			return new CursorLoader(getContext(), getContentUri(), null, 
					NotesDBInfo.USER_ID + "=?" + " AND " + NotesDBInfo.TRAVEL_ID + "=?" 
					+ " AND " + TravelsDBInfo.IS_DELETED + "=?", new String[]{
					String.valueOf(user_id), String.valueOf(travel_id), "0"},
					NotesDBInfo._ID + " ASC");
		}
		return new CursorLoader(getContext(), getContentUri(), null,
				NotesDBInfo.USER_ID + "=?" + " AND "
						+ NotesDBInfo.TRAVEL_CREATED_TIME + "=?" + " AND "
						+ TravelsDBInfo.IS_DELETED + "=?", new String[] {
						String.valueOf(user_id), travel_created_time,
						String.valueOf(0) }, NotesDBInfo._ID + " ASC");
	}
	
	public static final class NotesDBInfo implements BaseColumns
	{
		private NotesDBInfo()
		{
		}

		public static final String TABLE_NAME = "notes";

		public static final String ID = "id";

		public static final String USER_ID = "user_id";

		public static final String TRAVEL_CREATED_TIME = "travel_created_time";
		
		public static final String TRAVEL_ID = "travel_id";

		public static final String CONTENT = "content";

		public static final String TIME = "create_time";

		public static final String IS_DELETED = "is_deleted";
		
		public static final String DIRTY = "is_sync";

		public static final String JSON = "json";

		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
				.addColumn(ID, Column.DataType.INTEGER)
				.addColumn(USER_ID, Column.DataType.INTEGER)
				.addColumn(TRAVEL_CREATED_TIME, Column.DataType.TEXT)
				.addColumn(TRAVEL_ID, Column.DataType.INTEGER)
				.addColumn(CONTENT, Column.DataType.TEXT)
				.addColumn(TIME, Column.DataType.TEXT)
				.addColumn(IS_DELETED, Column.DataType.INTEGER)
				.addColumn(DIRTY, Column.DataType.INTEGER)
				.addColumn(JSON, Column.DataType.TEXT);
	}
}
