package io.weichao.earth_moon_system;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.model.EarthMoonModel;

public class MainActivity extends BaseFragmentActivity {
    private EarthMoonModel mEarthMoonModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout rootView = new RelativeLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(rootView);

        mEarthMoonModel = new EarthMoonModel(this);
        rootView.addView(mEarthMoonModel.view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEarthMoonModel != null) {
            mEarthMoonModel.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mEarthMoonModel != null) {
            mEarthMoonModel.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEarthMoonModel != null) {
            mEarthMoonModel.onDestroy();
        }
    }
}
