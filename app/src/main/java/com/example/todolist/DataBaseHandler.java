package com.example.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {

    // Classe pour créer la base de données

    public DataBaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableElement = "CREATE TABLE 'tasks' ( 'name' TEXT, 'deadline' INTEGER, 'group_name' TEXT, 'participant_name' TEXT, 'priority' INTEGER);";
        db.execSQL(createTableElement);

        String createTableRecipe = "CREATE TABLE 'participants' ( 'participant_name' TEXT, 'group_name' TEXT, 'color' TEXT);";
        db.execSQL(createTableRecipe);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks;");
        onCreate(db);
    }

}
