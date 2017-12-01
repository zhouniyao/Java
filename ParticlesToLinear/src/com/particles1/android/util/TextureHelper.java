package com.particles1.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import static android.opengl.GLES20.*;
import android.opengl.GLUtils;

public class TextureHelper {
	private static final String TAG = "TextureHelper";
	/**
	 * loadTexture()方法，从资源文件夹读入图像文件，并把图形数据加载进OpenGL，并取回一个纹理ID。如果失败，返回0.
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public static int loadTexture(Context context, int resourceId){
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);//生成纹理对象，OpenGL会把生成的ID存储在textureObjectIds中。
		
		if(textureObjectIds[0] == 0){
			if(LoggerConfig.ON)
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			return 0;
		}
		
		/*加载位图数据并与纹理绑定*/
		final BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inScaled = false;//告诉Android我们想要原始的图像数据，而不是这个图像的缩放版本。
		
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceId, options);//实际的解码工作，这个调用会把解码后的图像存入OpenGL
		if(bitmap == null){
			if(LoggerConfig.ON){
				Log.w(TAG, "Resource ID" + resourceId + "Could not be decode.");
			}
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		//告诉OpenGL后面纹理调用应该应用于这个纹理对象。
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		
		//设置纹理过滤参数
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);/*缩小*/
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);/*放大*/
		
		/*7.2.4 加载纹理到OpenGL并返回其ID*/
		
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);/*该函数调用告诉OpenGL读入bitmap定义的位图数据，并把它复制到当前绑定的纹理对象*/
		/*既然这些数据已经被加载进OpenGL，我们就不再需要持有Android的位图*/
		//立即释放数据
		bitmap.recycle();
		
		glGenerateMipmap(GL_TEXTURE_2D);/*生产MIP贴图*/
		glBindTexture(GL_TEXTURE_2D, 0);/*传递0为解除与当前纹理绑定*/
		
		return textureObjectIds[0];
	}
	
}
