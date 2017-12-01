package com.niming.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class xxxx {
	private static final String ALBUM_PATH = "sdcard/picture";

	/*�������ȡͼƬ������ΪInputStream������Ȼ�����BitmapFactory��decodeStream���������ȡͼƬ��*/
	private Bitmap getUrlBitmap(String url){
		Bitmap bm;
		try{
			URL imageUrl=new URL(url);
			HttpURLConnection conn=(HttpURLConnection)imageUrl.openConnection();
			conn.connect();
			InputStream is=conn.getInputStream();
			//ע�Ͳ��ֻ�������һ�ַ�ʽ����
			//byte[] bt=getBytes(is);
			//bm=BitmapFactory.decodeByteArray(bt,0,bt.length);
			bm=BitmapFactory.decodeStream(is); //����������ֽ��뷽ʽ�ڵͰ汾��API�ϻ���ֽ�������
			
			is.close();
			conn.disconnect();
			return bm;
		}catch(MalformedURLException e)
		{
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		return null;
			
	}
	
	/**
     * �����ļ�
     * 
     * @param bm
     *            λͼ
     * @param fileName
     *            �ļ���
     * @param modifyTime
     *            �޸�ʱ��
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName, String modifyTime)
            throws IOException {
        File myCaptureFile = new File(ALBUM_PATH + fileName);// �����ļ�
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

    }
}
