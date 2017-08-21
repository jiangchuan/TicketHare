package io.chizi.tickethare.database;

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
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;

import io.chizi.tickethare.R;

/**
 * Created by Jiangchuan on 5/21/17.
 */

// Remember to declare the provider in manifest, or your queries might return null!
public class DBProvider extends ContentProvider {

    // The Java namespace for the Content Provider
    static final String PROVIDER_NAME = "io.chizi.tickethare.database.DBProvider";

    public static final String KEY_ROW_ID = "id";
    public static final String KEY_TICKET_ID = "ticket_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_LICENSE_NUM = "license_num";
    public static final String KEY_LICENSE_COLOR = "license_color";
    public static final String KEY_CAR_TYPE = "car_type";
    public static final String KEY_CAR_COLOR = "car_color";
    public static final String KEY_DATETIME = "datetime";
    public static final String KEY_YEAR = "year";
    public static final String KEY_MONTH = "month";
    public static final String KEY_WEEK = "week";
    public static final String KEY_DAY = "day";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_MAP_URI = "mapURI";
    public static final String KEY_FAR_IMG_URI = "farImgURI";
    public static final String KEY_CLOSE_IMG_URI = "closeImgURI";
    public static final String KEY_TICKET_IMG_URI = "ticketImgURI";
    public static final String KEY_IS_UPLOADED = "isUploaded";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_POLICE_NAME = "police_name";
    public static final String KEY_POLICE_TYPE = "police_type";
    public static final String KEY_POLICE_DEPT = "police_dept";
    public static final String KEY_POLICE_SQUAD = "police_squad";
    public static final String KEY_POLICE_SECTION = "police_section";
    public static final String KEY_POLICE_PORTRAIT_URI = "police_portrait";
    public static final String KEY_POLICE_CITY = "police_city";

    public static final String KEY_TICKET_RANGE_START = "ticket_range_start";
    public static final String KEY_TICKET_RANGE_END = "ticket_range_end";

    // Assigned to a content provider so any application can access it
    // tickets is the virtual directory in the provider
    public static final Uri TICKET_URL = Uri.parse("content://" + PROVIDER_NAME + "/tickets");
    public static final Uri POLICE_URL = Uri.parse("content://" + PROVIDER_NAME + "/polices");
    public static final Uri RANGE_URL = Uri.parse("content://" + PROVIDER_NAME + "/ranges");
    static final int ticketUriCode = 1;
    static final int policeUriCode = 2;
    static final int rangeUriCode = 3;

    private static HashMap<String, String> values;

    // Used to match uris with Content Providers
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "tickets", ticketUriCode);
        uriMatcher.addURI(PROVIDER_NAME, "polices", policeUriCode);
        uriMatcher.addURI(PROVIDER_NAME, "ranges", rangeUriCode);
    }

    private SQLiteDatabase sqlDB;
    private static final String DATABASE_NAME = "Transportation";
    private static final String TICKET_TABLE_NAME = "tickets";
    private static final String POLICE_TABLE_NAME = "polices";
    private static final String RANGE_TABLE_NAME = "ranges";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TICKET_TABLE = "CREATE TABLE if not exists " + TICKET_TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TICKET_ID + " INTEGER NOT NULL DEFAULT -1 UNIQUE ON CONFLICT REPLACE, "
            + KEY_USER_ID + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_LICENSE_NUM + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_LICENSE_COLOR + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_CAR_TYPE + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_CAR_COLOR + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_DATETIME + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_YEAR + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_MONTH + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_WEEK + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_DAY + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_HOUR + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_MINUTE + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_ADDRESS + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_LONGITUDE + " DOUBLE NOT NULL DEFAULT -1, "
            + KEY_LATITUDE + " DOUBLE NOT NULL DEFAULT -1, "
            + KEY_MAP_URI + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_FAR_IMG_URI + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_CLOSE_IMG_URI + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_TICKET_IMG_URI + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_IS_UPLOADED + " INTEGER NOT NULL DEFAULT -1);";


    private static final String CREATE_POLICE_TABLE = "CREATE TABLE if not exists " + POLICE_TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USER_ID + " TEXT NOT NULL DEFAULT 'NA' UNIQUE ON CONFLICT REPLACE, "
            + KEY_PASSWORD + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_NAME + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_TYPE + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_CITY + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_DEPT + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_SQUAD + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_SECTION + " TEXT NOT NULL DEFAULT 'NA', "
            + KEY_POLICE_PORTRAIT_URI + " TEXT NOT NULL DEFAULT 'NA');";

    private static final String CREATE_RANGE_TABLE = "CREATE TABLE if not exists " + RANGE_TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USER_ID + " TEXT NOT NULL DEFAULT 'NA' UNIQUE ON CONFLICT REPLACE, "
            + KEY_TICKET_RANGE_START + " INTEGER NOT NULL DEFAULT -1, "
            + KEY_TICKET_RANGE_END + " INTEGER NOT NULL DEFAULT -1);";



    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if (sqlDB != null) {
            return true;
        }
        return false;
    }

    // Returns a cursor that provides read and write access to the results of the query
    // Uri : Links to the table in the provider (The From part of a query)
    // projection : an array of columns to retrieve with each row
    // selection : The where part of the query selection
    // selectionArgs : The argument part of the where (where id = 1)
    // sortOrder : The order by part of the query
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Used to create a SQL query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case ticketUriCode:
                // Set table to query
                queryBuilder.setTables(TICKET_TABLE_NAME);
                // A projection map maps from passed column names to database column names
                queryBuilder.setProjectionMap(values);
                break;
            case policeUriCode:
                queryBuilder.setTables(POLICE_TABLE_NAME);
                queryBuilder.setProjectionMap(values);
                break;
            case rangeUriCode:
                queryBuilder.setTables(RANGE_TABLE_NAME);
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Cursor provides read and write access to the database
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null,
                null, sortOrder);

        // Register to watch for URI changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Handles requests for the MIME type (Type of Data) of the data at the URI
    @Override
    public String getType(Uri uri) {
        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            // vnd.android.cursor.dir/tickets states that we expect multiple pieces of data
            case ticketUriCode:
                return "vnd.android.cursor.dir/tickets";
            case policeUriCode:
                return "vnd.android.cursor.dir/polices";
            case rangeUriCode:
                return "vnd.android.cursor.dir/ranges";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // Used to insert a new row into the provider
    // Receives the URI (Uniform Resource Identifier) for the Content Provider and a set of values
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {
            case ticketUriCode:
                // Gets the row id after inserting a map with the keys representing the the column
                // names and their values. The second attribute is used when you try to insert
                // an empty row
//                long ticketRowID = sqlDB.insert(TICKET_TABLE_NAME, null, values);
                long ticketRowID = sqlDB.insertWithOnConflict(TICKET_TABLE_NAME, KEY_TICKET_ID, values,
                        SQLiteDatabase.CONFLICT_REPLACE);

                // Verify a row has been added
                if (ticketRowID > 0) {
                    // Append the given id to the path and return a Builder used to manipulate URI
                    // references
                    _uri = ContentUris.withAppendedId(TICKET_URL, ticketRowID);
                    // getContentResolver provides access to the content model
                    // notifyChange notifies all observers that a row was updated
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case policeUriCode:
                long policeRowID = sqlDB.insertWithOnConflict(POLICE_TABLE_NAME, KEY_USER_ID, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (policeRowID > 0) {
                    _uri = ContentUris.withAppendedId(POLICE_URL, policeRowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case rangeUriCode:
                long rangeRowID = sqlDB.insertWithOnConflict(RANGE_TABLE_NAME, KEY_USER_ID, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (rangeRowID > 0) {
                    _uri = ContentUris.withAppendedId(RANGE_URL, rangeRowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
//                throw new IllegalArgumentException("Unknown URI " + uri);
                throw new SQLException("Failed to insert row into " + uri);
        }
        Toast.makeText(getContext(), R.string.toast_insert_db_failed, Toast.LENGTH_LONG).show();
        // Return the Builder used to manipulate the URI
        return null;
    }

    // Deletes a row or a selection of rows
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case ticketUriCode:
                rowsDeleted = sqlDB.delete(TICKET_TABLE_NAME, selection, selectionArgs);
                break;
            case policeUriCode:
                rowsDeleted = sqlDB.delete(POLICE_TABLE_NAME, selection, selectionArgs);
                break;
            case rangeUriCode:
                rowsDeleted = sqlDB.delete(RANGE_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContentResolver provides access to the content model
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    // Used to update a row or a selection of rows
    // Returns to number of rows updated
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case ticketUriCode:
                // Update the row or rows of data
                rowsUpdated = sqlDB.update(TICKET_TABLE_NAME, values, selection, selectionArgs);
                break;
            case policeUriCode:
                rowsUpdated = sqlDB.update(POLICE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case rangeUriCode:
                rowsUpdated = sqlDB.update(RANGE_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContentResolver provides access to the content model
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    // Creates and manages our database
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_TICKET_TABLE);
            sqlDB.execSQL(CREATE_POLICE_TABLE);
            sqlDB.execSQL(CREATE_RANGE_TABLE);
        }

        // Recreates the table when the database needs to be upgraded
        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TICKET_TABLE_NAME);
            sqlDB.execSQL("DROP TABLE IF EXISTS " + POLICE_TABLE_NAME);
            sqlDB.execSQL("DROP TABLE IF EXISTS " + RANGE_TABLE_NAME);
            onCreate(sqlDB);
        }
    }

}
