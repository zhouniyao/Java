package com.frame;

import static android.opengl.GLES20.*;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.Matrix;
import android.util.Log;

/**
 * ����һ��������֡���������������������
1�����Ǳ����������������һ����������ɫ����ȡ�ģ�建�壩��
2������������һ������ɫ������
3�����еĸ�����Ӧ�����Ѿ���ȫ���õģ��Ѿ��洢���ڴ�֮�У���
4��ÿ�����嶼Ӧ����ͬ����Ŀ��������
 */

/**
 * ֡����������֧�����²�����
1��ʹ��OpenGL ES��API����֡����������
2��һ��EGLContext �п��Դ������֡������������ȥ�л��Ĳ�����
3��֡����������������ӵ� ��Ļ����ɫ����ȡ�ģ�滺���� �� ����(FrameBufferObject �� RenderBuffer/Texture2D ����)
4�������ڶ��֡������֮�乲����ɫ����ȡ�ģ�滺����
5��֡�������������ֱ�������������⸴�Ʋ�����
6����֡������֮�临�Ʋ�ʹ֡����������ʧЧ
 */



public class FrameBufferObject {

    public static final String TAG = "GenFBO";
    
	/**֡��������������*/
		int fboId, fboTex, renderBufferId;
		
		public void initFBO(int fboWidth, int fboHeight){
			
			
//			//��ѯ��ǰGLESʵ����֧�ֵ�����RenderBufferSize,���ǳߴ�  
//			glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, &m_maxRenderBufferSize);  
//			  
//			//��������趨��ͼƬ�ߴ糬����GLESʵ����֧�ֵĳߴ磬���׳�����  
//			if ((m_maxRenderBufferSize <= m_textureWidth) || (m_maxRenderBufferSize <= m_textureHeight))  
//			{  
//			    return;  
//			}  

			//generate fbo id
			int[] temp = new int[1];
			glGenFramebuffers(1, temp, 0);//Framebuffer����FBO������
			fboId = temp[0];

			//Bind Frame buffer
			glBindFramebuffer(GL_FRAMEBUFFER, fboId);//��FBO�����������еĶ���д֡����Ĳ�������Ӱ�쵽��ǰ�󶨵�֡���塣

			
			
			/**��ҪΪ֡���崴��һЩ����(Attachment)������Ҫ����Щ�������ӵ�֡������*/
			//����������Ⱦ��������Ⱦ����ǰ�󶨵�֡����ĸ��ӻ����У�
				
			/**������֡������������󡿣�ʹ������ĺô��ǣ�������Ⱦ�����Ľ�����ᱻ����Ϊһ������ͼ���������ǾͿ��Լ򵥵�����ɫ����ʹ���ˡ�*/
			// Create a color attachment texture
			 glGenTextures(1, temp, 0);
			 fboTex = temp[0];
			//Define texture parameters
			 glTexImage2D( GL_TEXTURE_2D, 0,  GL_RGBA, fboWidth, fboHeight, 0,  GL_RGBA,  GL_UNSIGNED_BYTE, null);
	
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S,  GL_CLAMP_TO_EDGE);
	
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T,  GL_CLAMP_TO_EDGE);
	
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MAG_FILTER,  GL_LINEAR);//�������
			 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
			 
			 /**��������� ���ӵ� ֡�������������ɫ���ŵ�*/
			 //Bind texture
			 glBindTexture(GL_TEXTURE_2D, fboTex);
			 //Attach texture FBO color attachment, Attach it to currently bound framebuffer object
			 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fboTex, 0);//������FBO
				/* glFramebufferTexture2D(target, attachment, textarget, texture, level);//Attaching Texture Images to a Framebuffer.
					 target��������������֡�������͵�Ŀ�꣨���ơ���ȡ�����߶��У���
					 attachment�����������ӵĸ��������͡��������Ǹ��ӵ���һ����ɫ��������Ҫע�⣬�����Ǹ�0�ǰ�ʾ���ǿ��Ը���1��������ɫ�ĸ��������ǻ��ں���Ľ̳���̸����
					 textarget����ϣ�����ӵ��������͡�
					 texture�����ӵ�ʵ������
					 level��Mipmap level����������Ϊ0��
				 */
				 
				 
				 
				 /*
				  * ��֡������Ŀ�У���Ⱦ�����������ṩһЩ�Ż���������Ҫ����֪����ʱʹ����Ⱦ������󣬺�ʱʹ������
				  * ͨ���Ĺ����ǣ��������Զ������Ҫ���ض��Ļ����н��в�������Ⱦ���������ض������Ǹ����ǵ�ѡ��
				  * ���������Ҫ�ӱ�����ɫ�����ֵ�������ض�����������ݵĻ�������û���ʹ����������
				  * ��ִ��Ч�ʽǶȿ��ǣ��������Ч����̫��Ӱ�졣
				  * Ψһһ��Ҫ��ס�������ǣ��������ڴ�������һ����Ⱦ����������Ⱥ�ģ�帽�������ǰ������ڲ���������ΪGL_DEPTH24_STENCIL8
				  */


			//generate render buffer
			glGenRenderbuffers(1, temp, 0);
			renderBufferId = temp[0];

			//Bind render buffer and define buffer dimension
			 glBindRenderbuffer( GL_RENDERBUFFER, renderBufferId);//����Ⱦ�������󶨣��������к�����Ⱦ�����������Ӱ�쵽��ǰ����Ⱦ�������
			 glRenderbufferStorage( GL_RENDERBUFFER,  GL_DEPTH_COMPONENT16, fboWidth, fboHeight);//����һ�������Ⱦ�������// Use a single renderbuffer object for both a depth AND stencil buffer.
			 
			//Attaching Renderbuffer Images to Framebuffer
			 glFramebufferRenderbuffer( GL_FRAMEBUFFER,  GL_DEPTH_ATTACHMENT,  GL_RENDERBUFFER, renderBufferId);//Renderbuffer����FBO

			//we are done, reset
			 glBindTexture( GL_TEXTURE_2D, 0);

			 glBindRenderbuffer( GL_RENDERBUFFER, 0);
			 
				 //Ȼ����֡�����������ԣ�����������Ļ���ʾ����
				 if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
					 Log.v(TAG, "ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
				 }
				 //breaks the existing binding of a framebuffer object to the target
				 glBindFramebuffer( GL_FRAMEBUFFER, 0);//ͨ����Ϊ0��ʹ��Ĭ��֡���塿������
			 
		}
		
		
		
		
		/**
		 * ����֡���������ˣ�����Ҫ����ȫ��������Ⱦ��֡�����ϣ�
		 * �����ǰ󶨵�֡��������Ĭ�ϻ��塣�������������Ӱ�쵽��ǰ�󶨵�֡�����ϡ�
		 * ������Ⱥ�ģ�����ͬ����ӵ�ǰ�󶨵�֡�������Ⱥ�ģ�帽���ж�ȡ����Ȼ�����������ǿ��õ�����¡�
		 * �������©�˱�����Ȼ��壬������Ȳ��ԾͲ��Ṥ������Ϊ��ǰ�󶨵�֡������û����Ȼ��塣

			���ԣ�Ϊ�ѳ������Ƶ�һ���������������Ǳ��������沽��������
			1��ʹ���µİ�Ϊ����֡�����֡���壬������������Ⱦ������
			2���󶨵�Ĭ��֡���塣
			3������һ���ı��Σ�����ƽ�̵�������Ļ�ϣ����µ�֡�������ɫ������Ϊ��������
		 */
		
		
		
		
		
		public void RenderToTexture (GL10 arg0, int fboWidth, int fboHeight){

			glBindFramebuffer(GL_FRAMEBUFFER, fboId);//ʹ���ĵ�֡����

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
//		    fbor.RenderToTexture();//����RenderToTexture ����������������fbor�Ļ�������
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
