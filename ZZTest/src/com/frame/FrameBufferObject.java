package com.frame;

import static android.opengl.GLES20.*;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.Matrix;
import android.util.Log;

/**
 * 建构一个完整的帧缓冲必须满足以下条件：
1、我们必须往里面加入至少一个附件（颜色、深度、模板缓冲）。
2、其中至少有一个是颜色附件。
3、所有的附件都应该是已经完全做好的（已经存储在内存之中）。
4、每个缓冲都应该有同样数目的样本。
 */

/**
 * 帧缓冲区对象支持如下操作：
1、使用OpenGL ES的API创建帧缓冲区对象。
2、一个EGLContext 中可以创建多个帧缓冲区对象，免去切换的操作。
3、帧缓冲区对象可以连接到 屏幕外颜色、深度、模版缓冲区 和 纹理。(FrameBufferObject 和 RenderBuffer/Texture2D 连接)
4、可以在多个帧缓冲区之间共享颜色、深度、模版缓冲区
5、帧缓冲区对象可以直接连接纹理，避免复制操作。
6、在帧缓冲区之间复制并使帧缓冲区内容失效
 */



public class FrameBufferObject {

    public static final String TAG = "GenFBO";
    
	/**帧缓存对象生成与绑定*/
		int fboId, fboTex, renderBufferId;
		
		public void initFBO(int fboWidth, int fboHeight){
			
			
//			//查询当前GLES实现所支持的最大的RenderBufferSize,就是尺寸  
//			glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, &m_maxRenderBufferSize);  
//			  
//			//如果我们设定的图片尺寸超过了GLES实现所支持的尺寸，就抛出错误  
//			if ((m_maxRenderBufferSize <= m_textureWidth) || (m_maxRenderBufferSize <= m_textureHeight))  
//			{  
//			    return;  
//			}  

			//generate fbo id
			int[] temp = new int[1];
			glGenFramebuffers(1, temp, 0);//Framebuffer对象（FBO）创建
			fboId = temp[0];

			//Bind Frame buffer
			glBindFramebuffer(GL_FRAMEBUFFER, fboId);//绑定FBO，接下来所有的读、写帧缓冲的操作都会影响到当前绑定的帧缓冲。

			
			
			/**需要为帧缓冲创建一些附件(Attachment)，还需要把这些附件附加到帧缓冲上*/
			//后续所有渲染操作将渲染到当前绑定的帧缓冲的附加缓冲中，
				
			/**创建【帧缓存区纹理对象】，使用纹理的好处是，所有渲染操作的结果都会被储存为一个纹理图像，这样我们就可以简单的在着色器中使用了。*/
			// Create a color attachment texture
			 glGenTextures(1, temp, 0);
			 fboTex = temp[0];
			//Define texture parameters
			 glTexImage2D( GL_TEXTURE_2D, 0,  GL_RGBA, fboWidth, fboHeight, 0,  GL_RGBA,  GL_UNSIGNED_BYTE, null);
	
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S,  GL_CLAMP_TO_EDGE);
	
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T,  GL_CLAMP_TO_EDGE);
	
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MAG_FILTER,  GL_LINEAR);//纹理过滤
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
			 
			 /**将纹理对象 连接到 帧缓冲区对象的颜色附着点*/
			 //Bind texture
			 glBindTexture(GL_TEXTURE_2D, fboTex);
			 //Attach texture FBO color attachment, Attach it to currently bound framebuffer object
			 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fboTex, 0);//纹理附到FBO
				/* glFramebufferTexture2D(target, attachment, textarget, texture, level);//Attaching Texture Images to a Framebuffer.
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
			 
			//Attaching Renderbuffer Images to Framebuffer
			 glFramebufferRenderbuffer( GL_FRAMEBUFFER,  GL_DEPTH_ATTACHMENT,  GL_RENDERBUFFER, renderBufferId);//Renderbuffer附着FBO

			//we are done, reset
			 glBindTexture( GL_TEXTURE_2D, 0);

			 glBindRenderbuffer( GL_RENDERBUFFER, 0);
			 
				 //然后检测帧缓冲区完整性，如果不完整的话提示错误
				 if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
					 Log.v(TAG, "ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
				 }
				 //breaks the existing binding of a framebuffer object to the target
				 glBindFramebuffer( GL_FRAMEBUFFER, 0);//通过绑定为0来使【默认帧缓冲】被激活
			 
		}
		
		
		
		
		/**
		 * 现在帧缓冲做好了，我们要做的全部就是渲染到帧缓冲上，
		 * 而不是绑定到帧缓冲对象的默认缓冲。余下所有命令会影响到当前绑定的帧缓冲上。
		 * 所有深度和模板操作同样会从当前绑定的帧缓冲的深度和模板附件中读取，当然，得是在它们可用的情况下。
		 * 如果你遗漏了比如深度缓冲，所有深度测试就不会工作，因为当前绑定的帧缓冲里没有深度缓冲。

			所以，为把场景绘制到一个单独的纹理，我们必须以下面步骤来做：
			1、使用新的绑定为激活帧缓冲的帧缓冲，像往常那样渲染场景。
			2、绑定到默认帧缓冲。
			3、绘制一个四边形，让它平铺到整个屏幕上，用新的帧缓冲的颜色缓冲作为他的纹理。
		 */
		
		
		
		
		
		public void RenderToTexture (GL10 arg0, int fboWidth, int fboHeight){

			glBindFramebuffer(GL_FRAMEBUFFER, fboId);//使用心得帧缓冲

			glViewport(0, 0, fboWidth, fboHeight);

			        //******Rendering Code*******
			initFBO(fboWidth, fboHeight);
			
//			glBindFramebuffer(GL_FRAMEBUFFER, 0);

		}
		
//		public void onDrawFrame(GL10 arg0)
//
//		{
//
//		    //call FBORenderer to render to texture
//
//		    fbor.RenderToTexture();//调用RenderToTexture 方法，将纹理保存在fbor的缓冲区中
//
//		    //reset the projection, because viewport is set by FBO renderer is different
//
//		    glViewport(0, 0, vwidth, vheight);
//
//		    float ratio = (float)vwidth/(float)vheight;
//
//		    float a = 5f;
//
//		    Matrix.orthoM(m_fProj, 0, -a*ratio, a*ratio, -a*ratio, a*ratio, 1, 10);   
//
//		    //multiply view matrix with projection matrix
//
//		    Matrix.multiplyMM(m_fVPMat, 0, m_fProj, 0, m_fView, 0);
//
//		    //below procedure is same as any other rendering
//
//		    glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
//
//		   
//
//		    glUseProgram(iProgId);
//
//		   
//
//		    vertexBuffer.position(0);
//
//		    glVertexAttribPointer(iPosition, 3, GL_FLOAT, false, 0, vertexBuffer);
//
//		    glEnableVertexAttribArray(iPosition);
//
//		   
//
//		    texBuffer.position(0);
//
//		    glVertexAttribPointer(iTexCoords, 2, GL_FLOAT, false, 0, texBuffer);
//
//		    glEnableVertexAttribArray(iTexCoords);
//
//		   
//
//		    glActiveTexture(GL_TEXTURE0);
//
//		    glBindTexture(GL_TEXTURE_2D, iTexId);
//
//		    glUniform1i(iTexLoc, 0);
//
//		    //since I'm multi-texturing, bind fboId to texture1
//
//		    glActiveTexture(GL_TEXTURE1);
//
//		    glBindTexture(GL_TEXTURE_2D, fboId);
//
//		    glUniform1i(iTexLoc1, 1);
//
//		    //for rotating cube
//
//		    Matrix.setIdentityM(m_fModel, 0);
//
//		    Matrix.rotateM(m_fModel, 0, -xAngle, 0, 1, 0);
//
//		    Matrix.rotateM(m_fModel, 0, -yAngle, 1, 0, 0);
//
//		    //multiply model matrix with view-projection matrix
//
//		    Matrix.multiplyMM(m_fMVPMat, 0, m_fVPMat, 0, m_fModel, 0);   
//
//		    //pass model-view-projection matrix to shader
//
//		    glUniformMatrix4fv(iMVPMat, 1, false, m_fMVPMat, 0);   
//
//		    //draw cube
//		    glDrawElements(GL_TRIANGLES, cube.m_nIndeces, GL_UNSIGNED_SHORT, indexBuffer);
//
//		}

}
