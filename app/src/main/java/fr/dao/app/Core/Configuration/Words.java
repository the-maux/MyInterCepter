package fr.dao.app.Core.Configuration;

import android.content.Context;

public class                Words {

    public static String    yes(Context context) {
        return context.getResources().getString(android.R.string.yes);
    }

    public static String    no(Context context) {
        return context.getResources().getString(android.R.string.no);
    }
}
