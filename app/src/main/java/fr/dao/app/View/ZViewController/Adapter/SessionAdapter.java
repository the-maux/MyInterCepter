package fr.dao.app.View.ZViewController.Adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.GenericLittleCardAvatarHolder;


public class                        SessionAdapter extends RecyclerView.Adapter<GenericLittleCardAvatarHolder> {
    private String                  TAG = "SessionAdapter";
    private Singleton               mSingleton = Singleton.getInstance();
    private MyActivity              mActivity;
    private List<Session>           mSessions;
    private int                     color;

    public                          SessionAdapter(MyActivity activity, List<Session> sessions, int color) {
        this.mActivity = activity;
        this.mSessions = sessions;
        this.color = color;
    }

    public GenericLittleCardAvatarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenericLittleCardAvatarHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_generic_little, parent, false));
    }

    public void                     onBindViewHolder(GenericLittleCardAvatarHolder holder, int position) {
        Session session = mSessions.get(position);
        holder.card_view.setBackgroundColor(ContextCompat.getColor(mActivity, this.color));
        holder.title.setText("Session du " + session.getDateString().replace("/", " "));
        int def = 0, attack = 0;
        for (Action action : session.Actions()) {
            if (action.teamActionType == Action.TeamAction.READTEAM)
                attack++;
            else
                def++;
        }
        if (color == R.color.blueteam_color)
            holder.subtitle.setText(def + " def actions performed");
        else if (color == R.color.redteam_color)
            holder.subtitle.setText(attack + " attacks actions performed");
        else
            holder.subtitle.setText(session.Actions().size() + " actions performed");
        setLogo(holder.logo);
        holder.icon.setVisibility(View.GONE);
        holder.card_view.setOnClickListener(onCardClicked(position));
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) holder.card_view.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        holder.card_view.requestLayout();
    }

    private void                    setIcon(ImageView icon, int position) {
        int res;
        switch (position) {
            case 0:
                res = R.drawable.webserver_icon;
                break;
            default:
                res = R.drawable.webserver_icon;
                break;
        }
        GlideRequest builder = GlideApp.with(mActivity)
                .load(res)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        builder.into(icon);
    }

    private void                    setLogo(CircleImageView logo) {
        int res = R.mipmap.ic_stack;
        logo.setBorderWidth(1);
        GlideRequest builder = GlideApp.with(mActivity)
                .load(res)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        builder.into(logo);
    }

    private View.OnClickListener    onCardClicked(final int position) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                mActivity.showSnackbar("Not implemented");
            }
        };
    }

    public int                      getItemCount() {
        return mSessions == null ? 0 : mSessions.size();
    }

}
