package io.weichao.callback;

/**
 * Created by WeiChao on 2016/6/28.
 */
public interface LifeCycleCallback {
    void onCreate();

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onCreateContinue();

    void onStartContinue();

    void onRestartContinue();

    void onResumeContinue();
}
