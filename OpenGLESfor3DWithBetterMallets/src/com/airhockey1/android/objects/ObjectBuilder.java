package com.airhockey1.android.objects;

import static android.opengl.GLES20.*;

import java.util.ArrayList;
import java.util.List;

import com.airhockey1.android.util.Geometry.Circle;
import com.airhockey1.android.util.Geometry.Cylinder;
import com.airhockey1.android.util.Geometry.Point;

import android.util.FloatMath;


public class ObjectBuilder {
	/**
	 * 因为冰球是两个基本图元构成，所以我们需要一种方法，它可以把这些绘画命令合并在一起。
	 * 我们创建一个interface表示单个绘制命令。
	 * @author Niming
	 */
	static interface DrawCommand{
		void draw();
	}
	
	/**
	 * holder类
	 * @author Niming
	 */
	static class GeneratedData{
		final float[] vertexData;
		final List<DrawCommand> drawList;
		
		GeneratedData(float[] vertexData, List<DrawCommand> drawList){
			this.vertexData = vertexData;
			this.drawList = drawList;
		}
	}
	
	private static final int FLOATS_PER_VERTEX = 3;
	private final float[] vertexData;
	private int offset = 0;
	
	private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
	
	private ObjectBuilder(int sizeInVertices){
		vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];//基于顶点数量初始化数组
	}
	
	/**
	 * 计算圆的顶点数量
	 * @param numPoints
	 * @return
	 */
	private static int sizeOfCircleInVertices(int numPoints){
		return 1 + (numPoints + 1); 
	}
	
	/**
	 * 计算圆柱侧面顶点数量
	 */
	private static int sizeOfOpenCylinderInVertices(int numPoints){
		return (numPoints + 1) * 2;
	}
	
	/**
	 * 用三角形扇形构造圆
	 */
	private void appendCircle(Circle circle, int numPoints) {
		
		final int startVertex = offset / FLOATS_PER_VERTEX;//顶点数据的偏移值
		final int numVertices = sizeOfCircleInVertices(numPoints);//顶点数据长度
		
		//Center point of fan
		vertexData[offset++] = circle.center.x;
		vertexData[offset++] = circle.center.y;
		vertexData[offset++] = circle.center.z;
		
		//Fan around center point <= is used because we want to generate
		//the point at the staring angle twice to complete the fan.
		for (int i = 0; i <= numPoints; i++) {
			float angleInRadians = 
					((float)i/(float)numPoints) * ((float)Math.PI * 2f);//单位弧度值
			
			vertexData[offset++] = circle.center.x + circle.radius * FloatMath.cos(angleInRadians);
			vertexData[offset++] = circle.center.y;
			vertexData[offset++] = circle.center.z + circle.radius * FloatMath.sin(angleInRadians);
		}
		drawList.add(new DrawCommand(){

			@Override
			public void draw() {
				glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
			}});
	}
	
	/**
	 * 用三角形带构建圆柱体侧面
	 * @param puck
	 * @param numPoints
	 */
	private void appendOpenCylinder(Cylinder puck, int numPoints) {
		final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
		final float yStart = puck.center.y - (puck.height / 2f); /*顶*/
		final float yEnd = puck.center.y + (puck.height / 2f);   /*底*/
		
		/*搞清楚冰球从哪里开始到哪里结束*/
		for (int i = 0; i <= numPoints; i++) {
			float angleInRadians = 
					((float)i/(float)numPoints)*((float)Math.PI * 2f);
			float xPosition = puck.center.x + puck.radius * FloatMath.cos(angleInRadians);
			float zPosition = puck.center.x + puck.radius * FloatMath.sin(angleInRadians);
			vertexData[offset++] = xPosition;
			vertexData[offset++] = yStart;
			vertexData[offset++] = zPosition;
			vertexData[offset++] = xPosition;
			vertexData[offset++] = yEnd;
			vertexData[offset++] = zPosition;
		}
		drawList.add(new DrawCommand(){

			@Override
			public void draw() {
				glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);   //strip长条
			}});
	}
	
	private GeneratedData build() {
		return new GeneratedData(vertexData, drawList);
	}
	
	/**
	 * 用圆柱体创建冰球
	 */
	static GeneratedData createPuck(Cylinder puck, int numPoints){
		//找出需要多少个顶点构建这个冰球
		int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
		ObjectBuilder builder = new ObjectBuilder(size);
		
		Circle puckTop = new Circle(puck.center.translateY(puck.height/2f), puck.radius);
		builder.appendCircle(puckTop, numPoints);
		builder.appendOpenCylinder(puck, numPoints);
		
		return builder.build();
	}
	
	/**
	 * 【8.3.5】
	 * 1)首先，从这个类的外部调用静态方法createPuck()。
	 * 这个方法创建一个新的ObjectBuilder对象，它用合适大小的数组保存这个冰球的所有数据。 同时也创建了一个显示列表，以便后续绘制这个冰球。
	 * 2)在createPuck()内部，我们调用appendCircle()和appendOpenCylinder()分别生成冰球的顶部和侧面。
	 * 每个方法都给vertexData添加它的数据，并给drawList添加了一个绘画命令。
	 * 3)最后，调用build()返回生成的数据。
	 */
	
	static GeneratedData createMallet(Point center, float radius, float height, int numPoints){
		
		int size = sizeOfCircleInVertices(numPoints)*2 + sizeOfOpenCylinderInVertices(numPoints)*2;
		ObjectBuilder builder = new ObjectBuilder(size);
		
		//First, generate the mallet base.
		float baseHeight = height * 0.25f;
		
		Circle baseCircle = new Circle(center.translateY(-baseHeight), radius);
		Cylinder baseCylinder = new Cylinder(baseCircle.center.translateY(-baseHeight / 2f), 
				radius, baseHeight);
		
		builder.appendCircle(baseCircle, numPoints);
		builder.appendOpenCylinder(baseCylinder, numPoints);
		
		//绘制手柄
		float handleHeight = height * 0.75f;
		float handleRadius = radius / 3f;
		
		Circle handleCircle = new Circle(center.translateY(height * 0.5f), handleRadius);
		Cylinder handleCylinder = new Cylinder(handleCircle.center.translateY(-handleHeight / 2f),
				handleRadius, handleHeight);
		
		builder.appendCircle(handleCircle, numPoints);
		builder.appendOpenCylinder(handleCylinder, numPoints);
		
		return builder.build();
	}
	
}
