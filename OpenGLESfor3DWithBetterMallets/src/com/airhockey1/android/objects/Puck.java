package com.airhockey1.android.objects;

import java.util.List;

import com.airhockey1.android.data.VertexArray;
import com.airhockey1.android.objects.ObjectBuilder.*;
import com.airhockey1.android.program.ColorShaderProgram;
import com.airhockey1.android.util.Geometry.*;

public class Puck {
	private static final int POSITION_COMPONENT_COUNT = 3;
	public final float radius, height;
	
	private final VertexArray vertexArray;
	private final List<DrawCommand> drawList;
	
	public Puck(float radius, float height, int numPointsAroundPuck) {
		GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(
				new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
		this.radius = radius;
		this.height = height;
		
		vertexArray = new VertexArray(generatedData.vertexData);
		drawList = generatedData.drawList;
	}
	/**
	 * 把顶点数据绑定到着色器程序定义的属性上。
	 * @param colorProgram
	 */
	public void bindData(ColorShaderProgram colorProgram){
		vertexArray.setVertexAttribPointer(
				0,
				colorProgram.getPositionAttrbuteLocation(),
				POSITION_COMPONENT_COUNT,
				0
				);
	}
	public void draw(){
		for (DrawCommand drawCommand: drawList) {
			drawCommand.draw();
		}
	}
}
