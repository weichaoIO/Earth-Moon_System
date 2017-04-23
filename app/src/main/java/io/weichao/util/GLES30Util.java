package io.weichao.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GLES30Util {
    public static boolean detectOpenGLES30(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        return (am.getDeviceConfigurationInfo().reqGlEsVersion >= 0x30000);
    }

    public static int loadProgram(Context context, String vertexShaderFilePath, String fragmentShaderFilePath) {
        String vertexShaderSrc = readShader(context, vertexShaderFilePath);
        if (vertexShaderSrc == null) {
            return 0;
        }

        String fragmentShaderSrc = readShader(context, fragmentShaderFilePath);
        if (fragmentShaderSrc == null) {
            return 0;
        }

        return loadProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    public static int loadTexture(Context context, String filePath) {
        Bitmap bitmap = readBitmap(context, filePath);
        int textureId = loadTexture(bitmap);
        bitmap.recycle();
        return textureId;
    }

    public static int loadTexture(Context context, int resourceId) {
        Bitmap bitmap = readBitmap(context, resourceId);
        int textureId = loadTexture(bitmap);
        bitmap.recycle();
        return textureId;
    }

    private static int loadTexture(Bitmap bitmap) {
        int[] textureId = new int[1];

        // 创建纹理对象
        GLES30.glGenTextures(1, textureId, 0);// 要生成的纹理对象的数量：1
        // 保存n个纹理对象ID的无符号整数数组：textureId
        // 数组偏移量：0
        // 绑定纹理对象
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);// 纹理对象绑定的目标：GL_TEXTURE_2D
        // 要绑定的纹理对象的句柄：textureId[0]
        // 将bitmap应用到2D纹理通道当前绑定的纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        // 设置过滤模式
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);// 纹理目标：GL_TEXTURE_2D
        // 设置参数：GL_TEXTURE_MIN_FILTER
        // 设置参数值：GL_LINEAR
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);// 纹理目标：GL_TEXTURE_2D
        // 设置参数：GL_TEXTURE_MAG_FILTER
        // 设置参数值：GL_LINEAR
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);// 纹理目标：GL_TEXTURE_2D
        // 设置参数：GL_TEXTURE_WRAP_S
        // 设置参数值：GL_CLAMP_TO_EDGE
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);// 纹理目标：GL_TEXTURE_2D
        // 设置参数：GL_TEXTURE_WRAP_T
        // 设置参数值：GL_CLAMP_TO_EDGE

        return textureId[0];
    }

    private static Bitmap readBitmap(Context context, String filePath) {
        Bitmap bitmap = null;

        InputStream is = null;
        try {
            is = context.getAssets().open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    private static Bitmap readBitmap(Context context, int resourceId) {
        InputStream is = context.getResources().openRawResource(resourceId);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static boolean linkProgram(int vertexShader, int fragmentShader, int programObject) {
        // 连接着色器和程序
        GLES30.glAttachShader(programObject, vertexShader);// 指向程序对象的句柄：programObject
        // 指向程序连接的着色器对象的句柄：vertexShader
        checkGlError("glAttachShader");
        GLES30.glAttachShader(programObject, fragmentShader);// 指向程序对象的句柄：programObject
        // 指向程序连接的着色器对象的句柄：fragmentShader
        checkGlError("glAttachShader");
        // 链接程序
        GLES30.glLinkProgram(programObject);// 指向程序对象的句柄：programObject
        // 检查链接状态
        int[] linked = new int[1];
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);// 指向程序对象的句柄：programObject
        // 获取信息的参数：GL_LINK_STATUS
        // 指向查询结果整数存储位置的指针：linked
        // 存储位置的偏移量：0
        if (linked[0] == 0) {
            Log.e("GLES30Util", "Error linking program:");
            // 获取程序的信息日志
            Log.e("GLES30Util", GLES30.glGetProgramInfoLog(programObject));// 指向程序对象的句柄：programObject
            // 删除程序对象
            GLES30.glDeleteProgram(programObject);// 指向程序对象的句柄：programObject
            return true;
        }

        return false;
    }

    private static void checkGlError(String op) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("GLES30Util", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private static int loadShader(int type, String shaderSrc) {
        // 创建着色器
        int shader = GLES30.glCreateShader(type);// 创建的着色器类型：type
        if (shader == 0) {
            return 0;
        }

        // 提供着色器源代码
        GLES30.glShaderSource(shader, shaderSrc);// 指向着色器对象的句柄：shader
        // 着色器源字符串：shaderSrc
        // 编译着色器
        GLES30.glCompileShader(shader);// 需要编译的着色器对象句柄：shader
        // 查询着色器对象的信息
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);// 指向需要获取信息的着色器对象的句柄：shader
        // 获取信息的恶参数：GL_COMPILE_STATUS
        // 指向查询结果的整数存储位置的指针：compiled
        // 存储位置的偏移量：0
        if (compiled[0] == 0) {
            // 检索信息日志
            Log.e("GLES30Util", GLES30.glGetShaderInfoLog(shader));// 指向需要获取信息的着色器对象的句柄：shader
            // 标记删除着色器。当着色器不再连接到任何程序对象时，内存才会被释放。
            GLES30.glDeleteShader(shader);// 要删除的着色器对象的句柄：shader
            return 0;
        }

        return shader;
    }

    private static String readShader(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        String shaderSource = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = context.getAssets().open(fileName);
            int ch;
            baos = new ByteArrayOutputStream();
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            shaderSource = new String(buff, "UTF-8");
            shaderSource = shaderSource.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            is = null;
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return shaderSource;
    }

    private static int loadProgram(String vertexShaderSrc, String fragmentShaderSrc) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderSrc);
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSrc);
        if (fragmentShader == 0) {
            // 标记删除着色器。当着色器不再连接到任何程序对象时，内存才会被释放。
            GLES30.glDeleteShader(vertexShader);// 要删除的着色器对象的句柄：vertexShader
            return 0;
        }

        // 创建程序对象
        int programObject = GLES30.glCreateProgram();
        if (programObject == 0) {
            return 0;
        }

        // 连接着色器和程序
        if (linkProgram(vertexShader, fragmentShader, programObject)) {
            return 0;
        }

        // 标记删除着色器。当着色器不再连接到任何程序对象时，内存才会被释放。
        GLES30.glDeleteShader(vertexShader);// 要删除的着色器对象的句柄：vertexShader
        GLES30.glDeleteShader(fragmentShader);// 要删除的着色器对象的句柄：fragmentShader

        return programObject;
    }

    /**
     * 切分纹理产生纹理数组
     *
     * @param bw 纹理图切分的列数
     * @param bh 纹理图切分的行数
     * @return
     */
    public static float[] genTexCoord(int bw, int bh) {
        float[] result = new float[bw * bh * 6 * 2];
        float sizew = 1.0f / bw;//列数
        float sizeh = 1.0f / bh;//行数
        int c = 0;
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s = j * sizew;
                float t = i * sizeh;
                result[c++] = s;
                result[c++] = t;
                result[c++] = s;
                result[c++] = t + sizeh;
                result[c++] = s + sizew;
                result[c++] = t;
                result[c++] = s + sizew;
                result[c++] = t;
                result[c++] = s;
                result[c++] = t + sizeh;
                result[c++] = s + sizew;
                result[c++] = t + sizeh;
            }
        }
        return result;
    }
}
