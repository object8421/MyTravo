package com.cobra.mytravo.data;

import com.cobra.mytravo.models.UserSync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class UserSyncDataHelper extends BaseDataHelper
{
	public UserSyncDataHelper(Context context)
	{
		super(context);
	}

	@Override
	protected Uri getContentUri()
	{
		return DataProvider.USERSYNC_CONTENT_URI;
	}
	
	private ContentValues getContentValues(UserSync usersync)
	{
		ContentValues values = new ContentValues();
		values.put(UserSyncInfoDB.TOKEN, usersync.getToken());
		values.put(UserSyncInfoDB.JSON, usersync.toJson());
		return values;
	}
	
	public int update(UserSync usersync)
	{
		return this.update(getContentValues(usersync), UserSyncInfoDB.TOKEN + "=?", new String[]{usersync.getToken()});
	}
	public void insert(UserSync usersync)
	{
		this.insert(getContentValues(usersync));
	}
	
	public UserSync getByToken(String token)
	{
		Cursor cursor = query(null, UserSyncInfoDB.TOKEN + "=?", new String[]{token}, null);
		if(cursor.moveToFirst())
		{
			return UserSync.fromCursor(cursor);
		}
		return null;
	}
	
	public static final class UserSyncInfoDB implements BaseColumns
	{
		public static final String TABLE_NAME = "usersync";
		
		public static final String TOKEN = "token";
		
		public static final String JSON = "json";
		
		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
						.addColumn(TOKEN, Column.DataType.TEXT)
						.addColumn(JSON, Column.DataType.TEXT);
	} 
}
