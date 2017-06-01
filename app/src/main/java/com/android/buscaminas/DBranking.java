package com.androidya.buscaminas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by codeline on 25/03/17.
 */

public class DBranking extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "rankings.db";

    public static final String TABLA_NOMBRE = "puntuaciones";
    public static final String COLUMNA_ID   = "_id";
    public static final String COLUMNA_NOMBRE   = "nombre";
    public static final String COLUMNA_TIEMPO   = "tiempo";

    private static final String SQL_CREAR = "create table "
            + TABLA_NOMBRE + "("
            + COLUMNA_ID        + " integer primary key autoincrement, "
            + COLUMNA_NOMBRE    + " text not null, "
            + COLUMNA_TIEMPO    + " real not null);";

    public DBranking(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void agregar(String nombre, String tiempo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE, nombre);
        values.put(COLUMNA_TIEMPO, tiempo);

        db.insert(TABLA_NOMBRE, null, values);
        db.close();

    }
}
