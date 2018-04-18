package fr.dao.app.View.ZViewController.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import fr.dao.app.Core.Configuration.Words;
import fr.dao.app.R;

public class QuestionMultipleAnswerDialog {
    public QuestionMultipleAnswerDialog(Context context, CharSequence[] items,
                                        DialogInterface.OnClickListener onClickListener, String title, int selected) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.drawable.ico)
                .setSingleChoiceItems(items, selected, null)
                .setPositiveButton(Words.yes(context), onClickListener)
                .show();
    }
}
