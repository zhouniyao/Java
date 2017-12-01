package niming.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import static android.opengl.GLES20.*;
import android.opengl.GLUtils;

/**
 * ����ӳ�䣬������ؽ�OpenGL �������裺
 * 1�������������Ϊ��ָ��һ������ͼƬ
 * 2����������ӳ��ģʽ�����˷�ʽ�Ȳ���
 * 3�������������겢Ϊ��ά�����е�ÿ������ָ��һ����������
 * 4����������ӳ�书��
 * 5�����������
 * 6�������������״̬
 * 7��ָ�������������ݻ���
 * 8��ͼ����Ⱦ����������
 * 9���ر��������״̬
 */
public class TextureHelper {
	private static final String TAG = "TextureHelper";
	
	/**
	 * loadTexture()����������Դ�ļ��ж���ͼ���ļ�������ͼ�����ݼ��ؽ�OpenGL����ȡ��һ������ID�����ʧ�ܣ�����0.
	 * @return textureObjectIds
	 */
	public static int loadTexture(Context context, int resourceId){
//		final int[] textureObjectIds = new int[1];
//		glGenTextures(1, textureObjectIds, 0);//���ɡ�������󡿣�OpenGL������ɵ�ID�洢��textureObjectIds�С�
//		/*�����������Ƿ�ɹ�����*/
//		if(textureObjectIds[0] == 0){
//			if(LoggerConfig.ON)
//				Log.w(TAG, "Could not generate a new OpenGL texture object.");
//			return 0;
//		}
//		
//		/*����λͼ���ݲ��������*/
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		
//		options.inScaled = false;//����Android������Ҫԭʼ��ͼ�����ݣ����������ͼ������Ű汾��
//		
//		final Bitmap bitmap = BitmapFactory.decodeResource(
//				context.getResources(), resourceId, options);//ʵ�ʵĽ��빤����������û�ѽ�����ͼ�����OpenGL
//		if(bitmap == null){
//			if(LoggerConfig.ON){
//				Log.w(TAG, "Resource ID" + resourceId + "Could not be decode.");
//			}
//			glDeleteTextures(1, textureObjectIds, 0);
//			return 0;
//		}
//		
//		//����OpenGL�����������Ӧ��Ӧ��������������
//		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
//		
//		//����������˲���
//		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);/*��С*/
//		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);/*�Ŵ�*/
//		
//		/**7.2.4 ��������OpenGL��������ID*/
//		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);/*�ú������ø���OpenGL����bitmap�����λͼ���ݣ����������Ƶ���ǰbind�������*/
//		
//		/*��Ȼ��Щ�����Ѿ������ؽ�OpenGL�����ǾͲ�����Ҫ����Android��λͼ*/
//		//�����ͷ�ͼƬ����
//		bitmap.recycle();
//		
//		glGenerateMipmap(GL_TEXTURE_2D);/*����MIP��ͼ*/
//		glBindTexture(GL_TEXTURE_2D, 0);/*����0Ϊ����뵱ǰ����󶨣���ֹ��������ı�����*/
		boolean generateMipMap = false;
		return loadTexture(context, resourceId, generateMipMap);
	}
	
	
	public static int loadTexture(Context context, int resourceId, boolean generateMipMap){
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);//���ɡ�������󡿣�OpenGL������ɵ�ID�洢��textureObjectIds�С�
		/*�����������Ƿ�ɹ�����*/
		if(textureObjectIds[0] == 0){
			if(LoggerConfig.ON)
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			return 0;
		}
		
		/*����λͼ���ݲ��������*/
		final BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inScaled = false;//����Android������Ҫԭʼ��ͼ�����ݣ����������ͼ������Ű汾��
		
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceId, options);//ʵ�ʵĽ��빤����������û�ѽ�����ͼ�����OpenGL
		if(bitmap == null){
			if(LoggerConfig.ON){
				Log.w(TAG, "Resource ID" + resourceId + "Could not be decode.");
			}
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		//����OpenGL�����������Ӧ��Ӧ��������������
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		
		//����������˲���
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);/*��С*/
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);/*�Ŵ�*/
		
		/**7.2.4 ��������OpenGL��������ID*/
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);/*�ú������ø���OpenGL����bitmap�����λͼ���ݣ����������Ƶ���ǰbind�������*/
		
		/*��Ȼ��Щ�����Ѿ������ؽ�OpenGL�����ǾͲ�����Ҫ����Android��λͼ*/
		//�����ͷ�ͼƬ����
		bitmap.recycle();
		if(generateMipMap){
			glGenerateMipmap(GL_TEXTURE_2D);/*����MIP��ͼ*/
		}
		glBindTexture(GL_TEXTURE_2D, 0);/*����0Ϊ����뵱ǰ����󶨣���ֹ��������ı�����*/
		
		return textureObjectIds[0];
	}
}
