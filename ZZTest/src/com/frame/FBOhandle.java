package com.frame;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import android.content.Context;
import android.util.Log;

public class FBOhandle {
    public static final String TAG = "FBO_niming";
    final Context context;
    /**
     * 帧缓存对象生成与绑定【顺序】
     * 生成fbo、fboTex、renderbuf
     * OpenGL绑定fbo
     * OpenGL绑定fboTex、配置纹理
     * OpenGL绑定renderbuf、配置渲染缓冲
     * fbo附着纹理缓冲与渲染缓冲
     */
    public int fboId;
	public int fboTex;
	public int renderBufferId;
    
    public FBOhandle(Context context){
    	this.context = context;
    }
	public void initFBO( int fboWidth, int fboHeight){
		int[] temp = new int[1];
		
		
//		//查询当前GLES实现所支持的最大的RenderBufferSize,就是尺寸  
//		glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, &m_maxRenderBufferSize);  
//		  
//		//如果我们设定的图片尺寸超过了GLES实现所支持的尺寸，就抛出错误  
//		if ((m_maxRenderBufferSize <= m_textureWidth) || (m_maxRenderBufferSize <= m_textureHeight))  
//		{  
//		    return;  
//		}  

		//generate fbo id
		glGenFramebuffers(1, temp, 0);//Framebuffer对象（FBO）创建
		fboId = temp[0];

		//Bind Frame buffer
		glBindFramebuffer(GL_FRAMEBUFFER, fboId);//绑定FBO，接下来所有的读、写帧缓冲的操作都会影响到当前绑定的帧缓冲。

		
		/**需要为帧缓冲创建一些附件(Attachment)，还需要把这些附件附加到帧缓冲上*/
		//后续所有渲染操作将渲染到当前绑定的帧缓冲的附加缓冲中，
//=====================================================================================	
		/**创建【帧缓存区纹理对象】，使用纹理的好处是，所有渲染操作的结果都会被储存为一个纹理图像，这样我们就可以简单的在着色器中使用了。*/
		// Create a color attachment texture
		 glGenTextures(1, temp, 0);
		 fboTex = temp[0];
		 //Bind texture
		 glBindTexture(GL_TEXTURE_2D, fboTex);
		//Define texture parameters
		 glTexImage2D( GL_TEXTURE_2D, 0,  GL_RGBA, fboWidth, fboHeight, 0,  GL_RGBA,  GL_UNSIGNED_BYTE, null);//设置纹理参数，待加载资源
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S,  GL_CLAMP_TO_EDGE);
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T,  GL_CLAMP_TO_EDGE);
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MAG_FILTER,  GL_LINEAR);//纹理过滤
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
		 
		 /**将纹理对象 连接到 FBO的颜色附着点*/
		 //Attach texture FBO color attachment, Attach it to currently bound framebuffer object
//		 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fboTex, 0);//纹理附到FBO
			/* glFramebufferTexture2D(target, attachment, textarget, texture, level);
				 target：我们所创建的帧缓冲类型的目标（绘制、读取或两者都有）。
				 attachment：我们所附加的附件的类型。现在我们附加的是一个颜色附件。需要注意，最后的那个0是暗示我们可以附加1个以上颜色的附件。我们会在后面的教程中谈到。
				 textarget：你希望附加的纹理类型。
				 texture：附加的实际纹理。
				 level：Mipmap level。我们设置为0。
			 */
			 
			 
			 
			 /*
			  * 在帧缓冲项目中，渲染缓冲对象可以提供一些优化，但更重要的是知道何时使用渲染缓冲对象，何时使用纹理。
			  * 通常的规则是，如果你永远都不需要从特定的缓冲中进行采样，渲染缓冲对象对特定缓冲是更明智的选择。
			  * 如果哪天需要从比如颜色或深度值这样的特定缓冲采样数据的话，你最好还是使用纹理附件。
			  * 从执行效率角度考虑，它不会对效率有太大影响。
			  * 唯一一件要记住的事情是，我们正在创建的是一个渲染缓冲对象的深度和模板附件。我们把它的内部给事设置为GL_DEPTH24_STENCIL8
			  */


			//generate render buffer
			glGenRenderbuffers(1, temp, 0);
			renderBufferId = temp[0];
			//Bind render buffer and define buffer dimension
			glBindRenderbuffer( GL_RENDERBUFFER, renderBufferId);//把渲染缓冲对象绑定，这样所有后续渲染缓冲操作都会影响到当前的渲染缓冲对象
			glRenderbufferStorage( GL_RENDERBUFFER,  GL_DEPTH_COMPONENT16, fboWidth, fboHeight);//创建一个深度渲染缓冲对象// Use a single renderbuffer object for both a depth AND stencil buffer.


			 
			
			 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fboTex, 0);//纹理附到FBO
			//Attaching Renderbuffer Images to Framebuffer
			 glFramebufferRenderbuffer( GL_FRAMEBUFFER,  GL_DEPTH_ATTACHMENT,  GL_RENDERBUFFER, renderBufferId);//Renderbuffer附着FBO

			 //然后检测帧缓冲区完整性，如果不完整的话提示错误
			 if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
				 Log.v(TAG, "ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
			 }else{
				 Log.v(TAG, "OK::FRAMEBUFFER:: Framebuffer is complete!");
			 }
			 //we are done, reset
//			 glBindTexture( GL_TEXTURE_2D, 0);
//			 glBindRenderbuffer( GL_RENDERBUFFER, 0);
//			 glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}
