package fr.dao.app.View.ZViewController.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.dao.app.R;
import fr.dao.app.View.Startup.DefenseHomeActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.MenuItemHolder;

public class MenuDefenseAdapter extends RecyclerView.Adapter<MenuItemHolder> {
    private String              TAG = "NetworksAdapter";
    private DefenseHomeActivity mActivity;

    public MenuDefenseAdapter(DefenseHomeActivity activity) {
        this.mActivity = activity;
    }

    public MenuItemHolder        onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuItemHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monitor_card, parent, false));
    }

    public void                 onBindViewHolder(final MenuItemHolder holder, int position) {
        switch (position) {
            case 0:
                holder.titleCard.setText("Network bodyguard");
                holder.logo_card.setImageResource(R.drawable.scan);
                holder.progressBar_monitor.setVisibility(View.GONE);
                holder.statusIconCardView.setImageResource(R.color.online_color);
                holder.logo_card.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.titleCard.setText("Terminal");
                holder.logo_card.setImageResource(R.drawable.linuxicon);
                holder.progressBar_monitor.setVisibility(View.GONE);
                holder.statusIconCardView.setImageResource(R.color.online_color);
                holder.logo_card.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.titleCard.setText("Self Proxy");
                holder.logo_card.setImageResource(R.drawable.cage);
                holder.progressBar_monitor.setVisibility(View.GONE);
                holder.statusIconCardView.setImageResource(R.color.online_color);
                holder.logo_card.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.titleCard.setText("ARP Controller");
                holder.progressBar_monitor.setVisibility(View.GONE);
                holder.logo_card.setImageResource(R.drawable.poiz);
                holder.statusIconCardView.setImageResource(R.color.online_color);
                holder.logo_card.setVisibility(View.VISIBLE);
                break;
            case 4:
                holder.titleCard.setText("Crypt Check");
                holder.progressBar_monitor.setVisibility(View.GONE);
                holder.logo_card.setImageResource(R.mipmap.ic_lock);
                holder.statusIconCardView.setImageResource(R.color.online_color);
                holder.logo_card.setVisibility(View.VISIBLE);
                break;
            case 5:
                holder.titleCard.setText("Email checker");
                holder.progressBar_monitor.setVisibility(View.GONE);
                holder.logo_card.setImageResource(R.drawable.secure_computer1);
                holder.statusIconCardView.setImageResource(R.color.online_color);
                holder.logo_card.setVisibility(View.VISIBLE);
                break;
        }
        ViewGroup.LayoutParams lp = holder.card_view.getLayoutParams();
        lp.width = CardView.LayoutParams.MATCH_PARENT;
        holder.card_view.setLayoutParams(lp);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mActivity.showSnackbar(holder.titleCard.getText().toString());
            }
        });
    }
    
    public int                  getItemCount() {
        return 6;
    }
    
}
