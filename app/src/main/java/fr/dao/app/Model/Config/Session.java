package fr.dao.app.Model.Config;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.dao.app.Core.Configuration.Singleton;

@Table(name = "Session", id = "_id")
public class                Session extends Model {
    @Column(name = "Date")
    public Date             date;

    public List<Action>     Actions() {
        return getMany(Action.class, "Session");
    }

    public String           getDateString() {
        if (date == null)
            return "Not recorded";
        return new SimpleDateFormat("dd/MMMM", Locale.FRANCE).format(date);
    }

    public void             addAction(Action.actionType type, boolean isOffensif) {
        Action action = new Action();
        action.date = Calendar.getInstance().getTime();
        if (isOffensif)
            action.offensifAction = action.offensifAction + 1;
        else
            action.defensifAction = action.defensifAction + 1;
        if (Singleton.getInstance().CurrentNetwork != null)
            action.network = Singleton.getInstance().CurrentNetwork;
        action.session = this;
        action.type = type;
        action.save();
        this.Actions().add(action);
        this.save();
    }

    public                  Session() {
        super();
    }
}
