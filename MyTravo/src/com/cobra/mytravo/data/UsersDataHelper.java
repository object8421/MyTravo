package com.cobra.mytravo.data;

import com.cobra.mytravo.models.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class UsersDataHelper extends BaseDataHelper
{

	public UsersDataHelper(Context context)
	{
		super(context);
	}

	@Override
	protected Uri getContentUri()
	{
		return DataProvider.USERS_CONTENT_URI;
	}
	
	private ContentValues getContentValues(User user)
	{
		ContentValues values = new ContentValues();
		values.put(UserInfoDB.ID, user.getId());
		values.put(UserInfoDB.TOKEN, user.getToken());
		values.put(UserInfoDB.JSON, user.toJson());
		return values;
	}
	
	public void insert(User user)
	{
		this.insert(getContentValues(user));
	}
	
	public User getByToken(String token)
	{
		Cursor cursor = query(null, UserInfoDB.TOKEN + "=?", new String[]{token}, null);
		if(cursor.moveToFirst())
		{
			return User.fromCursor(cursor);
		}
		return null;
	}
	
	public static final class UserInfoDB implements BaseColumns
	{
		public static final String TABLE_NAME = "users";
		public static final String ID = "id";
		public static final String TOKEN = "token";
		public static final String JSON = "json";
		
		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
					.addColumn(ID, Column.DataType.INTEGER)
					.addColumn(TOKEN, Column.DataType.TEXT)
					.addColumn(JSON, Column.DataType.TEXT);
	}
}
