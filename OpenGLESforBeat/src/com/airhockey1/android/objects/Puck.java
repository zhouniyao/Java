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
							 TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;//һ�������
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
	 * �Ѷ������ݰ󶨵���ɫ��������������ϡ�
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
	public void draw(){
		for (DrawCommand drawCommand: drawList) {
			drawCommand.draw();
		}
	}
}
