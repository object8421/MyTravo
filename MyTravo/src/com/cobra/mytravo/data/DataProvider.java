
package com.cobra.mytravo.data;





import android.R.integer;
import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Issac on 7/18/13.
 */
public class DataProvider extends ContentProvider {
    private static final String TAG = "DataProvider";

    static final Object DBLock = new Object();

    public static final String AUTHORITY = "com.cobra.mytravo.data.DataProvider";

    public static final String SCHEME = "content://";

    // messages
    public static final String PATH_SHOTS = "/shots";
    public static final String PATH_USERS = "/users";
    public static final String PATH_NOTES = "/notes";
    public static final String PATH_TRAVELS = "/travels";
    
    public static final Uri SHOTS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_SHOTS);
    public static final Uri USERS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_USERS);
    public static final Uri NOTES_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_NOTES);
    public static final Uri TRAVELS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_TRAVELS);
   
    private static final int SHOTS = 1;
    private static final int USERS = 2;
    private static final int NOTES = 3;
    private static final int TRAVELS = 4;
    /*
     * MIME type definitions
     */
    public static final String SHOT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cobra.shot";
    
    
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "shots", SHOTS);
        sUriMatcher.addURI(AUTHORITY, "users", USERS);
        sUriMatcher.addURI(AUTHORITY, "notes", NOTES);
        sUriMatcher.addURI(AUTHORITY, "travels", TRAVELS);
    }

    private static DBHelper mDBHelper;

    public static DBHelper getDBHelper() {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(AppData.getContext());
        }
        return mDBHelper;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SHOTS:
                return SHOT_CONTENT_TYPE;
           
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private String matchTable(Uri uri) {
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case SHOTS:
                table = ShotsDataHelper.ShotsDBInfo.TABLE_NAME;
                break;
            
            case NOTES:
            	table = NotesDataHelper.NotesDBInfo.TABLE_NAME;
            	break;
            case TRAVELS:
            	table = TravelsDataHelper.TravelsDBInfo.TABLE_NAME;
            	break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        synchronized (DBLock) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            String table = matchTable(uri);
            queryBuilder.setTables(table);

            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, // The database to
                                                   // queryFromDB
                    projection, // The columns to return from the queryFromDB
                    selection, // The columns for the where clause
                    selectionArgs, // The values for the where clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder // The sort order
                    );

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        synchronized (DBLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insert(table, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        synchronized (DBLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    db.insertWithOnConflict(table, BaseColumns._ID, contentValues,
                            SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return values.length;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();

            int count = 0;
            String table = matchTable(uri);
            db.beginTransaction();
            try {
                count = db.delete(table, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count;
            String table = matchTable(uri);
            db.beginTransaction();
            try {
                count = db.update(table, values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);

            return count;
        }
    }

    static class DBHelper extends SQLiteOpenHelper {
        // 数据库名
        private static final String DB_NAME = "mytravo.db";

        // 数据库版本
        private static final int VERSION = 1;

        private DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            ShotsDataHelper.ShotsDBInfo.TABLE.create(db);
            TravelsDataHelper.TravelsDBInfo.TABLE.create(db);
            NotesDataHelper.NotesDBInfo.TABLE.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
