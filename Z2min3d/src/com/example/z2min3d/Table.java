package com.example.z2min3d;

import static android.opengl.GLES20.*;
import static niming.util.Constants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Table {
	FloatBuffer vertexData;
	float[] tableVerticesWithTriangles = {
			//table brim
			-0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
			-0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
			-0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f, -0.85f, 0.5f, 0.5f, 1.0f,
			 0.55f,  0.85f, 0.5f, 0.5f, 1.0f,
            // Triangle Fan
            0f,     0f,    1f,    1f,    1f,         
            -0.5f, -0.8f, 0.7f, 0.7f, 0f,        //ƽ���Ż�ɫ    
             0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
             0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
             0.5f, 0f, 1f, 0f, 0f,

            // Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f,  0.4f, 1f, 0f, 0f
		};
	
	public Table() {
		//����һ����СΪ�������ı����ڴ��
		vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * 4)
				//�����ֽڻ�����(byte buffer)���ձ����ֽ���(native byte order)��֯��������
				.order(ByteOrder.nativeOrder())
				//���ǲ�Ըֱ�Ӳ����������ֽڣ�����ϣ��ʹ�ø���������˵���asFloatBuffer()�õ�һ�����Է�ӳ�ײ��ֽڵ�FloatBuffer���ʵ��
				.asFloatBuffer();
		//Ȼ�����		vertexData.put(tableVerticesWithTriangles)�����ݴ�Dalvik���ڴ��и��Ƶ������ڴ�
		vertexData.put(tableVerticesWithTriangles);
	}
	public void bindData(int aPositionLocation){
		vertexData.position(0);//�����ڴ��еĶ������飬����ָ��λ�ù�0
	//	glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
		/*OpenGL�ӻ�������vertexData���ж�ȡ����*/
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 2, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
	}
	public void draw(){
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 16);
	}
}
