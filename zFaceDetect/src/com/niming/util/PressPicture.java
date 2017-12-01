package com.niming.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class PressPicture {
	/**
     * ͨ��uri��ȡͼƬ������ѹ��
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(ContentResolver cr, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //ͼƬ�ֱ�����480x800Ϊ��׼
        float hh = 800f;//�������ø߶�Ϊ800f
        float ww = 480f;//�������ÿ��Ϊ480f
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;//be=1��ʾ������
        if (originalWidth > originalHeight && originalWidth > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //����ѹ��
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//�������ű���
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = cr.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
 
        return compressImage(bitmap);//�ٽ�������ѹ��
    }
		/**
	    * ����ѹ������
	    *
	    * @param image
	    * @return
	    */
	   public static Bitmap compressImage(Bitmap image) {
	 
	       ByteArrayOutputStream baos = new ByteArrayOutputStream();
	       image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
	       int options = 100;
	       while (baos.toByteArray().length / 1024 > 100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
	           baos.reset();//����baos�����baos
	           //��һ������ ��ͼƬ��ʽ ���ڶ��������� ͼƬ������100Ϊ��ߣ�0Ϊ���  ������������������ѹ��������ݵ���
	           image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
	           options -= 10;//ÿ�ζ�����10
	       }
	       ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
	       Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
	       return bitmap;
	   }
}
