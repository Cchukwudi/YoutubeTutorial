package com.example.youtubetutorial;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteProvider extends ContentProvider {



    // globally unique string that identifies the content provider to the Android framework
    private static  final String AUTHORITY = "com.example.youtubetutorial.noteprovider";

    // This represents the entire data set
    private static final  String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    //CONTENT_URI is a uniform resource identifier that identifies the content provider


    // Constants to identify the requested operation. The numeric values are arbitrary.
    //these are simply ways of identifying the operations and they're private so they're only used within
    //this class. Notes means give me the data
    private static  final int NOTES = 1;
    // NOTES_ID deal with a single record
    private static  final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher=
            new UriMatcher(UriMatcher.NO_MATCH);// the purpose of te UriMatcher calls is to parse a URI and then tell which operations
    //has been requested

    public static final  String CONTENT_ITEM_TYPE = "Note";

    static {
        //this block will execute the first time anything is called from this class
        uriMatcher.addURI(AUTHORITY,BASE_PATH,NOTES);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#", NOTES_ID);// # is a wild card, it means any numerical value,
        //that means if I get a URI that starts with base_path and then ends with a / and a number that means I'm looking
        //for a particular note, a particular row in the database table.

    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if(uriMatcher.match(uri) == NOTES_ID){
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
                selection, null, null, null,
                DBOpenHelper.NOTE_CREATED + " DESC ");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // the insert method returns a URI. And that URI is supposed to match this pattern: the base_patch followed by
        // a/ and then the primary key value of the record
        // so first step is to get that primary key value
        long id = database.insert(DBOpenHelper.TABLE_NOTES,
                null, values);// content values is a class that has a collection of name value pairs
        // content values is very similar to bundle class in android, but the bundle class tends to be used to manage
        // the user interface. Whereas ContentValues is used to pass data around on the backend.
        return Uri.parse(BASE_PATH + "/" + id); // the parse method lets you put together a string and returns the equivalent Uri.
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES, selection,selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES,values, selection, selectionArgs);
    }
}
