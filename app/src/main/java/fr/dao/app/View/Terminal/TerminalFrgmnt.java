package fr.dao.app.View.Terminal;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    private ConstraintLayout    mCoordinatorLayout;
    ArrayList<Shell>            mShell;
    private TerminalActivity    mActivity;
    private boolean             root = false;
    private Map                 ttyByTabs = new HashMap();
    private TextView            stdout, prompt;
    private EditText            stdin;
    private boolean             isKeyboardON = true;

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
        mCoordinatorLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                mCoordinatorLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mCoordinatorLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    isKeyboardON = true;
                }
                else {
                    isKeyboardON = false;
                }
            }
        });
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        prompt = rootView.findViewById(R.id.prompt);
        stdin = rootView.findViewById(R.id.EditexTextCmd);
        stdout = rootView.findViewById(R.id.stdout);
        stdout.setText("");
        stdout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isKeyboardON) {
                    mActivity.hideKeyboard();
                    isKeyboardON = false;
                } else {
                    mActivity.showKeyboard();
                    isKeyboardON = true;
                }
            }
        });
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
        mActivity.mProgressBar.setVisibility(View.VISIBLE);
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                prompt.setVisibility(View.INVISIBLE);
                stdin.setVisibility(View.INVISIBLE);
            }
        });

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
                    mActivity.mProgressBar.setVisibility(View.GONE);
                }
                final Layout layout = stdout.getLayout();
                if(layout != null){
                    int scrollDelta = layout.getLineBottom(stdout.getLineCount() - 1)
                            - stdout.getScrollY() - stdout.getHeight();
                    if(scrollDelta > 0)
                        stdout.scrollBy(0, scrollDelta);
                }
            }
        });
    }

    void                        addTerminal() {
        mShell.add(new Shell(mActivity, mInstance));
        updateStdout();
    }

    public void                 rootClicker(ImageView mScanType) {
        if (root) {
            root = false;
            getShell().exec("exit");
            mScanType.setImageResource(R.mipmap.ic_root_off);
        } else {
            mScanType.setImageResource(R.mipmap.ic_root_on);
            root = true;
            getShell().exec("su");
        }

    }

    public Shell                getShell() {
        return mShell.get(mActivity.mTabs.getSelectedTabPosition());
    }

    public void                 updateStdout() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                stdout(getShell().actualOutput, true);
                prompt.setText(Html.fromHtml(getShell().PROMPT), TextView.BufferType.SPANNABLE);
            }
        });
    }

    public void                 clearShellRunning() {
        if (mShell != null) {
            for (Shell shell : mShell) {
                shell.close();
            }
            mShell = null;
        }

    }
}
