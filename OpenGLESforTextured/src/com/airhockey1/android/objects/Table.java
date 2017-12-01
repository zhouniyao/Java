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
//x=0,y=0对应纹理S=0.5,T=0.5;    x=-0.5,y=-0.8对应纹理S=0,T=0.9;
//之所以有这种对应关系，看下前面讲到的OpenGL纹理坐标与计算机图像坐标的对比就清楚啦。相对X轴翻转180°
        // Triangle Fan
		// Order of coordinates: X, Y, S, T
           0f,    0f,   0.5f, 0.5f, 
        -0.5f, -0.8f,   0f, 0.9f,  //纹理被裁剪
         0.5f, -0.8f,   1f, 0.9f, 
         0.5f,  0.8f,   1f, 0.1f, 
        -0.5f,  0.8f,   0f, 0.1f, 
        -0.5f, -0.8f,   0f, 0.9f 		
	};
	
	private final VertexArray vertexArray;
	//该构造函数会使用VertexArray把数据复制到本地内存中的一个FloatBuffer
	public Table(){
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	/**把顶点+纹理【数组】绑定到一个着色器程序上*/
	public void bindData(TextureShaderProgram textureProgram) {
		//着色器Attribute变量到本地内存读取vertexArray
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
