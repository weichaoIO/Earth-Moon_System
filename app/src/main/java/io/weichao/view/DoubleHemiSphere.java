package io.weichao.view;

import android.content.Context;
import android.opengl.GLES30;

import io.weichao.util.GLES30Util;
import io.weichao.util.MatrixStateUtil;

/**
 * 采用多重纹理的球面
 */
public class DoubleHemiSphere extends Sphere {
    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用
    private int muMMatrixHandle;//位置、旋转变换矩阵
    private int maCameraHandle; //摄像机位置属性引用
    private int maPositionHandle; //顶点位置属性引用
    private int maNormalHandle; //顶点法向量属性引用
    private int maTexCoordHandle; //顶点纹理坐标属性引用
    private int maSunLightLocationHandle;//光源位置属性引用
    private int uDayTexHandle;//白天纹理属性引用
    private int uNightTexHandle;//黑夜纹理属性引用
    private int mTextureIdDay;
    private int mTextureIdNight;

    public DoubleHemiSphere(Context context) {
        super(context);
    }

    public DoubleHemiSphere(Context context, float radius) {
        super(context, radius);
    }

    public DoubleHemiSphere(Context context, float radius, int splitCount) {
        super(context, radius, splitCount);
    }

    @Override
    public void initScript(Context context) {
        mProgram = GLES30Util.loadProgram(context, "model/double_hemi_sphere/script/vertex_shader.sh", "model/double_hemi_sphere/script/fragment_shader.sh");
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理属性引用
        maTexCoordHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoord");
        //获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用
        maSunLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocationSun");
        //获取白天、黑夜两个纹理引用
        uDayTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureDay");
        uNightTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureNight");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void setTextureId(int textureIdDay, int textureIdNight) {
        mTextureIdDay = textureIdDay;
        mTextureIdNight = textureIdNight;
    }

    @Override
    public void draw() {
        //指定使用某套着色器程序（必须每次都指定）
        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixStateUtil.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixStateUtil.getMMatrix(), 0);
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixStateUtil.cameraFB);
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maSunLightLocationHandle, 1, MatrixStateUtil.lightPositionFBSun);

        //将顶点位置数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mPositionBuffer);
        //将顶点纹理数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(maTexCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoordBuffer);
        //将顶点法向量数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mPositionBuffer);
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        //启用顶点纹理数据数组
        GLES30.glEnableVertexAttribArray(maTexCoordHandle);
        //启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle);

        //加载纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIdDay);  //绑定白天纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIdNight);  //绑定黑夜纹理
        GLES30.glUniform1i(uDayTexHandle, 0);
        GLES30.glUniform1i(uNightTexHandle, 1);

        //绘制图形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCount);
    }
}
