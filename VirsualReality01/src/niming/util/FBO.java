package niming.util;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;

import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

/** 
 * ����һ��������֡���������������������
1�����Ǳ����������������һ����������ɫ����ȡ�ģ�建�壩��
2������������һ������ɫ������
3�����еĸ�����Ӧ�����Ѿ���ȫ���õģ��Ѿ��洢���ڴ�֮�У���
4��ÿ�����嶼Ӧ����ͬ����Ŀ��������
 */
public class FBO {
	public int fboid, fbotex, fborender;
	public IntBuffer framebuffer = IntBuffer.allocate(1);
	public IntBuffer depthRenderbuffer = IntBuffer.allocate(1);
	public IntBuffer texture = IntBuffer.allocate(1);
	public FBO(int texWidth, int texHeight){
	       
        // generate the framebuffer, renderbuffer, and texture object names
        GLES20.glGenFramebuffers(1, framebuffer);
        GLES20.glGenRenderbuffers(1, depthRenderbuffer);
        GLES20.glGenTextures(1, texture);
        
        fboid = framebuffer.get(0);
        fbotex = texture.get(0);
        fborender = depthRenderbuffer.get(0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboid);
        // bind texture and load the texture mip-level 0 texels are RGB565
        // no texels need to be specified as we are going to draw into the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texWidth, texHeight,
        		0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        //Attach texture FBO color attachment, Attach it to currently bound framebuffer object
		 glFramebufferTexture2D( GL_FRAMEBUFFER,  GL_COLOR_ATTACHMENT0,  GL_TEXTURE_2D, fbotex, 0);//������FBO
		 
		 
        // bind renderbuffer and create a 16-bit depth buffer
        // width and height of renderbuffer = width and height of the texture
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbuffer.get(0));
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, texWidth, texHeight);
        //Attaching Renderbuffer Images to Framebuffer
		glFramebufferRenderbuffer( GL_FRAMEBUFFER,  GL_DEPTH_ATTACHMENT,  GL_RENDERBUFFER, fborender);//Renderbuffer����FBO

		//Ȼ����֡�������������ԡ�������������Ļ���ʾ����
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
			Log.v("GenFBO", "ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
		}
//		glBindTexture( GL_TEXTURE_2D, 0);
//		glBindRenderbuffer( GL_RENDERBUFFER, 0);
		 //breaks the existing binding of a framebuffer object to the target
//		glBindFramebuffer( GL_FRAMEBUFFER, 0);//ͨ����Ϊ0��ʹ��Ĭ��֡���塿������
	}
}
