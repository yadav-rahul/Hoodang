package com.sdsmdg.game.LeaderBoard.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Rahul Yadav on 6/24/2016.
 */
public class DBHandler extends SQLiteOpenHelper {


    public static final String KEY_NAME = "name";
    public static final String KEY_SCORE = "score";
    private static final String TAG = "com.sdsmdg.game";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "highScore.db";
    private static final String SQLITE_TABLE = "scores";
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_NAME + " PRIMARY KEY," +
                    KEY_SCORE + "," +
                    " UNIQUE (" + KEY_NAME + "));";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
        onCreate(db);
    }

    public void addProfile(Profile profile) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, profile.getUserName());
        values.put(KEY_SCORE, profile.getScore());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(SQLITE_TABLE, null, values);
    }

    public void checkDatabase(){

    }
    public void updateDatabase(int score){
        SQLiteDatabase db = getWritableDatabase();

    }
}
