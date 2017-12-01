
package com.niming.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;


public class FaceUtil {
	public final static int REQUEST_PICTURE_CHOOSE = 1;
	public final static int  REQUEST_CAMERA_IMAGE = 2;
	public final static int REQUEST_CROP_IMAGE = 3;
	private static final int IO_BUFFER_SIZE = 500;
	
	/***
	 * �ü�ͼƬ
	 * @param activity Activity
	 * @param uri ͼƬ��Uri
	 */
	public static void cropPicture(Activity activity, Uri uri) {
//		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		Intent innerIntent = new Intent();
		innerIntent.setDataAndType(uri, "image/*");
//		innerIntent.putExtra("crop", "true");// ����ü����ܣ�true���ܳ�������С���򣬲�Ȼû�м������ܣ�ֻ��ѡȡͼƬ
//		innerIntent.putExtra("aspectX", 1); // �Ŵ���С������X
//		innerIntent.putExtra("aspectY", 1);// �Ŵ���С������X   ����ı���Ϊ��   1:1
//		innerIntent.putExtra("outputX", 320);  //������������ͼƬ��С
//		innerIntent.putExtra("outputY", 320);  //���ص�ʱ�� Y �����ش�С��
//		innerIntent.putExtra("noFaceDetection", true); // �Ƿ�ȥ���沿��⣬ �������Ҫ�ض��ı���ȥ�ü�ͼƬ����ô���һ��Ҫȥ������Ϊ�����ƻ����ض��ı�����
		innerIntent.putExtra("return-data", true);//�Ƿ�Ҫ����ֵ�� һ�㶼Ҫ��
		// ��ͼ��С����������޺ڿ�
		innerIntent.putExtra("scale", true);
		innerIntent.putExtra("scaleUpIfNeeded", true);
		File imageFile = new File(getImagePath(activity.getApplicationContext()));
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		activity.startActivityForResult(innerIntent, REQUEST_CROP_IMAGE);
	}
	
	/**
	 * ����ü���ͼƬ��·��
	 * @return
	 */
	public static String getImagePath(Context context){
		String path;
		
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = context.getFilesDir().getAbsolutePath();
		} else {
			path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/";
		}
		
		if(!path.endsWith("/")) {
			path += "/";
		}
		
		File folder = new File(path);
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		path += "ifd.jpg";
		return path;
	}
	
	/**
	 * ��ȡͼƬ���ԣ���ת�ĽǶ�
	 * 
	 * @param path ͼƬ����·��
	 * @return degree ��ת�Ƕ�
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * ��תͼƬ
	 * 
	 * @param angle	��ת�Ƕ�
	 * @param bitmap ԭͼ
	 * @return bitmap ��ת���ͼƬ
	 */
	public static Bitmap rotateImage(int angle, Bitmap bitmap) {
		// ͼƬ��ת����
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// �õ���ת���ͼƬ
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * ��ָ�������Ͻ����������
	 * 
	 * @param canvas �����Ļ���
	 * @param face ��Ҫ���Ƶ�������Ϣ
	 * @param width ԭͼ��
	 * @param height ԭͼ��
	 * @param frontCamera �Ƿ�Ϊǰ������ͷ����Ϊǰ������ͷ�����ҶԳ�
	 * @param DrawOriRect �ɻ���ԭʼ��Ҳ����ֻ���ĸ���
	 */
	static public void drawFaceRect(Canvas canvas, FaceRect face, int width, int height, boolean frontCamera, boolean DrawOriRect) {
		if(canvas == null) {
			return;
		}

		Paint paint = new Paint(); 
		paint.setColor(Color.rgb(255, 203, 15));
		int len = (face.bound.bottom - face.bound.top) / 8;
		if (len / 8 >= 2) paint.setStrokeWidth(len / 8);
		else paint.setStrokeWidth(2);
		
		Rect rect = face.bound;

		if(frontCamera) {
			int top = rect.top;
			rect.top = width - rect.bottom;
			rect.bottom = width - top;
		}

		if (DrawOriRect) {
			paint.setStyle(Style.STROKE);
			canvas.drawRect(rect, paint);
		} else {
			int drawl = rect.left	- len;
			int drawr = rect.right	+ len;
			int drawu = rect.top 	- len;
			int drawd = rect.bottom	+ len;
			
			canvas.drawLine(drawl,drawd,drawl,drawd-len, paint);
			canvas.drawLine(drawl,drawd,drawl+len,drawd, paint);
			canvas.drawLine(drawr,drawd,drawr,drawd-len, paint);
			canvas.drawLine(drawr,drawd,drawr-len,drawd, paint);
			canvas.drawLine(drawl,drawu,drawl,drawu+len, paint);
			canvas.drawLine(drawl,drawu,drawl+len,drawu, paint);
			canvas.drawLine(drawr,drawu,drawr,drawu+len, paint);
			canvas.drawLine(drawr,drawu,drawr-len,drawu, paint);
		}
		
		if (face.point != null) {
			for (Point p : face.point) 
			{
				if(frontCamera) {
					p.y = width - p.y;
				}
				canvas.drawPoint(p.x, p.y, paint);
			}
		}
	}

	/**
	 * ��������ԭͼ˳ʱ����ת90��
	 * 
	 * @param r
	 * ����ת�ľ���
	 * 
	 * @param width
	 * ������ζ�Ӧ��ԭͼ��
	 * 
	 * @param height
	 * ������ζ�Ӧ��ԭͼ��
	 * 
	 * @return
	 * ��ת��ľ���
	 */
	static public Rect RotateDeg90(Rect r, int width, int height) {
		int left = r.left;
		r.left	= height- r.bottom;
		r.bottom= r.right;
		r.right	= height- r.top;
		r.top	= left;
		return r;
	}
	
	/**
	 * ������ԭͼ˳ʱ����ת90��
	 * @param p
	 * ����ת�ĵ�
	 * 
	 * @param width
	 * ������Ӧ��ԭͼ��
	 * 
	 * @param height
	 * ������Ӧ��ԭͼ��
	 * 
	 * @return
	 * ��ת��ĵ� 
	 */
	static public Point RotateDeg90(Point p, int width, int height) {
		int x = p.x;
		p.x = height - p.y;
		p.y = x;
		return p;
	}
	
	public static int getNumCores() {
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }
	    try {
	        File dir = new File("/sys/devices/system/cpu/");
	        File[] files = dir.listFiles(new CpuFilter());
	        return files.length;
	    } catch(Exception e) {
	        e.printStackTrace();
	        return 1;
	    }
	}
	
	/**
	 * ����Bitmap������
	 * @param Bitmap
	 */
	public static void saveBitmapToFile(Context context,Bitmap bmp){
		String file_path = getImagePath(context);
		File file = new File(file_path);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	  * �õ����ػ��������ϵ�bitmap url - ������߱���ͼƬ�ľ���·��,����: 
	  * 
	  * A.����·��: url="http://blog.foreverlove.us/girl2.png" ; 
	  * 
	  * B.����·��:url="file://mnt/sdcard/photo/image.png"; 
	  * 
	  * C.֧�ֵ�ͼƬ��ʽ ,png, jpg,bmp,gif�ȵ� 
	  * 
	  * @param url 
	  * @return 
	  */
	 public static Bitmap getBitmap2(String url) 
	 { 
		  Bitmap bitmap = null; 
		  InputStream in = null; 
		  BufferedOutputStream out = null; 
		  try
		  { 
			   in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE); 
			   final ByteArrayOutputStream dataStream = new ByteArrayOutputStream(); 
			   out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE); 
			   copy(in, out); 
			   out.flush(); 
			   byte[] data = dataStream.toByteArray(); 
			   bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); 
			   data = null; 
			   return bitmap; 
		  } 
		  catch (IOException e) 
		  { 
			   e.printStackTrace(); 
			   return null; 
		  } 
	 }
	 
	 public static Bitmap getImageFromNet(String url) {  
	        HttpURLConnection conn = null;  
	        try {  
	            URL mURL = new URL(url);  
	            conn = (HttpURLConnection) mURL.openConnection();  
	            conn.setRequestMethod("GET"); //�������󷽷�  
	            conn.setConnectTimeout(10000); //�������ӷ�������ʱʱ��  
	            conn.setReadTimeout(5000);  //���ö�ȡ���ݳ�ʱʱ��  
	  
	            conn.connect(); //��ʼ����  
	  
	            int responseCode = conn.getResponseCode(); //�õ�����������Ӧ��  
	            if (responseCode == 200) {  
	                //���ʳɹ�  
	                InputStream is = conn.getInputStream(); //��÷��������ص�������  
	                Bitmap bitmap = BitmapFactory.decodeStream(is); //���������� ����һ��bitmap����  
	                return bitmap;  
	  
	            } else {  
	                //����ʧ��  
	                Log.d("lyf--", "����ʧ��===responseCode��" + responseCode);  
	            }  
	  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            if (conn != null) {  
	                conn.disconnect(); //�Ͽ�����  
	            }  
	        }  
	        return null;  
	    }  
	 /**
	  * from InputStream to OutputStream
	  * @param in
	  * @param out
	 * @throws IOException 
	  */
	private static void copy (InputStream in, OutputStream out) throws IOException {
		 byte[] buf = new byte[1024 * 8];
         while (true) {
             int read = 0;
             if (in != null) {
                 read = in.read(buf);
             }
             System.out.println(read);
             if (read == -1) {
                 break;
             }
             out.write(buf, 0, read);
         }
         out.flush();
	}
	
	/**
	 * Try to return the absolute file path from the given Uri
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath( final Context context, final Uri uri ) {
	    if ( null == uri ) return null;
	    final String scheme = uri.getScheme();
	    String path = null;
	    if ( scheme == null )
	        path = uri.getPath();
	    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
	        path = uri.getPath();
	    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
	        Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
	        if ( null != cursor ) {
	            if ( cursor.moveToFirst() ) {
	                int index = cursor.getColumnIndex( ImageColumns.DATA );
	                if ( index > -1 ) {
	                    path = cursor.getString( index );
	                }
	            }
	            cursor.close();
	        }
	    }
	    return path;
	}
}
