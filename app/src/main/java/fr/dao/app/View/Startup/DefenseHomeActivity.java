package fr.dao.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.R;
import fr.dao.app.View.Dora.DoraActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.MenuItemHolder;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;

public class                    DefenseHomeActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private DefenseHomeActivity mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private CardView            blue_card, dashboard_card, settings_card, red_card;
    private RelativeLayout      rootView;
    private RecyclerView        RV_menu;
    private MenuDefenseAdapter  mAdapter;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defense);
        initXml();
        init();
    }

    private void                pushViewToFront() {
        startPostponedEnterTransition();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        RV_menu = findViewById(R.id.RV_menu);
        RV_menu.setLayoutManager(new GridLayoutManager(this, 2));
        rootView = findViewById(R.id.rootView);
    }

    private void                init() {
        //mAdapter = new MenuDefenseAdapter(this);
        //RV_menu.setAdapter(mAdapter);
        //RV_menu.setLayoutManager(new GridLayoutManager(this, 2));

    }

    public void                 showSnackbar(String txt) {
        super.showSnackbar(txt);
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    protected void              onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mAdapter = new MenuDefenseAdapter(this);
        RV_menu.setAdapter(mAdapter);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "RV show");
                        ViewAnimate.FabAnimateReveal(mInstance, RV_menu, null);
                    }
                });
            }
        }, 100);
    }

    protected void              onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }

    public class MenuDefenseAdapter extends RecyclerView.Adapter<MenuItemHolder> {
        private String              TAG = "DefenseHomeActivity";
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
                case 5:
                    holder.titleCard.setText("Network bodyguard");
                    holder.logo_card.setImageResource(R.drawable.scan);
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
                case 0:
                    holder.titleCard.setText("Terminal");
                    holder.logo_card.setImageResource(R.drawable.linuxicon);
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
                case 2:
                    holder.titleCard.setText("Self Proxy");
                    holder.logo_card.setImageResource(R.drawable.cage);
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
                case 3:
                    holder.titleCard.setText("ARP Controller");
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.logo_card.setImageResource(R.drawable.poiz);
                    holder.statusIconCardView.setImageResource(R.color.filtered_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
                case 4:
                    holder.titleCard.setText("Crypt Check");
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.logo_card.setImageResource(R.mipmap.ic_lock);
                    holder.statusIconCardView.setImageResource(R.color.filtered_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.showSnackbar(holder.titleCard.getText().toString());
                        }
                    });
                    break;
                case 1:
                    holder.titleCard.setText("Dora Diagnostic");
                    holder.progressBar_monitor.setVisibility(View.GONE);
                    holder.logo_card.setImageResource(R.drawable.pepper);
                    holder.statusIconCardView.setImageResource(R.color.filtered_color);
                    holder.logo_card.setVisibility(View.VISIBLE);
                    holder.card_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Utils.vibrateDevice(mActivity, 100);
                            mActivity.startActivity(new Intent(mActivity, DoraActivity.class));
                        }
                    });
                    break;
            }
            ViewGroup.LayoutParams lp = holder.card_view.getLayoutParams();
            lp.width = CardView.LayoutParams.MATCH_PARENT;
//                ViewAnimate.setVisibilityToVisibleLong(holder.card_view);
            holder.card_view.setLayoutParams(lp);
            holder.card_view.setAlpha(0.9f);
//            Animation animation = AnimationUtils.loadAnimation(mInstance, android.R.anim.slide_in_left);
//            holder.card_view.startAnimation(animation);
        }

        public int                  getItemCount() {
            return 6;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        rootView.setVisibility(View.GONE);
    }
}
