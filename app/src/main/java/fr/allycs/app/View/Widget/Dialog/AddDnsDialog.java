package fr.allycs.app.View.Widget.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import fr.allycs.app.R;

public class                    AddDnsDialog {
    private TextInputLayout     mTIL_host, mTIL_ip;
    private EditText            mHost, mIp;
    protected AlertDialog.Builder dialog;

    public                      AddDnsDialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(true);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_add_host_dns, null);
        dialog.setView(dialogView);

        mTIL_host = dialogView.findViewById(R.id.TIL_host);
        mHost = dialogView.findViewById(R.id.host);
        mTIL_ip = dialogView.findViewById(R.id.TIL_ip);
        mIp = dialogView.findViewById(R.id.ip);
    }

    public AddDnsDialog         setIcon(int ressource) {
        dialog.setIcon(ressource);
        return this;
    }
    public AddDnsDialog         setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public AddDnsDialog         onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public AddDnsDialog         setHint(String hint) {
        mTIL_host.setHint(hint);
        return this;
    }

    public AddDnsDialog         setHintText(String hint) {
        mHost.setHint(hint);
        return this;
    }

    public String               getHost() {
        return mHost.getText().toString();
    }

    public String               getIp() {
        return mIp.getText().toString();
    }

    public AlertDialog          show() {
        final AlertDialog dial = dialog.create();
        mHost.requestFocus();
        dial.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dial.show();
        mHost.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dial.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    return true;
                }
                return false;
            }
        });
        return dial;
    }

}