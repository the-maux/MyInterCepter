package fr.dao.app.Core.Configuration;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class                Words {

    public static String    yes(Context context) {
        return context.getResources().getString(android.R.string.yes);
    }

    public static String    no(Context context) {
        return context.getResources().getString(android.R.string.no);
    }

    public static String    getGenericDateFormat(Date date) {
        return new SimpleDateFormat("dd_MMMM_HH#mm-ss", Locale.FRANCE).format(date)
                .replace("#", "h").replace("-", "m").replace("é", "e");
    }

    public static String    getGenericLightDateFormat(Date date) {
        return new SimpleDateFormat("dd_MMMM_HH#mm-ss", Locale.FRANCE).format(date)
                .replace("#", "h").replace("-", "m").replace("é", "e");
    }
}
