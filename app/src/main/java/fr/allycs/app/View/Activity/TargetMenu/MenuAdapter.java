package fr.allycs.app.View.Activity.TargetMenu;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Dora;
import fr.allycs.app.Core.Tcpdump.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Activity.Dora.DoraActivity;
import fr.allycs.app.View.Activity.Scan.NmapActivity;
import fr.allycs.app.View.Activity.Settings.SettingsActivity;
import fr.allycs.app.View.Activity.WebServer.WebServerActivity;
import fr.allycs.app.View.Activity.Wireshark.WiresharkActivity;
import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.View.Widget.Holder.MenuItemPointHolder;

public class                    MenuAdapter extends RecyclerView.Adapter<MenuItemPointHolder> {
    private String              TAG = "MenuAdapter";
    private TargetMenuActivity  menuActivity;
    private Drawable            red, green;
    private Singleton           mSingleton = Singleton.getInstance();

    public MenuAdapter(TargetMenuActivity activity) {
        this.menuActivity = activity;
        red = new ColorDrawable(ContextCompat.getColor( activity, R.color.material_red_700));
        green = new ColorDrawable(ContextCompat.getColor( activity, R.color.material_green_700));

    }

    public MenuItemPointHolder    onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuItemPointHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false));
    }

    public void                 onBindViewHolder(MenuItemPointHolder holder, int position) {
        switch (position) {
            case 0:
                initIcmpVectorMenu(holder);
                break;
            case 1:
                initNmapMenu(holder);
                break;
            case 2:
                initInterceptMenu(holder);
                break;
            case 3:
                initDnsmasqMenu(holder);
                break;
            case 4:
                initWiresharkMenu(holder);
                break;
            case 5:
                initDoraMenu(holder);
                break;
            case 6:
                initWebServerMenu(holder);
                break;
            case 7:
                initSettingsMenu(holder);
                break;
        }
    }

    private void                    initIcmpVectorMenu(MenuItemPointHolder holder) {
        holder.name.setText("Icmp vectors");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.drawable.cage, holder.image, true);
        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, R.color.material_red_500, holder.monitor);
        holder.monitor.setVisibility(View.GONE);
        Pair<View, String> p1 = Pair.create((View)holder.image, "NmapIconTransition");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                menuActivity.showSnackbar("ICmp vector is not allowed");
            }
        });
    }
    private void                    initNmapMenu(MenuItemPointHolder holder) {
        holder.name.setText("Nmap");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.drawable.nmap, holder.image, true);
        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, R.color.material_red_500,holder.monitor);
        holder.monitor.setVisibility(View.GONE);
        Pair<View, String> p1 = Pair.create((View)holder.image, "NmapIconTransition");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(NmapActivity.class, options);
            }
        });
    }
    private void                    initInterceptMenu(MenuItemPointHolder holder) {
        holder.name.setText("Intercept");
        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, R.drawable.death, holder.image);
//        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, R.drawable.nmap, holder.monitor);
        holder.monitor.setVisibility(View.GONE);
        Pair<View, String> p1 = Pair.create((View)holder.image, "");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(NmapActivity.class, options);
                menuActivity.showSnackbar("Not implemented");
            }
        });
    }
    private void                    initDnsmasqMenu(MenuItemPointHolder holder) {
        holder.name.setText("Dns spoofing");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.mipmap.ic_dns, holder.image, true);
        Drawable ressourceID = (mSingleton.isDnsControlstarted()) ? red : green;
        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, ressourceID, holder.monitor);
        holder.monitor.setVisibility(View.GONE);
        Pair<View, String> p1 = Pair.create((View)holder.image, "iconDNS");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(DnsActivity.class, options);
            }
        });
    }
    private void                    initDoraMenu(MenuItemPointHolder holder) {
        holder.name.setText("Dora");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.drawable.radar1600, holder.image, true);
        Drawable ressourceID = (Dora.isRunning()) ? green : red;
        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, ressourceID, holder.monitor);
        Pair<View, String> p1 = Pair.create((View)holder.image, "");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Singleton.getInstance().selectedHostsList == null) {
                    menuActivity.showSnackbar("Wireshark needs target(s) to work");
                }
                startActivity(DoraActivity.class, options);
            }
        });    }

    private void                    initWiresharkMenu(MenuItemPointHolder holder) {
        holder.name.setText("Wireshark");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.drawable.wireshark, holder.image, true);
        Drawable ressourceID = (Tcpdump.isRunning()) ? green : red;
        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, ressourceID, holder.monitor);
//        holder.monitor.setVisibility(View.GONE);
        Pair<View, String> p1 = Pair.create((View)holder.image, "wiresharkIcon");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Singleton.getInstance().selectedHostsList == null) {
                    menuActivity.showSnackbar("Wireshark needs target(s) to work");
                }
                startActivity(WiresharkActivity.class, options);
            }
        });
    }

    private void                    initWebServerMenu(MenuItemPointHolder holder) {
        holder.name.setText("Webserver");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.drawable.www, holder.image, true);
     //   MyGlideLoader.loadDrawableInCircularImageView(menuActivity, R.drawable.nmap, holder.monitor);
//        holder.monitor.setVisibility(View.GONE);
        //Pair<View, String> p1 = Pair.create((View)holder.image, "");
//        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.monitor.setVisibility(View.GONE);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(WebServerActivity.class, null);
            }
        });
    }
    private void                    initSettingsMenu(MenuItemPointHolder holder) {
        holder.name.setText("Settings");
        MyGlideLoader.loadDrawableInImageView(menuActivity, R.drawable.ic_settings_white_24dp, holder.image, true);
//        MyGlideLoader.loadDrawableInCircularImageView(menuActivity, R.drawable.nmap, holder.monitor);
        holder.monitor.setVisibility(View.GONE);
    //    Pair<View, String> p1 = Pair.create((View)holder.image, "");
    ///    final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(menuActivity, p1);
        holder.Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(SettingsActivity.class, null);
            }
        });
    }

    private void                    startActivity(Class choice, ActivityOptionsCompat options) {
        if (choice != null) {
            Intent intent = new Intent(menuActivity, choice);
            if (options == null)
                menuActivity.startActivity(intent);
            else {
                menuActivity.startActivity(intent, options.toBundle());
            }
        }
    }

    public int                      getItemCount() {
        return 7;
    }

}
