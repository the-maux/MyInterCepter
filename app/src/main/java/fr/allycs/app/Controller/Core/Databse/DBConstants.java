package fr.allycs.app.Controller.Core.Databse;

import android.provider.BaseColumns;

public class DBConstants implements BaseColumns {

    // The database name
    public static final String DATABASE_NAME = "myData.db";

    // The database version
    public static final int DATABASE_VERSION = 1;

    // The table Name
    public static final String MY_TABLE = "Human";

    // ## Column name ##
    // My Column ID and the associated explanation for end-users
    public static final String KEY_COL_ID = "_id";// Mandatory

    // My Column Name and the associated explanation for end-users
    public static final String KEY_COL_NAME = "name";

    // My Column First Name and the associated explanation for end-users
    public static final String KEY_COL_FIRSTNAME = "firstName";

    // My Column Eyes Color and the associated explanation for end-users
    public static final String KEY_COL_EYES_COLOR = "eyesColor";

    // My Column Hair color and the associated explanation for end-users
    public static final String KEY_COL_HAIR_COLOR = "hairColor";

    // My Column age and the associated explanation for end-users
    public static final String KEY_COL_AGE = "age";

    // Indexes des colonnes
    // The index of the column ID
    public static final int ID_COLUMN = 1;

    // The index of the column NAME
    public static final int NAME_COLUMN = 2;

    // The index of the column FIRST NAME
    public static final int FIRSTNAME_COLUMN = 3;

    // The index of the column EYES COLOR
    public static final int EYES_COLOR_COLUMN = 4;

    // The index of the column HAIR COLOR
    public static final int HAIR_COLOR_COLUMN = 5;

    // The index of the column AGE
    public static final int AGE_COLUMN = 6;
}
