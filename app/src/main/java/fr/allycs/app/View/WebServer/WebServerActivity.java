package fr.allycs.app.View.WebServer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.allycs.app.Controller.AndroidUtils.SniffActivity;
import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.WebServer.GenericServer;
import fr.allycs.app.R;

public class                    WebServerActivity extends SniffActivity {
    private String              TAG = "WebServerActivity";
    private int                 PORT = 8080;

    private Singleton           mSingleton = Singleton.getInstance();
    private ConstraintLayout    mCoordinatorLayout;
    private EditText            mEditTextPort;
    private FloatingActionButton mFab;
    private TextView            mTV_Message, mTV_IpAccess;

    private GenericServer       mWebServer;
    private BroadcastReceiver   broadcastReceiverNetworkState;
    private static boolean      mIsStarted = false;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        init();
    }

    private void                init() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mEditTextPort = findViewById(R.id.editTextPort);
        mTV_Message = findViewById(R.id.textViewMessage);
        mTV_IpAccess = findViewById(R.id.textViewIpAccess);
        mTV_IpAccess.setText("http://" + mSingleton.network.myIp + ":");
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(onFabClick());
        initBroadcastReceiverNetworkStateChanged();// INIT BROADCAST RECEIVER TO LISTEN NETWORK STATE CHANGED
    }

    private View.OnClickListener onFabClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                if (!mIsStarted && startAndroidWebServer()) {
                    mIsStarted = true;
                    mTV_Message.setVisibility(View.VISIBLE);
                    mFab.setBackgroundTintList(ContextCompat.getColorStateList(WebServerActivity.this, R.color.material_green_500));
                    mEditTextPort.setEnabled(false);
                } else if (stopAndroidWebServer()) {
                    mIsStarted = false;
                    mTV_Message.setVisibility(View.INVISIBLE);
                    mFab.setBackgroundTintList(ContextCompat.getColorStateList(WebServerActivity.this, R.color.material_red_500));
                    mEditTextPort.setEnabled(true);
                }
            }
        };
    }

    private boolean             startAndroidWebServer() {
        if (!mIsStarted) {
            String valueEditText = mEditTextPort.getText().toString();
            int port = (valueEditText.length() > 0) ? Integer.parseInt(valueEditText) : PORT;
            try {
                if (port == 0) {
                    throw new Exception();
                }
                mWebServer = new GenericServer(port);
                mWebServer.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                showSnackbar("The PORT " + port + " doesn't work, please change it between 1000 and 9999.");
            }
        } else {
            showSnackbar("Server already launched");
        }
        return false;
    }
    private boolean             stopAndroidWebServer() {
        if (mIsStarted && mWebServer != null) {
            mWebServer.stop();
            return true;
        }
        return false;
    }

    private void                initBroadcastReceiverNetworkStateChanged() {
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        broadcastReceiverNetworkState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTV_IpAccess.setText("http://" + mSingleton.network.myIp + ":");
            }
        };
        super.registerReceiver(broadcastReceiverNetworkState, filters);
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public void                 onBackPressed() {
        if (mIsStarted) {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage(R.string.dialog_exit_message)
                    .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    protected void              onDestroy() {
        super.onDestroy();
        stopAndroidWebServer();
        mIsStarted = false;
        if (broadcastReceiverNetworkState != null) {
            unregisterReceiver(broadcastReceiverNetworkState);
        }
    }


    /*Navigation */

    public int                  getContentViewId() {
        return R.layout.activity_webserver;
    }

    public int                  getNavigationMenuItemId() {
        return R.id.navigation_webserver;
    }

}
