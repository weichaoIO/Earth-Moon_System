package io.weichao.model;

import android.util.Log;
import android.view.KeyEvent;

import io.weichao.callback.GestureCallback;
import io.weichao.callback.LifeCycleCallback;
import io.weichao.callback.OnKeyDownCallback;
import io.weichao.util.ConstantUtil;

/**
 * Created by Administrator on 2016/10/17.
 */

public class BaseModel implements LifeCycleCallback, GestureCallback, OnKeyDownCallback {
    @Override
    public void onCreate() {
        Log.d(ConstantUtil.TAG, "onCreate()");
    }

    @Override
    public void onStart() {
        Log.d(ConstantUtil.TAG, "onStart()");
    }

    @Override
    public void onRestart() {
        Log.d(ConstantUtil.TAG, "onRestart()");
    }

    @Override
    public void onResume() {
        Log.d(ConstantUtil.TAG, "onResume()");
    }

    @Override
    public void onPause() {
        Log.d(ConstantUtil.TAG, "onPause()");
    }

    @Override
    public void onStop() {
        Log.d(ConstantUtil.TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        Log.d(ConstantUtil.TAG, "onDestroy()");
    }


    @Override
    public void onCreateContinue() {
        Log.d(ConstantUtil.TAG, "onCreateContinue()");
    }

    @Override
    public void onStartContinue() {
        Log.d(ConstantUtil.TAG, "onStartContinue()");
    }

    @Override
    public void onRestartContinue() {
        Log.d(ConstantUtil.TAG, "onRestartContinue()");
    }

    @Override
    public void onResumeContinue() {
        Log.d(ConstantUtil.TAG, "onResumeContinue()");
    }


    @Override
    public void onFlingUp() {
        Log.d(ConstantUtil.TAG, "onFlingUp()");
    }

    @Override
    public void onFlingDown() {
        onButtonB();
    }

    @Override
    public void onFlingLeft() {
        onDpadLeft();
    }

    @Override
    public void onFlingRight() {
        onDpadRight();
    }

    @Override
    public void onDown() {
        Log.d(ConstantUtil.TAG, "onDown()");
    }

    @Override
    public void onLongPress() {
        Log.d(ConstantUtil.TAG, "onLongPress()");
    }

    @Override
    public void onSingleTap() {
        onButtonA();
    }

    @Override
    public void onDoubleTap() {
        Log.d(ConstantUtil.TAG, "onDoubleTap()");
    }


    @Override
    public void onVolumeUp() {
        Log.d(ConstantUtil.TAG, "onVolumeUp()");
    }

    @Override
    public void onVolumeDown() {
        Log.d(ConstantUtil.TAG, "onVolumeDown()");
    }

    @Override
    public void onDpadUp() {
        Log.d(ConstantUtil.TAG, "onDpadUp()");
    }

    @Override
    public void onDpadDown() {
        Log.d(ConstantUtil.TAG, "onDpadDown()");
    }

    @Override
    public void onDpadLeft() {
        Log.d(ConstantUtil.TAG, "onDpadLeft()");
    }

    @Override
    public void onDpadRight() {
        Log.d(ConstantUtil.TAG, "onDpadRight()");
    }

    @Override
    public void onButtonA() {
        Log.d(ConstantUtil.TAG, "onButtonA()");
    }

    @Override
    public void onButtonB() {
        Log.d(ConstantUtil.TAG, "onButtonB()");
    }

    @Override
    public void onButtonY() {
        Log.d(ConstantUtil.TAG, "onButtonY()");
    }

    public void onKeyDown(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                onVolumeUp();
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                onVolumeDown();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                onDpadUp();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                onDpadDown();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                onDpadLeft();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                onDpadRight();
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                onButtonA();
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                onButtonB();
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                onButtonY();
                break;
        }
    }
}
