package com.cst2335.recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MyOpenHelper extends SQLiteOpenHelper {

    public static final String filename = "MyDatabase";
    public static final int version = 1;
    public static final String TABLE_NAME = "MyData";
    public static final String KEY_ID = "_id";
    public static final String COL_ingredient = "ingredient";
    public static final String COL_title = "title";
    public static final String COL_url = "url";
    public static final boolean COL_isFavorite = false;

    public MyOpenHelper(Context context) {
        super(context, filename, null, version);
    }

    // should be the creation statement
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table MyData ( _id INTEGER PRIMARY KEY AUTOINCREMENT, Message TEXT, SendOrReceive INTEGER, TimeSent TEXT );
        String result = String.format(" %s %s %s", "FirstString" , "10", "10.0" );

        //                                      //TABLE_NAME               take care of id numbers
        db.execSQL( String.format( "Create table %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER);"
                , TABLE_NAME, KEY_ID,COL_ingredient, COL_title, COL_url,COL_isFavorite));

    }
    // delete current table, create a new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "Drop table if exists " + TABLE_NAME ); //deletes the current data
        //create a new table:

        this.onCreate(db); //calls function on line 26
    }

}