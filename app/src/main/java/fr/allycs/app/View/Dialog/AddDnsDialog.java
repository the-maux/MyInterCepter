package fr.allycs.app.View.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.WindowManager.LayoutParams;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import fr.allycs.app.R;

public class                    AddDnsDialog {
    private TextInputLayout     TIL_host, TIL_ip;
    private EditText            host, ip;

    protected AlertDialog.Builder dialog;

    public AddDnsDialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(true);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_add_host_dns, null);
        dialog.setView(dialogView);
        TIL_host = (TextInputLayout) dialogView.findViewById(R.id.TIL_host);
        host = (EditText) dialogView.findViewById(R.id.host);
        TIL_ip = (TextInputLayout) dialogView.findViewById(R.id.TIL_ip);
        ip = (EditText) dialogView.findViewById(R.id.ip);
    }

    public AddDnsDialog setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public AddDnsDialog onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public AddDnsDialog setHint(String hint) {
        TIL_host.setHint(hint);
        return this;
    }

    public AddDnsDialog setHintText(String hint) {
        host.setHint(hint);
        return this;
    }

    public String       getHost() {
        return host.getText().toString();
    }

    public String       getIp() {
        return ip.getText().toString();
    }

    public AlertDialog          show() {
        final AlertDialog dial = dialog.create();
        host.requestFocus();
        dial.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dial.show();
        host.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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