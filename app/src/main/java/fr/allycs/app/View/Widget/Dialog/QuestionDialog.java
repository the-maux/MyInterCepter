package fr.allycs.app.View.Widget.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import fr.allycs.app.R;

public class                        QuestionDialog {
    private TextView                mQuestion;
    protected AlertDialog.Builder   dialog;

    public                          QuestionDialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(true);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_questionsimple, null);
        dialog.setView(dialogView);
        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setIcon(R.drawable.ico);
        mQuestion = dialogView.findViewById(R.id.question);
    }

    public QuestionDialog           setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public QuestionDialog           onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public QuestionDialog           setText(String question) {
        mQuestion.setText(question);
        return this;
    }

    public AlertDialog              show() {
        final AlertDialog dial = dialog.create();
        dial.show();
        return dial;
    }

}