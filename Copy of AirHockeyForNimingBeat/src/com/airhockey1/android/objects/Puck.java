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
//给表面添——纹理
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
							 TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;//一步，跨幅
	private static int offset = 0;
	
	//该构造函数会使用VertexArray把数据复制到本地内存中的一个FloatBuffer
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
					((float)i/(float)numPoints) * ((float)Math.PI * 2f);//单位弧度值
			float cosV = FloatMath.cos(angleInRadians);
			float sinV = FloatMath.sin(angleInRadians);
			vertexData[offset++] = circle.center.x + circle.radius * cosV;
//			vertexData[offset++] = circle.center.y;
			vertexData[offset++] = circle.center.z + circle.radius * sinV;
			vertexData[offset++] = (0.5f + cosV)  ;
			vertexData[offset++] = (0.5f + sinV) ;
//			//避免重复绘制【失败】
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
	/*把顶点数组绑定到一个着色器程序上*/
	public void bindData(TextureShaderProgram textureProgram) {
		//从着色器程序获取每个属性的位置。
		vertexArray.setVertexAttribPointer(
				0, 
				textureProgram.getPositionAttributeLocation(),//把位置数据绑定到 被引用的着色器属性上
				POSITION_COMPONENT_COUNT, 
				STRIDE);
		vertexArray.setVertexAttribPointer(
				POSITION_COMPONENT_COUNT, 
				textureProgram.getTextureCoordinatesAttributeLocation(), //把纹理坐标数据 绑定到 被引用的着色器属性上
				TEXTURE_COORDINATES_COMPONENT_COUNT, 
				STRIDE);
	}
	
	public void draw() {
		glDrawArrays(GL_TRIANGLE_FAN, 0, numPoints +2);
		
	}
}

