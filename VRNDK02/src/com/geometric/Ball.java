package com.geometric;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;


import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.example.vrndk01.R;

import niming.util.ShaderHelper;
import niming.util.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;

//�޸Ŀα������
public class Ball {
	Context context;
	public static final float UNIT_SIZE = 1f;
	int mProgram;// �Զ�����Ⱦ������ɫ������id
//	int muMVPMatrixHandle;// �ܱ任��������
//    int muMMatrixHandle;//λ�á���ת�任��������
//	int maPositionHandle; // ����λ����������
//	int muRHandle;// ��İ뾶��������   
//    int maNormalHandle; //���㷨������������
//    int maLightLocationHandle;//��Դλ����������
    
    
	String mVertexShader;// ������ɫ��
	String mFragmentShader;// ƬԪ��ɫ��

	FloatBuffer mVertexBuffer;// �����������ݻ���
	FloatBuffer mNormalBuffer;//���㷨�������ݻ���
	int vCount = 0;
	float yAngle = 0;// ��y����ת�ĽǶ�
	float xAngle = 0;// ��x����ת�ĽǶ�
	float zAngle = 0;// ��z����ת�ĽǶ�
	float r = 0.8f;
	private int aPositionLocation;
	private int aTextureCoordLocation;
	private int aColorLocation;
	private int uMVPMatrixLocation;
	private int uColorLocation;
	private int uTextureUnitLoaction;
	public Ball(Context context) {
		this.context = context;
		// ��ʼ��������������ɫ����
		initVertexData();
		// ��ʼ��shader
//		initShader(R.raw.simple_vertex_shader2, R.raw.simple_fragment_shader2);
		initShader(R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
	}

	// ��ʼ�������������ݵķ���
	public void initVertexData() {
		// �����������ݵĳ�ʼ��================begin============================
		ArrayList<Float> alVertix = new ArrayList<Float>();// ��Ŷ��������ArrayList
		final int angleSpan = 10;// ������е�λ�зֵĽǶ�
		for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan)// ��ֱ����angleSpan��һ��
		{
			for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan)// ˮƽ����angleSpan��һ��
			{// ����������һ���ǶȺ�����Ӧ�Ĵ˵��������ϵ�����
				float x0 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
						.toRadians(hAngle)));
				float y0 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
						.toRadians(hAngle)));
				float z0 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle)));

				float x1 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
						.toRadians(hAngle + angleSpan)));
				float y1 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
						.toRadians(hAngle + angleSpan)));
				float z1 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle)));

				float x2 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.cos(Math.toRadians(hAngle + angleSpan)));
				float y2 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.sin(Math.toRadians(hAngle + angleSpan)));
				float z2 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle + angleSpan)));

				float x3 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.cos(Math.toRadians(hAngle)));
				float y3 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.sin(Math.toRadians(hAngle)));
				float z3 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle + angleSpan)));

				// �����������XYZ��������Ŷ��������ArrayList
				alVertix.add(x1);
				alVertix.add(y1);
				alVertix.add(z1);
				alVertix.add(x3);
				alVertix.add(y3);
				alVertix.add(z3);
				alVertix.add(x0);
				alVertix.add(y0);
				alVertix.add(z0);

				alVertix.add(x1);
				alVertix.add(y1);
				alVertix.add(z1);
				alVertix.add(x2);
				alVertix.add(y2);
				alVertix.add(z2);
				alVertix.add(x3);
				alVertix.add(y3);
				alVertix.add(z3);
			}
		}
		vCount = alVertix.size() / 3;// ���������Ϊ����ֵ������1/3����Ϊһ��������3������

		// ��alVertix�е�����ֵת�浽һ��float������
		float vertices[] = new float[vCount * 3];
		for (int i = 0; i < alVertix.size(); i++) {
			vertices[i] = alVertix.get(i);
		}

		// ���������������ݻ���
		// vertices.length*4����Ϊһ�������ĸ��ֽ�
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// �����ֽ�˳��
		mVertexBuffer = vbb.asFloatBuffer();// ת��Ϊint�ͻ���
		mVertexBuffer.put(vertices);// �򻺳����з��붥����������
		mVertexBuffer.position(0);// ���û�������ʼλ��
		// �ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
		// ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
		
		//�������ƶ��㷨��������
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(vertices);//�򻺳����з��붥����������
        mNormalBuffer.position(0);//���û�������ʼλ��     
	}

	// ��ʼ��shader
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		// ���ض�����ɫ���Ľű�����
		mVertexShader = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		// ����ƬԪ��ɫ���Ľű�����
		mFragmentShader = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
		
		int vertexShader = ShaderHelper.compileVertexShader(mVertexShader);
		int fragmentShader = ShaderHelper.compileFragmentShader(mFragmentShader);
		
		// ���ڶ�����ɫ����ƬԪ��ɫ����������
		mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
        
        
		glUseProgram(mProgram);
		/*attribute������λ*/
		aPositionLocation = glGetAttribLocation(mProgram, "a_Position");
		aColorLocation = glGetAttribLocation(mProgram, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(mProgram, "a_TexCoordinate");
		/*uniform������λ*/
		uMVPMatrixLocation = glGetUniformLocation(mProgram, "u_Matrix");
		uColorLocation = glGetUniformLocation(mProgram, "u_Color");
		uTextureUnitLoaction = glGetUniformLocation(mProgram, "u_TextureUnit");
	}

	public void drawSelf(float[] MVPMatrix) {		
		// �ƶ�ʹ��ĳ����ɫ������
		GLES20.glUseProgram(mProgram);
		// �����ձ任��������ɫ������
		// �����ձ任��������ɫ������
		GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false,
				MVPMatrix, 0);        
		// ������λ�����ݴ�����Ⱦ����
		GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
//        //�����㷨�������ݴ�����Ⱦ����
//		GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false,
//				3 * 4, mNormalBuffer);
		// ���ö���λ������
		GLES20.glEnableVertexAttribArray(aPositionLocation); 
//        GLES20.glEnableVertexAttribArray(maNormalHandle);// ���ö��㷨��������
		
//		glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);//������ɫ��������
		// ������		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
}
