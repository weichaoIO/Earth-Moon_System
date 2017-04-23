package io.weichao.model;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.util.ConstantUtil;
import io.weichao.util.GLES30Util;
import io.weichao.view.GLES30EarthMoonSV;

public class EarthMoonModel extends BaseModel {
    public RelativeLayout view;

    private GLES30EarthMoonSV mGLES30SphereSV;

    public EarthMoonModel(BaseFragmentActivity activity) {
        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (GLES30Util.detectOpenGLES30(activity)) {
            mGLES30SphereSV = new GLES30EarthMoonSV(activity);
        } else {
            Log.e(ConstantUtil.TAG, "OpenGL ES 3.0 not supported on device.  Exiting...");
        }
        view.addView(mGLES30SphereSV);
    }

    @Override
    public void onResume() {
        if (mGLES30SphereSV != null) {
            mGLES30SphereSV.isThreadRun = true;
            mGLES30SphereSV.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mGLES30SphereSV != null) {
            mGLES30SphereSV.isThreadRun = false;
            mGLES30SphereSV.onPause();
        }
    }
}
