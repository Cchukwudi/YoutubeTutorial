package com.example.youtubetutorial;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openEditorNewNote();

        cursorAdapter = new NotesCursorAdapter(this,
                null, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivit.class);
                Uri uri = Uri.parse(NoteProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NoteProvider.CONTENT_ITEM_TYPE,uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });


        getLoaderManger().initLoader(0, null, this );
    }

    private void openEditorNewNote() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), EditorActivit.class);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
    }

    private void getLoaderManger() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NoteProvider. CONTENT_URI,
                values);
        Log.d(TAG, "Inserted note: "+noteUri.getLastPathSegment());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_create_sample:
                insertSampleDate();
                break;

            case R.id.action_delete_all:
                deleteAllNotes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void insertSampleDate(){
        insertNote("Simple note");
        insertNote("Multi-Line\nnote");
        insertNote("Very Long note with a lot of text that exceeds the width of the screen");
        //each time you change the data in the database, you need to tell your loader object that it needs
        //to restart, that it needs to re-read the data from the back-end database
         restartLoader();
    }

    private void restartLoader() {
        getLoaderManger().restartLoader(0, null, this);
    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert data management code here
                            getContentResolver().delete(
                                    NoteProvider.CONTENT_URI,
                                    null, //delete everything
                                    null
                            );

                            restartLoader();

                            Toast.makeText(MainActivity.this,
                                    R.string.All_Deleted,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, NoteProvider.CONTENT_URI,
                null, null, null, null);
        // here we set the projection, that's the list of columns, to null because that's already coded in the provider
        //selection to null, means that I want all the data
        // and we're not using selectionArgs and sortOrder
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //the onLoadFinished is receiving a cursor object. When you create the cursor the cursor loader object
        //it executes the query method pn the background thread
        //and when the data comes back, onLoadFinished is called. our job is to take the data represented
        // by the cursor object, named data and pass it to the cursor adapter. we do that with
        //cursorAdapter.swapCursor
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //This is called whenever the data needs to be wiped out
        cursorAdapter.swapCursor(null);
    }
}
