
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
	 * 裁剪图片
	 * @param activity Activity
	 * @param uri 图片的Uri
	 */
	public static void cropPicture(Activity activity, Uri uri) {
//		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		Intent innerIntent = new Intent();
		innerIntent.setDataAndType(uri, "image/*");
//		innerIntent.putExtra("crop", "true");// 激活裁剪功能，true才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
//		innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
//		innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X   这里的比例为：   1:1
//		innerIntent.putExtra("outputX", 320);  //这个是限制输出图片大小
//		innerIntent.putExtra("outputY", 320);  //返回的时候 Y 的像素大小。
//		innerIntent.putExtra("noFaceDetection", true); // 是否去除面部检测， 如果你需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例。
		innerIntent.putExtra("return-data", true);//是否要返回值。 一般都要。
		// 切图大小不足输出，无黑框
		innerIntent.putExtra("scale", true);
		innerIntent.putExtra("scaleUpIfNeeded", true);
		File imageFile = new File(getImagePath(activity.getApplicationContext()));
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		activity.startActivityForResult(innerIntent, REQUEST_CROP_IMAGE);
	}
	
	/**
	 * 保存裁剪的图片的路径
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
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path 图片绝对路径
	 * @return degree 旋转角度
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
	 * 旋转图片
	 * 
	 * @param angle	旋转角度
	 * @param bitmap 原图
	 * @return bitmap 旋转后的图片
	 */
	public static Bitmap rotateImage(int angle, Bitmap bitmap) {
		// 图片旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 得到旋转后的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 在指定画布上将人脸框出来
	 * 
	 * @param canvas 给定的画布
	 * @param face 需要绘制的人脸信息
	 * @param width 原图宽
	 * @param height 原图高
	 * @param frontCamera 是否为前置摄像头，如为前置摄像头需左右对称
	 * @param DrawOriRect 可绘制原始框，也可以只画四个角
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
	 * 将矩形随原图顺时针旋转90度
	 * 
	 * @param r
	 * 待旋转的矩形
	 * 
	 * @param width
	 * 输入矩形对应的原图宽
	 * 
	 * @param height
	 * 输入矩形对应的原图高
	 * 
	 * @return
	 * 旋转后的矩形
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
	 * 将点随原图顺时针旋转90度
	 * @param p
	 * 待旋转的点
	 * 
	 * @param width
	 * 输入点对应的原图宽
	 * 
	 * @param height
	 * 输入点对应的原图宽
	 * 
	 * @return
	 * 旋转后的点 
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
	 * 保存Bitmap至本地
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
	  * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如: 
	  * 
	  * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ; 
	  * 
	  * B.本地路径:url="file://mnt/sdcard/photo/image.png"; 
	  * 
	  * C.支持的图片格式 ,png, jpg,bmp,gif等等 
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
	            conn.setRequestMethod("GET"); //设置请求方法  
	            conn.setConnectTimeout(10000); //设置连接服务器超时时间  
	            conn.setReadTimeout(5000);  //设置读取数据超时时间  
	  
	            conn.connect(); //开始连接  
	  
	            int responseCode = conn.getResponseCode(); //得到服务器的响应码  
	            if (responseCode == 200) {  
	                //访问成功  
	                InputStream is = conn.getInputStream(); //获得服务器返回的流数据  
	                Bitmap bitmap = BitmapFactory.decodeStream(is); //根据流数据 创建一个bitmap对象  
	                return bitmap;  
	  
	            } else {  
	                //访问失败  
	                Log.d("lyf--", "访问失败===responseCode：" + responseCode);  
	            }  
	  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            if (conn != null) {  
	                conn.disconnect(); //断开连接  
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
