package fr.dao.app.Core.Database;

import android.util.Log;

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.dao.app.Model.Config.Session;

public class                                DBSessions {
    private static String                   TAG = "DBSessions";

    public static List<Session>             getAllSession() {
        Log.d(TAG, "::getAllSession");
        return new Select()
                .all()
                .from(Session.class)
                .orderBy("date ASC")
                .execute();
    }

    /**
     * Get or create Session for the day
     * @return
     */
    public static Session                    getSession() {
        Log.d(TAG, "::getSession");
        List<Session> sessions = getAllSession();
        Calendar cal2 = Calendar.getInstance(), cal1 = Calendar.getInstance();
        if (sessions != null)
            for (Session session : sessions) {
                cal2.setTime(session.date);
                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                    Log.d(TAG, "::returning session -> " + session.getDateString());
                    return session;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd_MMMM_HH#mm-ss", Locale.FRANCE);
                    Log.d(TAG, "Session not matching s1[" + sdf.format(cal1.getTime())+ "] s2[" + sdf.format(cal2.getTime()) + "]");
                }
            }
        else
            Log.d(TAG, "no sessions in BDD");
        return createSession();
    }

    private static Session                  createSession() {
        Log.d(TAG, "::creatingSession");
        Session session = new Session();
        session.date = Calendar.getInstance().getTime();
        session.save();
        return session;
    }
}
