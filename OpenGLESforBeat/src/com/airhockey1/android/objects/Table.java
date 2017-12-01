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
							 TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;//一步，跨幅
	private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y, S, T

        // Triangle Fan
           0f,    0f, 0.5f, 0.5f, 
        -0.5f, -0.8f,   0f, 0.9f,  //纹理被裁剪
         0.5f, -0.8f,   1f, 0.9f, 
         0.5f,  0.8f,   1f, 0.1f, 
        -0.5f,  0.8f,   0f, 0.1f, 
        -0.5f, -0.8f,   0f, 0.9f 		
	};
	
	//该构造函数会使用VertexArray把数据复制到本地内存中的一个FloatBuffer
	private final VertexArray vertexArray;
	public Table(){
		vertexArray = new VertexArray(VERTEX_DATA);
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
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		
	}
}
