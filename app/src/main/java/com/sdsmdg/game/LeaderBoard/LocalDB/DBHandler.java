package com.sdsmdg.game.LeaderBoard.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.sdsmdg.game.Launcher;

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

    public boolean checkDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) AS NumberOfRows FROM " + SQLITE_TABLE + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int entities = c.getInt(c.getColumnIndex("NumberOfRows"));
        Log.i(TAG, "Number of rows : " + entities);
        return (entities == 0);

    }

    public String getUserName() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT  " + KEY_NAME + " from " + SQLITE_TABLE + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return (c.getString(c.getColumnIndex("name")));
    }

    public void updateDatabase(int score, Launcher context) {
        SQLiteDatabase db = getWritableDatabase();
        if (score > getPastHighScore()) {
            String updateQuery = "UPDATE " + SQLITE_TABLE
                    + " SET " + KEY_SCORE + "='" + score + "';";
            db.execSQL(updateQuery);
            Toast.makeText(context.getApplicationContext(), "Your HighScore is updated to : " + score, Toast.LENGTH_SHORT).show();
        }
    }

    public int getPastHighScore() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT  " + KEY_SCORE + " from " + SQLITE_TABLE + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int pastHighScore = c.getInt(c.getColumnIndex("score"));
        Log.i(TAG, "Previous High Score : " + String.valueOf(pastHighScore));
        return pastHighScore;
    }
}
