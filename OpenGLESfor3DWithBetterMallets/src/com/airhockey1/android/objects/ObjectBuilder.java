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
	 * ��Ϊ��������������ͼԪ���ɣ�����������Ҫһ�ַ����������԰���Щ�滭����ϲ���һ��
	 * ���Ǵ���һ��interface��ʾ�����������
	 * @author Niming
	 */
	static interface DrawCommand{
		void draw();
	}
	
	/**
	 * holder��
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
		vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];//���ڶ���������ʼ������
	}
	
	/**
	 * ����Բ�Ķ�������
	 * @param numPoints
	 * @return
	 */
	private static int sizeOfCircleInVertices(int numPoints){
		return 1 + (numPoints + 1); 
	}
	
	/**
	 * ����Բ�����涥������
	 */
	private static int sizeOfOpenCylinderInVertices(int numPoints){
		return (numPoints + 1) * 2;
	}
	
	/**
	 * �����������ι���Բ
	 */
	private void appendCircle(Circle circle, int numPoints) {
		
		final int startVertex = offset / FLOATS_PER_VERTEX;//�������ݵ�ƫ��ֵ
		final int numVertices = sizeOfCircleInVertices(numPoints);//�������ݳ���
		
		//Center point of fan
		vertexData[offset++] = circle.center.x;
		vertexData[offset++] = circle.center.y;
		vertexData[offset++] = circle.center.z;
		
		//Fan around center point <= is used because we want to generate
		//the point at the staring angle twice to complete the fan.
		for (int i = 0; i <= numPoints; i++) {
			float angleInRadians = 
					((float)i/(float)numPoints) * ((float)Math.PI * 2f);//��λ����ֵ
			
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
	 * �������δ�����Բ�������
	 * @param puck
	 * @param numPoints
	 */
	private void appendOpenCylinder(Cylinder puck, int numPoints) {
		final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
		final float yStart = puck.center.y - (puck.height / 2f); /*��*/
		final float yEnd = puck.center.y + (puck.height / 2f);   /*��*/
		
		/*�������������￪ʼ���������*/
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
				glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);   //strip����
			}});
	}
	
	private GeneratedData build() {
		return new GeneratedData(vertexData, drawList);
	}
	
	/**
	 * ��Բ���崴������
	 */
	static GeneratedData createPuck(Cylinder puck, int numPoints){
		//�ҳ���Ҫ���ٸ����㹹���������
		int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
		ObjectBuilder builder = new ObjectBuilder(size);
		
		Circle puckTop = new Circle(puck.center.translateY(puck.height/2f), puck.radius);
		builder.appendCircle(puckTop, numPoints);
		builder.appendOpenCylinder(puck, numPoints);
		
		return builder.build();
	}
	
	/**
	 * ��8.3.5��
	 * 1)���ȣ����������ⲿ���þ�̬����createPuck()��
	 * �����������һ���µ�ObjectBuilder�������ú��ʴ�С�����鱣�����������������ݡ� ͬʱҲ������һ����ʾ�б��Ա���������������
	 * 2)��createPuck()�ڲ������ǵ���appendCircle()��appendOpenCylinder()�ֱ����ɱ���Ķ����Ͳ��档
	 * ÿ����������vertexData����������ݣ�����drawList�����һ���滭���
	 * 3)��󣬵���build()�������ɵ����ݡ�
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
		
		//�����ֱ�
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
