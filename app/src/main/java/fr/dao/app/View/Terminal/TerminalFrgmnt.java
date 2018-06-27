package fr.dao.app.View.Terminal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.dao.app.Core.Shell;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialog;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    TerminalFrgmnt extends MyFragment  {
    private String              TAG = "NmapOutputView";
    private TerminalFrgmnt      mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    ArrayList<Shell>            mShell;
    private TerminalActivity    mActivity;
    private boolean             root = true;
    private Map                 ttyByTabs = new HashMap();
    private TextView            stdout, prompt;
    private EditText            stdin;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_terminal, container, false);
        mActivity = (TerminalActivity)getActivity();
        initXml(rootView);
        init();
        return rootView;
    }

    public void                 init() {
        if (mShell == null) {
            mShell = new ArrayList<>();
            mShell.add(new Shell(mActivity, mInstance));
            prompt.setText(Html.fromHtml(getShell().PROMPT), TextView.BufferType.SPANNABLE);
        } else {

        }
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        prompt = rootView.findViewById(R.id.prompt);
        stdin = rootView.findViewById(R.id.EditexTextCmd);
        stdout = rootView.findViewById(R.id.stdout);
        stdout.setText("");
        stdout.setMovementMethod(new ScrollingMovementMethod());
        stdin.setBackgroundResource(android.R.color.transparent);
        stdin.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!execCmd(stdin.getText().toString()))
                        previousExecutionNotFinished();
                    return true;
                }
                return false;
            }
        });
    }

    private void                previousExecutionNotFinished() {
        new QuestionDialog(mActivity)
                .setTitle("Previous command not over")
                .setText("You want to start this command in new Terminal ?")
                .onPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.showSnackbar("Oe bah cest pas fait, attends");
                    }
                })
                .show();
    }

    private boolean             execCmd(String s) {
        if (mActivity.mProgressBar != null && mActivity.mProgressBar.getVisibility() == View.VISIBLE)
            mActivity.mProgressBar.setVisibility(View.VISIBLE);
        prompt.setVisibility(View.INVISIBLE);
        stdin.setVisibility(View.INVISIBLE);
        return getShell().exec(s);
    }


    public void                 stdin(final String line) {
        Log.d(TAG, "stdin[" + line + "]");
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                stdin.setText(Html.fromHtml(line), TextView.BufferType.SPANNABLE);
            }
        });
    }
    public void                 stdout(final String line, final boolean isCmdOver) {
        Log.d(TAG, "stdout[" + line + "]");
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                stdout.setText(Html.fromHtml(line), TextView.BufferType.SPANNABLE);
                if (mActivity.mProgressBar != null && mActivity.mProgressBar.getVisibility() == View.VISIBLE)
                    mActivity.mProgressBar.setVisibility(View.GONE);
                if (isCmdOver) {
                    stdin.setText("");
                    prompt.setVisibility(View.VISIBLE);
                    stdin.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    void                        addTerminal() {
        mShell.add(new Shell(mActivity, mInstance));
        updateStdout();
    }



    public void                 rootClicker(ImageView mScanType) {
        /*if (root) {
            root = false;
            mScanType.setImageResource(R.mipmap.ic_root_off);
        } else {
            mScanType.setImageResource(R.mipmap.ic_root_on);
            root = true;
        }*/
        getShell().changeUser(root);
    }

    public Shell                getShell() {
        return mShell.get(mActivity.mTabs.getSelectedTabPosition());
    }

    public void                 updateStdout() {
        stdout(getShell().actualOutput, true);
        prompt.setText(Html.fromHtml(getShell().PROMPT), TextView.BufferType.SPANNABLE);
    }
}
