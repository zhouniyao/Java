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
	/*��Ȼ�����Ѿ��Ѷ������ݺ���ɫ������ֱ���ڲ�ͬ�������ˣ����ھͿ��Ը�����Ⱦ��*/
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
		//������ɫ������
		table = new Table();
		mallet = new Mallet();
		//������ɫ������
		textureProgram = new TextureShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
		colorProgram = new ColorShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
		//������������ID
		texture = TextureHelper.loadTexture(context, R.drawable.taiji);//loadTexture()����������Դ�ļ��ж���ͼ���ļ�������ͼ�����ݼ��ؽ�OpenGL����ȡ��һ������ID�����ʧ�ܣ�����0.
//		texture = TextureHelper.loadTexture(context, 0x7f020001);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/*��OpenGL��ӳ���ʱ������Ѵ�(-1,-1,-1)��(1,1,1)��Χӳ�䵽�Ǹ�Ϊ��ʾ��Ԥ���Ĵ����ϡ�
		 *�����Χ֮��Ĺ�һ���豸����ᱻ�ü����� 
		 */
		glViewport(0, 0, width, height);//�ӿ�
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
		//��׵��ƽ��
		setIdentityM(modelMatrix, 0);//��һ��
		translateM(modelMatrix, 0, 0f, 0f, -3f);//ƽ�Ʊ任
		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
		//��������
		//vertex_clip = ProjectionMatrix * vertex_eye
		//vertex_eye = ModelMatrix * vertex_model
		//vertex_clip = ProjectionMatrix * ModelMatrix * vertex_model
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}
	
	@Override
	/**
	 * �������е�display()������Ʋ���
	 * 1)����glClear()������������ݣ�
	 * 2)����OpenGL��������Ⱦ����
	 * 3)������ͼ���������Ļ��
	 */
	public void onDrawFrame(GL10 glUnused) {
		//Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);
		//Draw the table.
		textureProgram.useProgram();
		/*Pass the matrix into the shader program.��������������Ԫ������*/
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