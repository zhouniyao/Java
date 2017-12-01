package com.niming;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.niming.util.FaceRect;
import com.niming.zfacedetect.R;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.media.FaceDetector;
import android.media.MediaScannerConnection;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.sax.EndElementListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class DynamicDetect  extends Activity{
    private static final String TAG = "niming";
    private final int VIBRATE_TIME = 60;  
	
	private final int maxFaces = 50;
	private Bitmap mImage = null;
	private Bitmap tietu = null;
	private Toast mToast;
	private File mPictureFile;
	private int distance = 0;
	//FaceDetector 对象，集成了人脸识别。
	private FaceDetector mFaceDetector;
	//人脸识别结果
	private FaceRect[] mFaces;
	private FaceDetector.Face[] myFaces; //存储多张人脸的数组变量  
    float myEyesDistance;           //两眼之间的距离  
    int numberOfFaceDetected;       //实际检测到的人脸数  
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		ImageView imageView = (ImageView) findViewById(R.id.dynamic_img);//展示图片
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 设置相机拍照后照片保存路径
				mPictureFile = new File(Environment.getExternalStorageDirectory(), 
						"picture" + System.currentTimeMillis()/1000 + ".jpg");
				// 启动拍照,并保存到临时文件
				Intent mIntent = new Intent();
				mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
				mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				startActivityForResult(mIntent, 3);
			}
		});

		
		findViewById(R.id.rect2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 tietu = BitmapFactory.decodeResource(getResources(), R.drawable.rect) ;  
			     vibrate();distance = (int)(tietu.getWidth()/2);
			     detectDraw();
			}
		});//选框
		findViewById(R.id.hat2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        tietu = BitmapFactory.decodeResource(getResources(), R.drawable.hat) ;  
		        vibrate();distance = (int)(tietu.getWidth()/2);
		        detectDraw();
			}
		});//官帽
		findViewById(R.id.pricess2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.princess);
				vibrate();distance = (int)(tietu.getWidth()/2);
				detectDraw();
			}
		});//公主
		findViewById(R.id.zhenhuan2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.zhenhuan2) ;  
				vibrate();distance = (int)(tietu.getWidth()/2);
				detectDraw();
			}
		});//甄嬛
		findViewById(R.id.keai2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.keai2) ;  
				vibrate();distance = (int)(tietu.getWidth()/2);
				detectDraw();
			}
		});//可爱
		
	}
	/**
	 * 找人脸，然后绘制
	 */
	void detectDraw(){
		/*创建FaceDetector*/
	    int imageWidth = mImage.getWidth();   
		int imageHeight = mImage.getHeight();   
		myFaces = new FaceDetector.Face[maxFaces];       //分配人脸数组空间  
		mFaceDetector = new FaceDetector(imageWidth, imageHeight, maxFaces);   
		numberOfFaceDetected = mFaceDetector.findFaces(mImage, myFaces);    //FaceDetector 构造实例并解析人脸  
		if(numberOfFaceDetected == 0){
			showInfo("找不到人脸");
		}
	    drawFaceRects(myFaces);
	}
	/**
	 * 设置震动
	 */
	void vibrate(){
//		vibrator.vibrate(new long[]{0,1000}, -1);//调节震动频率，时间1秒
		vibrator.vibrate(VIBRATE_TIME);
	}


	
	private void drawFaceRects(Face[] faces) {
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);
		paint.setStyle(Style.STROKE);
		//新建Bitmap
		Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
				mImage.getHeight(), Config.ARGB_8888);
		//创建canvas对象
		Canvas canvas = new Canvas(bitmap);//设定画布及大小大小
		canvas.drawBitmap(mImage, new Matrix(), null);//先画整幅图
		paint.setColor(Color.RED);
		paint.setStrokeWidth(20);//设置画笔粗细
		
		for (int i=0; i < numberOfFaceDetected; i++) {
            
			
			FaceDetector.Face face = myFaces[i];
			  PointF myMidPoint = new PointF();   //一个包含二维坐标(x, y)的类
	            face.getMidPoint(myMidPoint);   
	            myEyesDistance = face.eyesDistance();   //得到人脸中心点和眼间距离参数，并对每个人脸进行画框  
	            
	            Log.v(TAG, "视距：" + myEyesDistance);
	            Log.v(TAG, "人脸中心"+ myMidPoint.x +"  " + myMidPoint.y);
	            if( tietu == null){
	                return;  
	            }
	            canvas.save();//隔离当前canvas配置
	            float ratio = 0.0125f * myEyesDistance; //0.0125为经验值
	            canvas.scale(ratio, ratio, myMidPoint.x, myMidPoint.y);//贴图缩放比例，按轴中心缩放
	            
	            
	            canvas.drawBitmap(tietu, (int)(myMidPoint.x - distance), (int)(myMidPoint.y - distance),null);  //贴图，初始坐标（0， 0）相对于整个画布而言
	            canvas.restore();//恢复之前canvas配置
		}
		
		((ImageView) findViewById(R.id.dynamic_img)).setImageBitmap(bitmap);//将图像显示在当前的ImageView控件中
	}



	/**
	 *导入的图片成功，人脸无法坚持
	 */
	public static final Bitmap getBitmap(ContentResolver cr, Uri uri)
	        throws FileNotFoundException, IOException {
	    InputStream input = cr.openInputStream(uri);
	    Bitmap bitmap = BitmapFactory.decodeStream(input);
	    input.close();
	    return bitmap;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//这是个回调函数
		if (resultCode != RESULT_OK) {						//第一个if 判断是否返回成功
			return;
		}
		
		String fileSrc = null;
		/*相机回调*/
		if (requestCode == 3) {//第二个if  判断requestCode和上面 startActivity(),里的参数相同,就获取图片
			
			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			
			
//		  	 Uri uri = data.getData();  //获取图片Path
//	         Log.d(TAG, "URI:"+ uri.getPath());//错误路径
//	         ContentResolver cr = this.getContentResolver();  
//	         String[] proj = {MediaStore.Images.Media.DATA};
//	         @SuppressWarnings("deprecation")
//			 Cursor cursor = managedQuery(uri, proj, null, null, null);
//	         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//按我个人理解 这个是获得用户选择的图片的索引值
//	         cursor.moveToFirst();//将光标移至开头 ，这个很重要，不小心很容易引起越界
//	         //最后根据索引值获取图片路径
//	         String imagePath = cursor.getString(column_index);
			 String imagePath = fileSrc;

	         Log.d(TAG, "path：  "+ imagePath);//路径正确
	         Toast.makeText(getApplicationContext(), "图片导入成功", Toast.LENGTH_SHORT).show();
            try {  
				
            	/*怎么从图库 导入 bitmap*/
			    BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
		        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //构造位图生成的参数，必须为565。类名+enum  
		        BitmapFactoryOptionsbfo.inPurgeable = true;
		        BitmapFactoryOptionsbfo.inInputShareable = true;
		        mImage = BitmapFactory.decodeFile(imagePath, BitmapFactoryOptionsbfo); //此时返回mImage为空
		        
		        Log.d(TAG, "Bitmap: " + mImage);
    			/*图片太大，Imageview不显示*/
			        DisplayMetrics dm = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(dm);
			        int screenWidth=dm.widthPixels;
			        if(mImage.getWidth() <= screenWidth){
			        	((ImageView) findViewById(R.id.dynamic_img)).setImageBitmap(mImage);
			        }else{
//			        	Bitmap bmp = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight()*screenWidth/mImage.getWidth(), true);
//			        	((ImageView) findViewById(R.id.static_img)).setImageBitmap(bmp);
			        	mImage = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight()*screenWidth/mImage.getWidth(), true);
			        	((ImageView) findViewById(R.id.dynamic_img)).setImageBitmap(mImage);
			        }
    			
			} catch (Exception e) {  
			    // TODO Auto-generated catch block  
			    e.printStackTrace();  
			}  
            
		}//End if
	}
	private void updateGallery(String filename) {
		MediaScannerConnection.scanFile(this, new String[] {filename}, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					
					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void showInfo(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	protected void onStop() {
		if(null!=vibrator){
			vibrator.cancel();
		}
		super.onStop();
	}
	public void copyFile(String oldPath, String newPath) {   
	       try {   
	           int bytesum = 0;   
	           int byteread = 0;   
	           File oldfile = new File(oldPath);   
	           if (!oldfile.exists()) { //文件不存在时   
	               InputStream inStream = new FileInputStream(oldPath); //读入原文件   
	               FileOutputStream fs = new FileOutputStream(newPath);   
	               byte[] buffer = new byte[1024];   
	               int length;   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   bytesum += byteread; //字节数 文件大小   
	                   System.out.println(bytesum);   
	                   fs.write(buffer, 0, byteread);   
	               }   
	               inStream.close();   
	           }   
	       }   
	       catch (Exception e) {   
	           System.out.println("复制单个文件操作出错");   
	           e.printStackTrace();   

	       }   

	   }

	


}
