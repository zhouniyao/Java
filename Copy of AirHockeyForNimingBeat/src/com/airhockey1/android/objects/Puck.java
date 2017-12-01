package com.airhockey1.android.objects;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

import java.util.List;

import android.annotation.SuppressLint;
import android.util.FloatMath;

import com.airhockey1.android.Constants;
import com.airhockey1.android.data.VertexArray;
import com.airhockey1.android.objects.ObjectBuilder.*;
import com.airhockey1.android.program.ColorShaderProgram;
import com.airhockey1.android.program.TextureShaderProgram;
import com.airhockey1.android.util.Geometry.*;

public class Puck {
//��������������
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
							 TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;//һ�������
	private static int offset = 0;
	
	//�ù��캯����ʹ��VertexArray�����ݸ��Ƶ������ڴ��е�һ��FloatBuffer
	private final VertexArray vertexArray;
	private static float[] vertexData;
	public final float radius, height;
	private static int numPoints;
	public Puck(float radius, float height, int numPointsAroundPuck) {
		this.radius = radius;
		this.height = height;
		this.numPoints = numPointsAroundPuck;
		vertexData = new float[(numPointsAroundPuck + 1) * STRIDE + 1];
		gen(new Circle(new Point(0, 0, 0), radius));
		vertexArray = new VertexArray(vertexData);
	}
	
	static void gen(Circle circle){
		
		//Center point of fan
		vertexData[offset++] = circle.center.x;
//		vertexData[offset++] = circle.center.y;
		vertexData[offset++] = circle.center.z;
		vertexData[offset++] = 0.5f;
		vertexData[offset++] = 0.5f;
		
		//Fan around center point <= is used because we want to generate
		//the point at the staring angle twice to complete the fan.
		for (int i = 0; i <= numPoints; i++) {
			float angleInRadians = 
					((float)i/(float)numPoints) * ((float)Math.PI * 2f);//��λ����ֵ
			float cosV = FloatMath.cos(angleInRadians);
			float sinV = FloatMath.sin(angleInRadians);
			vertexData[offset++] = circle.center.x + circle.radius * cosV;
//			vertexData[offset++] = circle.center.y;
			vertexData[offset++] = circle.center.z + circle.radius * sinV;
			vertexData[offset++] = (0.5f + cosV)  ;
			vertexData[offset++] = (0.5f + sinV) ;
//			//�����ظ����ơ�ʧ�ܡ�
//			if((0.5f + cosV) > 1){
//				vertexData[offset] = 1f;
//			}else if((0.5f + cosV) < -1){
//				vertexData[offset] = -1f;
//			}else {
//				vertexData[offset] = cosV + 0.5f;
//			}
//			
//			offset++;
//			
//			if((0.5f + sinV) > 1){
//				vertexData[offset] = 1f;
//			}else if((0.5f + sinV) < -1){
//				vertexData[offset] = -1f;
//			}else {
//				vertexData[offset] = sinV + 0.5f;
//			}
//			offset++;
		}
	}
	/*�Ѷ�������󶨵�һ����ɫ��������*/
	public void bindData(TextureShaderProgram textureProgram) {
		//����ɫ�������ȡÿ�����Ե�λ�á�
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
		glDrawArrays(GL_TRIANGLE_FAN, 0, numPoints +2);
		
	}
}

