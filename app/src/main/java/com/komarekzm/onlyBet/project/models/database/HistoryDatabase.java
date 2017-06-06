package com.komarekzm.onlyBet.project.models.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by komarekzm on 2/15/2017.
 * Tutorial
 * *
 */

public class HistoryDatabase {
    private static final String TABLE_NAME = "history";
    private static final String COLUMN_ENTRY_ID = "entry_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_WIN = "win";
    private static final String COLUMN_COURSE = "course";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LEAGUE = "league";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIPS_DATA = "data";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "history.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private HistoryDatabase.HistoryDatabaseHelper helper;
    private static HistoryDatabase database;

    public static HistoryDatabase getInstance(Context c) {
        if (database == null) {
            database = new HistoryDatabase(c);
        }
        return database;
    }


    private HistoryDatabase(Context c) {
        helper = new HistoryDatabase.HistoryDatabaseHelper(c);
    }

    public ArrayList getAllData() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Tip> result = new ArrayList<>();
        Gson gson = new Gson();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                Tip tip = gson.fromJson(
                        c.getString(
                                c.getColumnIndex(COLUMN_TIPS_DATA)
                        ),
                        Tip.class);
                result.add(tip);
            }
            while (c.moveToNext());
        }

        c.close();
        db.close();
        return result;
    }

    public long insertData(Tip tip) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        ContentValues val = new ContentValues();

        Gson gson = new Gson();

        val.put(COLUMN_NAME, tip.getName());
        val.put(COLUMN_WIN, tip.getWin());
        val.put(COLUMN_COURSE, tip.getCourse());
        val.put(COLUMN_TIME, tip.getTime());
        val.put(COLUMN_LEAGUE, tip.getLeague());
        val.put(COLUMN_DATE, tip.getDate());
        val.put(COLUMN_TIPS_DATA, gson.toJson(tip, Tip.class));

        long id = db.insert(TABLE_NAME, null, val);
        c.close();
        db.close();
        return id;
    }

    public long insertAllData(List<Tip> tipList) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        ContentValues val = new ContentValues();

        Gson gson = new Gson();

        for (int i = 0; i < tipList.size(); i++) {
            val.put(COLUMN_NAME, tipList.get(i).getName());
            val.put(COLUMN_WIN, tipList.get(i).getWin());
            val.put(COLUMN_COURSE, tipList.get(i).getCourse());
            val.put(COLUMN_TIME, tipList.get(i).getTime());
            val.put(COLUMN_LEAGUE, tipList.get(i).getLeague());
            val.put(COLUMN_DATE, tipList.get(i).getDate());
            val.put(COLUMN_TIPS_DATA, gson.toJson(tipList.get(i), Tip.class));
            db.insert(TABLE_NAME, null, val);
        }
        c.close();
        db.close();
        return 1;
    }

    public long deleteTip(Tip tip) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(COLUMN_DATE))
                        .equals(tip.getDate())
                        ) {
                    String selection = COLUMN_ENTRY_ID + " LIKE ?";
                    String[] selectionArgs = {
                            String.valueOf(c.getString(c.getColumnIndex(COLUMN_ENTRY_ID)))
                    };

                    long id = db.delete(TABLE_NAME, selection, selectionArgs);
                    c.close();
                    db.close();
                    return id;
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return -1;
    }

    public long deleteAllData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
        return 1;
    }

        /*------------------------------Database Helper------------------------*/

    private static class HistoryDatabaseHelper extends SQLiteOpenHelper {
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_WIN + TEXT_TYPE + COMMA_SEP +
                COLUMN_COURSE + TEXT_TYPE + COMMA_SEP +
                COLUMN_TIME + TEXT_TYPE + COMMA_SEP +
                COLUMN_LEAGUE + TEXT_TYPE + COMMA_SEP +
                COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_TIPS_DATA + TEXT_TYPE +
                " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Context context;


        HistoryDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

}

