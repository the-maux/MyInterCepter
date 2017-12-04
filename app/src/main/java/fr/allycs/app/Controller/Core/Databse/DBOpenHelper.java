package fr.allycs.app.Controller.Core.Databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class                    DBOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE_DEVICE_TABLE =
            "create table " + DBConstants.MY_TABLE +
            "(" + DBConstants.KEY_COL_ID + " integer primary key autoincrement, " +
                    DBConstants.KEY_COL_AGE + " INTEGER, " +
                    DBConstants.KEY_COL_NAME + " TEXT, " +
                    DBConstants.KEY_COL_FIRSTNAME + " TEXT, " +
                    DBConstants.KEY_COL_EYES_COLOR + " TEXT, " +
                    DBConstants.KEY_COL_HAIR_COLOR + " TEXT) ";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void                 onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_DEVICE_TABLE);
    }

    @Override
    public void                 onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DBOpenHelper", "Mise à jour de la version " + oldVersion
                + " vers la version " + newVersion
                + ", les anciennes données seront détruites ");
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.MY_TABLE);
        onCreate(db);
    }
}
