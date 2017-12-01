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

	/*从网络获取图片，数据为InputStream流对象，然后调用BitmapFactory的decodeStream方法解码获取图片。*/
	private Bitmap getUrlBitmap(String url){
		Bitmap bm;
		try{
			URL imageUrl=new URL(url);
			HttpURLConnection conn=(HttpURLConnection)imageUrl.openConnection();
			conn.connect();
			InputStream is=conn.getInputStream();
			//注释部分换用另外一种方式解码
			//byte[] bt=getBytes(is);
			//bm=BitmapFactory.decodeByteArray(bt,0,bt.length);
			bm=BitmapFactory.decodeStream(is); //如果采用这种解码方式在低版本的API上会出现解码问题
			
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
     * 保存文件
     * 
     * @param bm
     *            位图
     * @param fileName
     *            文件名
     * @param modifyTime
     *            修改时间
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName, String modifyTime)
            throws IOException {
        File myCaptureFile = new File(ALBUM_PATH + fileName);// 创建文件
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

    }
}
