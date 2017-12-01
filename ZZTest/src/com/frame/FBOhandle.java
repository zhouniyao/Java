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
     * ֡�������������󶨡�˳��
     * ����fbo��fboTex��renderbuf
     * OpenGL��fbo
     * OpenGL��fboTex����������
     * OpenGL��renderbuf��������Ⱦ����
     * fbo��������������Ⱦ����
     */
    public int fboId;
	public int fboTex;
	public int renderBufferId;
    
    public FBOhandle(Context context){
    	this.context = context;
    }
	public void initFBO( int fboWidth, int fboHeight){
		int[] temp = new int[1];
		
		
//		//��ѯ��ǰGLESʵ����֧�ֵ�����RenderBufferSize,���ǳߴ�  
//		glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, &m_maxRenderBufferSize);  
//		  
//		//��������趨��ͼƬ�ߴ糬����GLESʵ����֧�ֵĳߴ磬���׳�����  
//		if ((m_maxRenderBufferSize <= m_textureWidth) || (m_maxRenderBufferSize <= m_textureHeight))  
//		{  
//		    return;  
//		}  

		//generate fbo id
		glGenFramebuffers(1, temp, 0);//Framebuffer����FBO������
		fboId = temp[0];

		//Bind Frame buffer
		glBindFramebuffer(GL_FRAMEBUFFER, fboId);//��FBO�����������еĶ���д֡����Ĳ�������Ӱ�쵽��ǰ�󶨵�֡���塣

		
		/**��ҪΪ֡���崴��һЩ����(Attachment)������Ҫ����Щ�������ӵ�֡������*/
		//����������Ⱦ��������Ⱦ����ǰ�󶨵�֡����ĸ��ӻ����У�
//=====================================================================================	
		/**������֡������������󡿣�ʹ������ĺô��ǣ�������Ⱦ�����Ľ�����ᱻ����Ϊһ������ͼ���������ǾͿ��Լ򵥵�����ɫ����ʹ���ˡ�*/
		// Create a color attachment texture
		 glGenTextures(1, temp, 0);
		 fboTex = temp[0];
		 //Bind texture
		 glBindTexture(GL_TEXTURE_2D, fboTex);
		//Define texture parameters
		 glTexImage2D( GL_TEXTURE_2D, 0,  GL_RGBA, fboWidth, fboHeight, 0,  GL_RGBA,  GL_UNSIGNED_BYTE, null);//���������������������Դ
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S,  GL_CLAMP_TO_EDGE);
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T,  GL_CLAMP_TO_EDGE);
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MAG_FILTER,  GL_LINEAR);//�������
		 glTexParameteri( GL_TEXTURE_2D,  GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
		 
		 /**��������� ���ӵ� FBO����ɫ���ŵ�*/
		 //Attach texture FBO color attachment, Attach it to currently bound framebuffer object
//		 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fboTex, 0);//������FBO
			/* glFramebufferTexture2D(target, attachment, textarget, texture, level);
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


			 
			
			 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fboTex, 0);//������FBO
			//Attaching Renderbuffer Images to Framebuffer
			 glFramebufferRenderbuffer( GL_FRAMEBUFFER,  GL_DEPTH_ATTACHMENT,  GL_RENDERBUFFER, renderBufferId);//Renderbuffer����FBO

			 //Ȼ����֡�����������ԣ�����������Ļ���ʾ����
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
