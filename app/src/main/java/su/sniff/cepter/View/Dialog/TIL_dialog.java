package su.sniff.cepter.View.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import su.sniff.cepter.R;

/**
 * Générique AlertDialog with TextInputLayout
 */
public class                    TIL_dialog {
    private TextInputLayout     TIL_host;
    protected AlertDialog.Builder dialog;

    public                      TIL_dialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_ipaddress, null);
        dialog.setView(dialogView);
        TIL_host = (TextInputLayout) dialogView.findViewById(R.id.TIL_host);
    }

    public TIL_dialog            setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public TIL_dialog            onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public String               getText() {
        return TIL_host.getEditText().getText().toString();
    }

    public AlertDialog show() {
        return dialog.show();
    }
}