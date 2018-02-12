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

public class                        DialogQuestionWithInput {
    private TextInputLayout         mTIL_firstQuestion, mTIL_SecondQuestion;
    private EditText                mED_FirstQuestion, mED_SeconfQuestion;
    protected AlertDialog.Builder   dialog;

    public                          DialogQuestionWithInput(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(true);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_question_with_input, null);
        dialog.setView(dialogView);
        mTIL_firstQuestion = dialogView.findViewById(R.id.TIL_host);
        mED_FirstQuestion = dialogView.findViewById(R.id.host);
        mTIL_SecondQuestion = dialogView.findViewById(R.id.TIL_ip);
        mED_SeconfQuestion = dialogView.findViewById(R.id.ip);
    }

    public DialogQuestionWithInput  setIcon(int ressource) {
        dialog.setIcon(ressource);
        return this;
    }
    public DialogQuestionWithInput  setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public DialogQuestionWithInput  onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public DialogQuestionWithInput setHintToTILFirstQuestion(String hint) {
        mTIL_firstQuestion.setHint(hint);
        return this;
    }

    public DialogQuestionWithInput setHintToEDFirstQuestion(String hint) {
        mED_FirstQuestion.setHint(hint);
        return this;
    }

    public DialogQuestionWithInput setHintToTILSecoundQuestion(String hint) {
        mTIL_SecondQuestion.setHint(hint);
        return this;
    }

    public DialogQuestionWithInput setHintToEDSecoundQuestion(String hint) {
        mED_SeconfQuestion.setHint(hint);
        return this;
    }

    public DialogQuestionWithInput hideSecondInput() {
        mTIL_SecondQuestion.setVisibility(View.GONE);
        return this;
    }

    public String                   getFirstInputQuestion() {
        return mED_FirstQuestion.getText().toString();
    }

    public String                   getSecoundInputQuestion() {
        return mED_SeconfQuestion.getText().toString();
    }

    public AlertDialog              show() {
        final AlertDialog dial = dialog.create();
        mED_FirstQuestion.requestFocus();
        dial.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dial.show();
        mED_FirstQuestion.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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