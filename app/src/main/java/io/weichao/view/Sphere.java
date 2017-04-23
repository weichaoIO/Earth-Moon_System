package io.weichao.view;

import android.content.Context;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import io.weichao.util.GLES30Util;
import io.weichao.util.MatrixStateUtil;

/**
 * Created by WeiChao on 2016/8/5.
 */
public class Sphere {
    protected FloatBuffer mPositionBuffer;//顶点坐标数据缓冲
    protected FloatBuffer mTexCoordBuffer;//顶点纹理坐标数据缓冲
    protected int mVertexCount;

    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用
    private int muMMatrixHandle;//位置、旋转变换矩阵
    private int maCameraHandle; //摄像机位置属性引用
    private int maPositionHandle; //顶点位置属性引用
    private int maNormalHandle; //顶点法向量属性引用
    private int maTexCoordHandle; //顶点纹理坐标属性引用
    private int maSunLightLocationHandle;//光源位置属性引用
    private int mTextureId;

    public Sphere(Context context) {
        this(context, 1, 10);
    }

    public Sphere(Context context, float radius) {
        this(context, radius, 10);
    }

    public Sphere(Context context, float radius, int splitCount) {
        //初始化顶点数据
        initVertexData(radius, splitCount);
        //初始化着色器
        initScript(context);
    }

    /**
     * 初始化顶点数据
     *
     * @param radius
     */
    public void initVertexData(float radius, int splitCount) {
        int span = 10;
        try {
            span = 180 / splitCount;
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Float> alVertix = new ArrayList<>();
        //将球进行单位切分的角度，纵向、横向angleSpan度一份
        for (float vAngle = 90; vAngle > -90; vAngle = vAngle - span) {
            for (float hAngle = 360; hAngle > 0; hAngle = hAngle - span) {
                //纵向、横向各到一个角度后，计算对应的此点在球面上的坐标
                double xozLength = radius * Math.cos(Math.toRadians(vAngle));
                float x1 = (float) (xozLength * Math.cos(Math.toRadians(hAngle)));
                float z1 = (float) (xozLength * Math.sin(Math.toRadians(hAngle)));
                float y1 = (float) (radius * Math.sin(Math.toRadians(vAngle)));

                xozLength = radius * Math.cos(Math.toRadians(vAngle - span));
                float x2 = (float) (xozLength * Math.cos(Math.toRadians(hAngle)));
                float z2 = (float) (xozLength * Math.sin(Math.toRadians(hAngle)));
                float y2 = (float) (radius * Math.sin(Math.toRadians(vAngle - span)));

                xozLength = radius * Math.cos(Math.toRadians(vAngle - span));
                float x3 = (float) (xozLength * Math.cos(Math.toRadians(hAngle - span)));
                float z3 = (float) (xozLength * Math.sin(Math.toRadians(hAngle - span)));
                float y3 = (float) (radius * Math.sin(Math.toRadians(vAngle - span)));

                xozLength = radius * Math.cos(Math.toRadians(vAngle));
                float x4 = (float) (xozLength * Math.cos(Math.toRadians(hAngle - span)));
                float z4 = (float) (xozLength * Math.sin(Math.toRadians(hAngle - span)));
                float y4 = (float) (radius * Math.sin(Math.toRadians(vAngle)));

                //构建第1个三角形
                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);
                alVertix.add(x4);
                alVertix.add(y4);
                alVertix.add(z4);
                //构建第2个三角形
                alVertix.add(x4);
                alVertix.add(y4);
                alVertix.add(z4);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
            }
        }
        //顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        mVertexCount = alVertix.size() / 3;

        float[] positionArray = new float[alVertix.size()];
        for (int i = 0; i < alVertix.size(); i++) {
            positionArray[i] = alVertix.get(i);
        }
        mPositionBuffer = ByteBuffer.allocateDirect(positionArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositionBuffer.put(positionArray).position(0);

        float[] texCoordArray = GLES30Util.genTexCoord((int) (360 / span), (int) (180 / span));
        mTexCoordBuffer = ByteBuffer.allocateDirect(texCoordArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexCoordBuffer.put(texCoordArray).position(0);
    }

    /**
     * 初始化着色器
     *
     * @param context
     */
    public void initScript(Context context) {
        mProgram = GLES30Util.loadProgram(context, "model/sphere/script/vertex_shader.sh", "model/sphere/script/fragment_shader.sh");
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点经纬度属性引用
        maTexCoordHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoord");
        //获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用
        maSunLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocationSun");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    public void draw() {
        //指定使用某套着色器程序（必须每次都指定）
        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixStateUtil.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixStateUtil.getMMatrix(), 0);
        // TODO 下面这句有时报错：java.lang.IllegalArgumentException: remaining() < count*3 < needed
        //将摄像机位置传入渲染管线
//        GLES30.glUniform3fv(maCameraHandle, 1, MatrixStateUtil.cameraFB);
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

        //激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        //绘制图形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCount);
    }
}
