package su.sniff.cepter;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SwipeDetector implements OnTouchListener {
    private static final int MIN_DISTANCE = 100;
    private static final String logTag = "SwipeDetector";
    private float downX;
    private float downY;
    private Action mSwipeDetected = Action.None;
    private float upX;
    private float upY;

    public enum Action {
        LR,
        RL,
        TB,
        BT,
        None
    }

    public boolean swipeDetected() {
        return this.mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return this.mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.downX = event.getX();
                this.downY = event.getY();
                this.mSwipeDetected = Action.None;
                break;
            case 1:
                this.upX = event.getX();
                this.upY = event.getY();
                float deltaX = this.downX - this.upX;
                float deltaY = this.downY - this.upY;
                if (Math.abs(deltaX) <= 100.0f) {
                    if (Math.abs(deltaY) > 100.0f) {
                        if (deltaY >= 0.0f) {
                            if (deltaY > 0.0f) {
                                Log.i(logTag, "Swipe Bottom to Top");
                                this.mSwipeDetected = Action.BT;
                                break;
                            }
                        }
                        Log.i(logTag, "Swipe Top to Bottom");
                        this.mSwipeDetected = Action.TB;
                        break;
                    }
                } else if (deltaX >= 0.0f) {
                    if (deltaX > 0.0f) {
                        Log.i(logTag, "Swipe Right to Left");
                        this.mSwipeDetected = Action.RL;
                        break;
                    }
                } else {
                    Log.i(logTag, "Swipe Left to Right");
                    this.mSwipeDetected = Action.LR;
                    break;
                }
                break;
        }
        return false;
    }
}
