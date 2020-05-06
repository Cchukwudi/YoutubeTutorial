package com.example.youtubetutorial;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Reading and inflating layout fpr the note_list_item, and passing it back whenever the newView
        //method is called
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item,
                parent,
                false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //When you bind the view, you receive an instance of the cursor object, and it will already point to the
        //particular row of your database that's supposed to be displayed.
        String noteText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT)
        );

        //we'll check whether there's a line feed

        int pos = noteText.indexOf(10);// 10 is ascii value of line feed character
        if(pos != -1){
            noteText = noteText.substring(0, pos)+"...";
        }
        TextView tv = view.findViewById(R.id.tvNote);
        tv.setText(noteText);
    }
}
