package com.handy.es3x.java.renderer;

import android.opengl.GLES30;

import com.handy.es3x.java.R;
import com.handy.common.renderer.SurfaceRenderer;
import com.handy.common.utils.ResReadUtils;
import com.handy.common.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description: 顶点缓冲区
 */
public class VertexBufferRenderer implements SurfaceRenderer {

    private static final String TAG = "VertexBufferRenderer";

    private static final int VERTEX_POS_INDEX = 0;

    private final FloatBuffer vertexBuffer;

    private static final int VERTEX_POS_SIZE = 3;

    private static final int VERTEX_STRIDE = VERTEX_POS_SIZE * 4;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    /**
     * 缓冲数组
     */
    private int[] vboIds = new int[1];

    public VertexBufferRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated() {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_buffer_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_buffer_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);

        //1. 生成1个缓冲ID
        GLES30.glGenBuffers(1, vboIds, 0);

        //2. 绑定到顶点坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        //3. 向顶点坐标数据缓冲送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexPoints.length * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        //4. 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(VERTEX_POS_INDEX, VERTEX_POS_SIZE, GLES30.GL_FLOAT, false, VERTEX_STRIDE, 0);
        //5. 启用顶点位置属性
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX);

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //6. 使用程序片段
        GLES30.glUseProgram(mProgram);

        //7. 开始绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //8. 解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }

    @Override
    public void onDestroy() {

    }
}
