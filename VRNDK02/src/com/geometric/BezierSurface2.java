package com.geometric;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.example.vrndk01.R;

import niming.util.ShaderHelper;
import niming.util.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;


public class BezierSurface2 {
	Context context;
	int program;// �Զ�����Ⱦ������ɫ������id
	FloatBuffer mVertexBuffer;// �����������ݻ���
	FloatBuffer mNormalBuffer;// ���㷨�����������ݻ���
	int vCount = 0;//����������
    /*����װ��ʱ������ɫ��������OpenGL�ڵ�Location*/
    private int aPositionLocation;
    private int aColorLocation;
    private int aTextureCoordLocation;
    private int uTextureUnitLoaction;
    private int uMVPMatrixLocation;
    private int uColorLocation;
	
    private final int srcCount = 4;
    public final int dstCount = 40;
    
    private Point3D[][] src = new Point3D[srcCount][srcCount];// ��4x4�������
    public Point3D[][] dst = new Point3D[dstCount][dstCount];// Bezier����㼯
    
    {//��ʼ����άPoint3D����
        for (int i = 0; i < srcCount; i++) {
            for (int j = 0; j < srcCount; j++) {
                src[i][j] = new Point3D();
            }
        }
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
                dst[i][j] = new Point3D();
            }
        }
    }
    public BezierSurface2(Context context) {
    	this.context = context;
    	initps();
    	resultBezierSurface();
    	/*���Զ��㻺����*/
//    	for (int i = 0; i < mVertexBuffer.capacity(); i++) {
//			Log.i("���㻺������", ""+mVertexBuffer.get());
//		}
//    	
    	initShader(R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);;
	}
    /**
     * ��ʼ��src����Ԫ�ء���3D���Ƶ�
     */
    private void initps() {
    	//������
    	src[0][0].setXYZ(-1.5, -1.5, 4.0);src[0][1].setXYZ(-0.5, -2.5, 2.0);src[0][2].setXYZ(0.5, -3.0, -1);src[0][3].setXYZ(1.5, -1.5, 2.0);
    	src[1][0].setXYZ(-2, -0.5, 1.0);src[1][1].setXYZ(-0.5, -0.5, 3.0);src[1][2].setXYZ(0.5, -0.5, 0.0);src[1][3].setXYZ(1.7, -0.5, -1.0);
    	src[2][0].setXYZ(-2, 0.5, 4.0);src[2][1].setXYZ(-0.5, 0.5, 0.0);src[2][2].setXYZ(0.5, 0.5, -2.0);src[2][3].setXYZ(1.8, 0.5, 4.0);
    	src[3][0].setXYZ(-1.5, 1.5, -2.0);src[3][1].setXYZ(-0.5, 3.0, 0.0);src[3][2].setXYZ(0.5, 3.5, 0.0);src[3][3].setXYZ(1.5, 1.5, -1.0);
    	
    	//������
//    	src[0][0].setXYZ(-2, -1.5, 4.0);src[0][1].setXYZ(-0.5, -1.5, 2.0);src[0][2].setXYZ(0.5, -1.5, -1);src[0][3].setXYZ(1.5, -1.5, 2.0);
//    	src[1][0].setXYZ(-2, -0.5, 1.0);src[1][1].setXYZ(-0.5, -0.5, 3.0);src[1][2].setXYZ(0.5, -0.5, 0.0);src[1][3].setXYZ(1.5, -0.5, -1.0);
//    	src[2][0].setXYZ(-2, 0.5, 4.0);src[2][1].setXYZ(-0.5, 0.5, 0.0);src[2][2].setXYZ(0.5, 0.5, -2.0);src[2][3].setXYZ(1.5, 0.5, 4.0);
//    	src[3][0].setXYZ(-2, 1.5, -2.0);src[3][1].setXYZ(-0.5, 1.5, 0.0);src[3][2].setXYZ(0.5, 1.5, 0.0);src[3][3].setXYZ(1.5, 1.5, -1.0);
    }
    
    //BezierSurface
    private void resultBezierSurface() {
        
        double mui, muj, bi = 0, bj = 0;
        for (int i = 0; i < dstCount; i++) {
            mui = (i) / (double)(dstCount - 1);
            for (int j = 0; j < dstCount; j++) {
                muj = (j) / (double)(dstCount - 1);
                dst[i][j].x = 0;
                dst[i][j].y = 0;
                dst[i][j].z = 0;
                for (int ki = 0; ki < srcCount; ki++) {
                    bi = bezierBlendFunction(ki,mui,srcCount - 1);
                    for (int kj = 0; kj < srcCount; kj++) {
                        bj = bezierBlendFunction(kj,muj,srcCount - 1);
                        dst[i][j].x += (src[ki][kj].x * bi * bj);
                        dst[i][j].y += (src[ki][kj].y * bi * bj);
                        dst[i][j].z += (src[ki][kj].z * bi * bj);
                    }
                }
            }
        }//End outest for
        vCount = dstCount * dstCount;
        int k = 0;
        float vertices[] = new float[vCount * 3];
        for (int i = 0; i < dstCount; i++) {
            for (int j = 0; j < dstCount; j++) {
            	vertices[k++] = dst[i][j].x;
            	vertices[k++] = dst[i][j].y;
            	vertices[k++] = dst[i][j].z;
            }
        }
		// ���������������ݻ���
		// vertices.length*4����Ϊһ�������ĸ��ֽ�
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// �����ֽ�˳��
		mVertexBuffer = vbb.asFloatBuffer();// ת��Ϊint�ͻ���
		mVertexBuffer.put(vertices);// �򻺳����з��붥����������
		mVertexBuffer.position(0);// ���û�������ʼλ��       
		//�������ƶ��㷨��������[ʹ�ö���������Ϊ������]
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(vertices);//�򻺳����з��붥����������
        mNormalBuffer.position(0);//���û�������ʼλ��     
        
    }
    
    private double bezierBlendFunction(int k, double mu, int n) {
        int nn = n, kn = k, nkn = n - k;
        double blend = 1;
        
        while (nn >= 1) {
            blend *= nn;
            nn = nn - 1;
            if (kn > 1) {
                blend /= (double)kn;
                kn--;
            }
            if (nkn > 1) {
                blend /= (double)nkn;
                nkn--;
            }
        }            
        if (k > 0) {
            blend *= Math.pow(mu, (double)k);
        }
        if (n - k > 0) {
            blend *= Math.pow(1 - mu, (double)(n - k)); 
        }
        return blend;
    }
    
    /**
	 * ������ɫ������Ӧ�ó�������ɫ���������С�����װ�䡿
	 */
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		/**
		 * ����һ��������Դ�ļ�����ɫ��Դ���룩��ȡ�� String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
		/**
		 * ���ڶ�����������ɫ��Դ���룬����OpenGL���ɵ�ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		/**
		 * ����������һ��OpenGL������ǰ�һ��������ɫ����һ��Ƭ����ɫ��������һ���ɵ������󣬶�����ɫ����Ƭ����ɫ������һ�����ġ� 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		glUseProgram(program);
		/*attribute������λ*/
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aColorLocation = glGetAttribLocation(program, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
		/*uniform������λ*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_Matrix");
		uColorLocation = glGetUniformLocation(program, "u_Color");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
		
	}
    
	public void drawSelf(float[] MVPMatrix){
		glUseProgram(program);
		// �����ձ任��������ɫ������
		GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false,
				MVPMatrix, 0);
		// ������λ�����ݴ�����Ⱦ����
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, mVertexBuffer);
		glEnableVertexAttribArray(aPositionLocation);
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);//������ɫ��������
		//����
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);
	}
}

