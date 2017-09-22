package su.sniff.cepter.View.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import su.sniff.cepter.R;

/**
 * Générique AlertDialog with TextInputLayout
 */
public class                    TIL_dialog {
    private TextInputLayout     TIL_host;
    private EditText            editText;

    protected AlertDialog.Builder dialog;

    public                      TIL_dialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_ipaddress, null);
        dialog.setView(dialogView);
        TIL_host = (TextInputLayout) dialogView.findViewById(R.id.TIL_host);
        editText = (EditText) dialogView.findViewById(R.id.editText);

    }

    public TIL_dialog            setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public TIL_dialog            onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public TIL_dialog           setHint(String hint) {
        TIL_host.setHint(hint);
        return this;
    }



    public String               getText() {
        return editText.getText().toString();
    }

    public AlertDialog          show() {
        final AlertDialog dial = dialog.create();
        final InputMethodManager imm =
                (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        dial.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        editText.requestFocus();

        dial.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dial.show();
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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