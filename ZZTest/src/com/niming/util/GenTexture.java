package com.niming.util;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameterf;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;


public class GenTexture {
	private static final String TAG = "Generate Texture";

	/**
	 * loadTexture()����������Դ�ļ��ж���ͼ���ļ�resourceID������ͼ�����ݼ��ؽ�OpenGL����ȡ��һ������ID�����ʧ�ܣ�����0.
	 * @return textureObjectIds
	 */
	public static int loadTexture(Resources resources, int resourceId, boolean generateMipMap){
		
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);//���ɡ��������󡿣�OpenGL������ɵ�ID�洢��textureObjectIds�С�
		/*������������Ƿ�ɹ�����*/
		if(textureObjectIds[0] == 0){
			Log.w(TAG, "Could not generate a new OpenGL texture object.");
			return 0;
		}
		
		/*����λͼ���ݲ���������*/
		final BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inScaled = false;//����Android������Ҫԭʼ��ͼ�����ݣ����������ͼ������Ű汾��
		
		final Bitmap bitmap = BitmapFactory.decodeResource(
				resources, resourceId, options);//ʵ�ʵĽ��빤����������û�ѽ�����ͼ�����OpenGL
		if(bitmap == null){
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		//����OpenGL������������Ӧ��Ӧ���������������
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		
		//�����������˲���
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);/*��С*/
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);/*�Ŵ�*/
		
		/**7.2.4 ����������OpenGL��������ID*/
		
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);/*�ú������ø���OpenGL����bitmap�����λͼ���ݣ����������Ƶ���ǰbind��������*/
		
		/*��Ȼ��Щ�����Ѿ������ؽ�OpenGL�����ǾͲ�����Ҫ����Android��λͼ*/
		//�����ͷ�ͼƬ����
		bitmap.recycle();
		
		if(generateMipMap)
			glGenerateMipmap(GL_TEXTURE_2D);/*����MIP��ͼ*/
		glBindTexture(GL_TEXTURE_2D, 0);/*����0Ϊ����뵱ǰ�����󶨣���ֹ��������ı�����*/
		
		return textureObjectIds[0];
	}
}