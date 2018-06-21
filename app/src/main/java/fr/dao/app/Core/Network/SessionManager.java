package fr.dao.app.Core.Network;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.dao.app.Core.Configuration.Comparator.Comparators;
import fr.dao.app.Core.Database.DBSessions;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Config.Session;

public class                        SessionManager {
    private String                  TAG = "SessionManager";
    private List<Session>           sessionsFromBDD;
    private ArrayList<Session>      loadedSessions = new ArrayList<>();;

    public SessionManager() {
        update();
    }

    private ArrayList<Session>      getSessionBetweenDate(Date start, Date end) {
        if (start == null || end == null) {
            return loadedSessions;
        }
        loadedSessions.clear();
        for (Session session : sessionsFromBDD) {
            if (session.date.before(end) && session.date.after(start))
                loadedSessions.add(session);
        }
        Collections.sort(loadedSessions, Comparators.getSessionComparator());
        return loadedSessions;
    }

    public ArrayList<Session>       update() {
        Log.d(TAG, "update");
        sessionsFromBDD = DBSessions.getAllSession();
        Collections.sort(sessionsFromBDD, Comparators.getSessionComparator());
        loadedSessions = new ArrayList<>(sessionsFromBDD);
        return loadedSessions;
    }

    public  int                     getNbrSessionsRecorded() {
        return (sessionsFromBDD == null) ? 0 : sessionsFromBDD.size() ;
    }

    public List<Entry>              getEntryFromLoadedSessionsByType(Action.TeamAction type) {
        List<Entry> attackEntry = new ArrayList<>();
        if (loadedSessions == null)
            getSessionBetweenDate(null, null);
        for (int offsetSession = 0; offsetSession < loadedSessions.size(); offsetSession++) { //For nbr network in networkFocused
            attackEntry.add(new Entry(offsetSession, loadedSessions.get(offsetSession).getNbrActionType(type)));
        }
        return attackEntry;
    }

    public List<Session>            getSessionsFromDate(Date start, Date end) {
        return (start == null && end == null) ? sessionsFromBDD : getSessionBetweenDate(start, end);
    }

    public List<Entry>              getFakeAttackEntry() {
        List<Entry> attackEntry = new ArrayList<Entry>();
        int raxattack = 0;
        for (;raxattack< 10;raxattack++) { //For nbr network in networkFocused
            switch (raxattack) {
                case 1:
                    attackEntry.add(new Entry(0f, 5f));
                    break;
                case 2:
                    attackEntry.add(new Entry(1f, 2f));
                    break;
                case 3:
                    attackEntry.add(new Entry(2f, 6f));
                    break;
                case 4:
                    attackEntry.add(new Entry(3f, 4f));
                    break;
                case 5:
                    attackEntry.add(new Entry(4f, 8f));
                    break;
                case 6:
                    attackEntry.add(new Entry(5f, 4f));
                    break;
                case 7:
                    attackEntry.add(new Entry(6f, 7f));
                    break;
            }
        }
        return attackEntry;
    }

    public List<Entry>              getFakeDefenseEntry() {
        List<Entry> defenseEntry = new ArrayList<Entry>();
        //FOR TEST X: nbrAttack Y: nbrDef
        //Simulate 9 Session
        int raxattack = 0;
        for (;raxattack< 10;raxattack++) { //For nbr network in networkFocused
            switch (raxattack) {
                case 1:
                    defenseEntry.add(new Entry(0f, 2f));
                    break;
                case 2:
                    defenseEntry.add(new Entry(1f, 7f));
                    break;
                case 3:
                    defenseEntry.add(new Entry(2f, 4f));
                    break;
                case 4:
                    defenseEntry.add(new Entry(3f, 17f));
                    break;
                case 5:
                    defenseEntry.add(new Entry(4f, 12f));
                    break;
                case 6:
                    defenseEntry.add(new Entry(5f, 2f));
                    break;
                case 7:
                    defenseEntry.add(new Entry(6f, 5f));
                    break;
            }
        }
        return defenseEntry;
    }

    public Session                  getSessionFromOffset(float value) {
        return loadedSessions.get((int)value);
    }

    public ArrayList<Session>       getActualListSessions() {
        return loadedSessions;
    }
}
