package com.airhockey1.android.objects;

import android.R.integer;
import static android.opengl.GLES20.*;
import com.airhockey1.android.Constants;
import com.airhockey1.android.data.VertexArray;
import com.airhockey1.android.program.TextureShaderProgram;
import com.airhockey1.android.util.TextureHelper;

public class Table {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
							 TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;//һ�������
	private static final float[] VERTEX_DATA = {
//x=0,y=0��Ӧ����S=0.5,T=0.5;    x=-0.5,y=-0.8��Ӧ����S=0,T=0.9;
//֮���������ֶ�Ӧ��ϵ������ǰ�潲����OpenGL��������������ͼ������ĶԱȾ�����������X�ᷭת180��
        // Triangle Fan
		// Order of coordinates: X, Y, S, T
           0f,    0f,   0.5f, 0.5f, 
        -0.5f, -0.8f,   0f, 0.9f,  //�����ü�
         0.5f, -0.8f,   1f, 0.9f, 
         0.5f,  0.8f,   1f, 0.1f, 
        -0.5f,  0.8f,   0f, 0.1f, 
        -0.5f, -0.8f,   0f, 0.9f 		
	};
	
	private final VertexArray vertexArray;
	//�ù��캯����ʹ��VertexArray�����ݸ��Ƶ������ڴ��е�һ��FloatBuffer
	public Table(){
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	/**�Ѷ���+�������顿�󶨵�һ����ɫ��������*/
	public void bindData(TextureShaderProgram textureProgram) {
		//��ɫ��Attribute�����������ڴ��ȡvertexArray
		vertexArray.setVertexAttribPointer(
				0, 
				textureProgram.getPositionAttributeLocation(),//��λ�����ݰ󶨵� �����õ���ɫ��������
				POSITION_COMPONENT_COUNT, 
				STRIDE);
		vertexArray.setVertexAttribPointer(
				POSITION_COMPONENT_COUNT, 
				textureProgram.getTextureCoordinatesAttributeLocation(), //�������������� �󶨵� �����õ���ɫ��������
				TEXTURE_COORDINATES_COMPONENT_COUNT, 
				STRIDE);
	}
	
	public void draw() {
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		
	}
}
