package com.airhockey1.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R.integer;
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey1.android.objects.Mallet;
import com.airhockey1.android.objects.Table;
import com.airhockey1.android.program.ColorShaderProgram;
import com.airhockey1.android.program.TextureShaderProgram;
import com.airhockey1.android.util.*;
import com.niming.airhockey1.R;

public class AirHockeyRenderer implements Renderer {
	/*既然我们已经把顶点数据和着色器程序分别放于不同的类中了，现在就可以更新渲染类*/
	private final Context context;
	
	private final float[] projectionMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	
	private Table table;
	private Mallet mallet;
	
	
	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;
	
	private int texture;
	
	public AirHockeyRenderer(Context context) {

		this.context = context;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0f, 0f, 0f, 0f);
		//创建着色器对象
		table = new Table();
		mallet = new Mallet();
		//创建着色器程序
		textureProgram = new TextureShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
		colorProgram = new ColorShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
		//创建纹理对象ID
		texture = TextureHelper.loadTexture(context, R.drawable.taiji);//loadTexture()方法，从资源文件夹读入图像文件，并把图形数据加载进OpenGL，并取回一个纹理ID。如果失败，返回0.
//		texture = TextureHelper.loadTexture(context, 0x7f020001);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/*当OpenGL做映射的时候，它会把从(-1,-1,-1)至(1,1,1)范围映射到那个为显示而预留的窗口上。
		 *这个范围之外的归一化设备坐标会被裁剪掉。 
		 */
		glViewport(0, 0, width, height);//视口
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
		//视椎体平移
		setIdentityM(modelMatrix, 0);//归一化
		translateM(modelMatrix, 0, 0f, 0f, -3f);//平移变换
		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
		//矩阵连乘
		//vertex_clip = ProjectionMatrix * vertex_eye
		//vertex_eye = ModelMatrix * vertex_model
		//vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}
	
	@Override
	/**
	 * 几乎所有的display()完成类似操作
	 * 1)调用glClear()来清除窗口内容；
	 * 2)调用OpenGL命令来渲染对象；
	 * 3)将最终图像输出到屏幕。
	 */
	public void onDrawFrame(GL10 glUnused) {
		//Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);
		//Draw the table.
		textureProgram.useProgram();
		/*Pass the matrix into the shader program.并且生成纹理单元绑定纹理*/
		textureProgram.setUniforms(projectionMatrix, texture);
		table.bindData(textureProgram);
		table.draw();
		
		//Draw the mallets.
		colorProgram.useProgram();
		colorProgram.setUniforms(projectionMatrix);
		mallet.bindData(colorProgram);
		mallet.draw();
	}




}
