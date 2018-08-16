package fr.dao.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.R;
import fr.dao.app.View.Cryptcheck.CryptCheckActivity;
import fr.dao.app.View.Dora.DoraActivity;
import fr.dao.app.View.Terminal.TerminalActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.MenuItemHolder;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class                    DefenseHomeActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private DefenseHomeActivity mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        appBarLayout;
    private CardView            blue_card, dashboard_card, settings_card, red_card;
    private ConstraintLayout    rootView;
    private RecyclerView        RV_menu;
    private MenuDefenseAdapter  mAdapter;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defense);
        postponeEnterTransition();
        initXml();
        if (mAdapter == null) {
            mAdapter = new MenuDefenseAdapter(mInstance);
            RV_menu.setAdapter(mAdapter);
        }
        startPostponedEnterTransition();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        rootView = findViewById(R.id.rootView);
        appBarLayout = findViewById(R.id.appBar);
        RV_menu = findViewById(R.id.RV_menu);
        RV_menu.setLayoutManager(new GridLayoutManager(this, 2));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        setStatusBarColor(R.color.blueteam_color);
    }

   protected void               onPostResume() {
        super.onPostResume();
    }

    public void                 showSnackbar(String txt) {
        super.showSnackbar(txt);
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public class MenuDefenseAdapter extends RecyclerView.Adapter<MenuItemHolder> {
        private String              TAG = "DefenseHomeAdapter";
        private DefenseHomeActivity mActivity;

        public MenuDefenseAdapter(DefenseHomeActivity activity) {
            this.mActivity = activity;
        }

        public MenuItemHolder        onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.monitor_card, parent, false));
        }

        public void                 onBindViewHolder(final MenuItemHolder holder, int position) {
            holder.card_view.setBackgroundColor(getResources().getColor(R.color.vulnsPrimary));
            switch (position) {
                case 0:
                    holder.titleCard.setText("Terminal");
                    MyGlideLoader.loadDrawableInImageView(mInstance, R.drawable.linuxicon, holder.logo_card, false, false);
//                    holder.logo_card.setImageResource(R.drawable.linuxicon);
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.statusIconCardView.setImageResource(R.color.online_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(mInstance, TerminalActivity.class);
                            Pair<View, String> p1 = Pair.create((View)holder.logo_card, "LogoTransition");
                            Pair<View, String> p2 = Pair.create((View)holder.titleCard, "title_transition");
                            Pair<View, String> p3 = Pair.create((View)holder.card_view, "rootViewTransition");
                            startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1, p2, p3).toBundle());
                        }
                    });
                    break;
                case 1:
                    holder.titleCard.setText("Dora Diagnostic");
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    MyGlideLoader.loadDrawableInImageView(mInstance, R.mipmap.ic_dora_png, holder.logo_card, false, false);
//                    holder.logo_card.setImageResource(R.drawable.pepper);
                    holder.statusIconCardView.setImageResource(R.color.online_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            Intent intent = new Intent(mInstance, DoraActivity.class);
                            Pair<View, String> p1 = Pair.create((View)holder.titleCard, "title_transition");
                            Pair<View, String> p2 = Pair.create((View)holder.card_view, "rootViewTransition");
                            startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1, p2).toBundle());
                        }
                    });
                    break;
                case 2:
                    holder.titleCard.setText("Self Proxy");
                    MyGlideLoader.loadDrawableInImageView(mInstance, R.mipmap.ic_proxy_png, holder.logo_card, false, false);
//                    holder.logo_card.setImageResource(R.drawable.cage);
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.statusIconCardView.setImageResource(R.color.offline_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
                case 3:
                    holder.titleCard.setText("ARP Controller");
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.logo_card.setImageResource(R.mipmap.ic_arp_png);
                    //MyGlideLoader.loadDrawableInImageView(mInstance, R.drawable.poiz, holder.logo_card, false, false);
                      holder.statusIconCardView.setImageResource(R.color.offline_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
                case 4:
                    holder.titleCard.setText("CryptCheck");
                    holder.progressBar_monitor.setVisibility(View.GONE);
//                    MyGlideLoader.loadDrawableInImageView(mInstance, R.mipmap.ic_aeris_png, holder.logo_card, false, false);
//                    holder.logo_card.setImageResource(R.mipmap.ic_lock);
                    holder.statusIconCardView.setImageResource(R.color.online_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            Intent intent = new Intent(mInstance, CryptCheckActivity.class);
                            Pair<View, String> p1 = Pair.create((View)holder.titleCard, "title_transition");
                            Pair<View, String> p2 = Pair.create((View)holder.card_view, "rootViewTransition");
                            startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1, p2).toBundle());
                        }
                    });
                    break;
                case 5:
                    holder.titleCard.setText("Network bodyguard");
                    MyGlideLoader.loadDrawableInImageView(mInstance, R.mipmap.ic_networkbody_png, holder.logo_card, false, false);
//                    holder.logo_card.setImageResource(R.drawable.scan);
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.statusIconCardView.setImageResource(R.color.offline_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
                case 6:
                    holder.titleCard.setText("Elin Ersson");
                    MyGlideLoader.loadDrawableInImageView(mInstance, R.mipmap.ic_elin_png, holder.logo_card, false, false);
//                    holder.logo_card.setImageResource(R.drawable.gallery);
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.statusIconCardView.setImageResource(R.color.offline_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
                case 7:
                    holder.titleCard.setText("Data Leaker");
                    MyGlideLoader.loadDrawableInImageView(mInstance, R.mipmap.ic_vpn_png, holder.logo_card, false, false);
                    holder.logo_card.setImageResource(R.drawable.network);
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.statusIconCardView.setImageResource(R.color.filtered_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
            }
            ViewGroup.LayoutParams lp = holder.card_view.getLayoutParams();
            lp.width = CardView.LayoutParams.MATCH_PARENT;
//                ViewAnimate.setVisibilityToVisibleLong(holder.card_view);
            holder.card_view.setLayoutParams(lp);
            holder.card_view.setVisibility(View.VISIBLE);
            /*ViewAnimate.reveal(mInstance, holder.card_view, new Runnable() {
                public void run() {
                    holder.card_view.setAlpha(0.8f);
                }
            });*/
//            Animation animation = AnimationUtils.loadAnimation(mInstance, android.R.anim.slide_in_left);
//            holder.card_view.startAnimation(animation);

        }

        public int                  getItemCount() {
            return 8;
        }
    }
}
