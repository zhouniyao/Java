package com.airhockey1.android.objects;

import java.util.List;

import com.airhockey1.android.Constants;
import com.airhockey1.android.data.VertexArray;
import com.airhockey1.android.objects.ObjectBuilder.*;
import com.airhockey1.android.program.ColorShaderProgram;
import com.airhockey1.android.program.TextureShaderProgram;
import com.airhockey1.android.util.Geometry.*;

public class Puck {
	private static final int POSITION_COMPONENT_COUNT = 3;
//	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
							 TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;//一步，跨幅
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
	public void	bindData(TextureShaderProgram textureProgram) {
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
	public void draw(){
		for (DrawCommand drawCommand: drawList) {
			drawCommand.draw();
		}
	}
}
