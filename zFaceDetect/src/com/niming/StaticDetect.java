package com.niming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.niming.util.FaceRect;
import com.niming.util.FaceUtil;
import com.niming.util.ParseResult;
import com.niming.util.PressPicture;
import com.niming.util.SoundUtil;
import com.niming.zfacedetect.R;


import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 相片贴图
 */
public class StaticDetect extends Activity implements OnClickListener{
	private static final String TAG = "niming";
    private final String newpath = "com.niming:raw/temp";
    private final int VIBRATE_TIME = 60;  
	
	private final int maxFaces = 5;
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
	public Vibrator vibrator;
	private SoundPool soundPool; //声音
	int scrId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_static);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);//容纳1个声音
		scrId = soundPool.load(StaticDetect.this, R.raw.nopicture, 1);
		
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		ImageView imageView = (ImageView) findViewById(R.id.static_img);//展示图片
		Button attachButton = (Button) findViewById(R.id.static_detect);//智能贴图按钮
		
		imageView.setOnClickListener (StaticDetect.this);
		attachButton.setOnClickListener(StaticDetect.this);
		
		findViewById(R.id.rect).setOnClickListener(StaticDetect.this);//选框
		findViewById(R.id.hat).setOnClickListener(StaticDetect.this);//官帽
		findViewById(R.id.pricess).setOnClickListener(StaticDetect.this);//公主
		findViewById(R.id.zhenhuan).setOnClickListener(StaticDetect.this);//甄嬛
		findViewById(R.id.zhichang).setOnClickListener(StaticDetect.this);//制服
		
	}

	void vibrate(){
//		vibrator.vibrate(new long[]{0,1000}, -1);//调节震动频率，时间1秒
		vibrator.vibrate(VIBRATE_TIME);
	}

	@Override
	public void onClick(View v) {
//		if(mFaceDetector == null){
//			Log.v(TAG, "创建FaceDetector失败");
//			return;
//		}
		switch (v.getId()) {
		case R.id.static_img:
			/*点击后调转图库*/
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(intent, 1);
			break;
		case R.id.rect:
	        tietu = BitmapFactory.decodeResource(getResources(), R.drawable.rect) ;  
	        vibrate();distance = (int)(tietu.getWidth()/2);
			break;
		case R.id.hat://选择hat
			 //将hat图像转换为Bitmap对象  
	        tietu = BitmapFactory.decodeResource(getResources(), R.drawable.hat) ;  
	        vibrate();distance = (int)(tietu.getWidth()/2);
			break;
			
		case R.id.zhenhuan:
			tietu = BitmapFactory.decodeResource(getResources(), R.drawable.zhenhuan2) ;  
			vibrate();distance = (int)(tietu.getWidth()/2);
			break;
		case R.id.zhichang:
			tietu = BitmapFactory.decodeResource(getResources(), R.drawable.keai2) ;  
			vibrate();distance = (int)(tietu.getWidth()/2);
			break;
		case R.id.pricess://选择dog
			tietu = BitmapFactory.decodeResource(getResources(), R.drawable.princess);
			vibrate();distance = (int)(tietu.getWidth()/2);
//			// 设置相机拍照后照片保存路径
//			mPictureFile = new File(Environment.getExternalStorageDirectory(), 
//					"picture" + System.currentTimeMillis()/1000 + ".jpg");
//			// 启动拍照,并保存到临时文件
//			Intent mIntent = new Intent();
//			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
//			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//			startActivityForResult(mIntent, FaceUtil.REQUEST_CAMERA_IMAGE);
			break;
		case R.id.static_detect:
			vibrate();//提示声音
			if(mImage == null){
//			     Menu2.this.finish();//返回到主页
				soundPool.play(scrId, 1, 1, 0, 0, 1);
				return;
			}
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
		    break;
		default:
			break;
		}
	}
	private void drawFaceRects2(FaceRect[] faces) {
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);
		paint.setStyle(Style.STROKE);

		Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
				mImage.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(mImage, new Matrix(), null);
		
		for (FaceRect face: faces) {
			canvas.drawRect(face.bound, paint);
			
			if (null != face.point) {
				for (Point p: face.point) {
					canvas.drawPoint(p.x, p.y, paint);
				}
			}
		}
		
		((ImageView) findViewById(R.id.static_img)).setImageBitmap(bitmap);
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
	            
//	            Log.v(TAG, "视距：" + myEyesDistance);
//	            Log.v(TAG, "人脸中心"+ myMidPoint.x +"  " + myMidPoint.y);
	            /*画官帽*/
	           // 剪裁一个区域，当前的操作对象为Rect裁剪的区域  
	            //Rect(x,y, w,h)
//	            Rect rect = new Rect ((int)(myMidPoint.x - myEyesDistance),
//	            		(int)(myMidPoint.y - myEyesDistance),
//	            		(int)(myMidPoint.x + myEyesDistance),
//	            		(int)(myMidPoint.y + myEyesDistance)) ;  
//	            Rect rect = new Rect(0,0, mImage.getWidth(), mImage.getHeight()/2);// 剪裁矩形框
	            //当前的画图区域为Rect裁剪的区域，而不是我们之前赋值的bitmap  
//	            canvas.clipRect(rect)  ; 
	            if( tietu == null){
	            	showInfo("这里什么也没有");
	                return;  
	            }
	            /*绘制矩形*/
//	            canvas.drawRect(            //设置矩形框的位置参数  
//	                    (int)(myMidPoint.x - myEyesDistance),   
//	                    (int)(myMidPoint.y - myEyesDistance),   
//	                    (int)(myMidPoint.x + myEyesDistance),   
//	                    (int)(myMidPoint.y + myEyesDistance*1.25),   
//	                    paint);  
//	            canvas.drawRect(rect, paint);
//	            canvas.drawColor(Color.YELLOW); //剪裁区域变成yellow
//	            paint.setTextSize((float) 30.0); //设置字体大小
//	            canvas.drawText("匹配人脸", myMidPoint.x - myEyesDistance, myMidPoint.y - myEyesDistance, paint) ;  //每个人脸旁写字提示
	            canvas.save();//隔离当前canvas配置
	            float ratio = 0.0125f * myEyesDistance; //0.0125为经验值
	            canvas.scale(ratio, ratio, myMidPoint.x, myMidPoint.y);//贴图缩放比例，按轴中心缩放
	            
//	            canvas.drawPoint(myMidPoint.x, myMidPoint.y, paint); //画中点
//	            paint.reset();//重新设置画笔
//	            paint.setColor(Color.BLUE);
//	            paint.setStrokeWidth(10);
//	            //画经过中点的一条长度为2myEyesDistance线 == 就是两眼的内部边缘距离
//	            canvas.drawLine(myMidPoint.x + myEyesDistance, myMidPoint.y, myMidPoint.x - myEyesDistance, myMidPoint.y, paint);
	            
	            canvas.drawBitmap(tietu, (int)(myMidPoint.x - distance), (int)(myMidPoint.y - distance),null);  //贴图，初始坐标（0， 0）相对于整个画布而言
	            canvas.restore();//恢复之前canvas配置
		}
		
		((ImageView) findViewById(R.id.static_img)).setImageBitmap(bitmap);//将图像显示在当前的ImageView控件中
	}
//	/*画贴图*/
//	private void drawHat(Context context, int resourceId, Canvas canvas, Paint paint, PointF myMidPoint ) {
//		/*加载位图数据并与纹理绑定*/
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		
//		options.inScaled = false;//告诉Android我们想要原始的图像数据，而不是这个图像的缩放版本。
//		
//		final Bitmap bitmap = BitmapFactory.decodeResource(
//				context.getResources(), resourceId, options);//实际的解码工作，这个调用会把解码后的图像存入OpenGL
//		if(bitmap == null){
//				Log.w(TAG, "Resource ID" + resourceId + "Could not be decode.");
//		}
//		
//		 Rect mSrcRect, mDestRect;  
//		 int mBitHeight = bitmap.getHeight();
//		 int mBitWidth  = bitmap.getWidth();
//		 mSrcRect = new Rect(0, 0, mBitWidth, mBitHeight);  
//		 mDestRect = new Rect(0, 0, mBitWidth, mBitHeight);  
//		canvas.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
//		
//		/**画矩形可以*/
////        canvas.drawRect(            //设置矩形框的位置参数  
////        (int)(myMidPoint.x - myEyesDistance),   
////        (int)(myMidPoint.y - myEyesDistance),   
////        (int)(myMidPoint.x + myEyesDistance),   
////        (int)(myMidPoint.y + myEyesDistance*1.25),   
////        paint);  
//	}



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
		if (requestCode == 1) {//第二个if  判断requestCode和上面 startActivity(),里的参数相同,就获取图片
		  	 Uri uri = data.getData();  //获取图片Path
	         Log.d(TAG, "URI:"+ uri.getPath());//错误路径
	         ContentResolver cr = this.getContentResolver();  
	         String[] proj = {MediaStore.Images.Media.DATA};
	         @SuppressWarnings("deprecation")
			 Cursor cursor = managedQuery(uri, proj, null, null, null);
	         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//按我个人理解 这个是获得用户选择的图片的索引值
	         cursor.moveToFirst();//将光标移至开头 ，这个很重要，不小心很容易引起越界
	         //最后根据索引值获取图片路径
	         String imagePath = cursor.getString(column_index);

	         Log.d(TAG, "path：  "+ imagePath);//路径正确
//	         copyFile(imagePath, newpath);//暂且没用着
	         Toast.makeText(getApplicationContext(), "图片导入成功", Toast.LENGTH_SHORT).show();
            try {  
				
            	/*怎么从图库 导入 bitmap*/
			    BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
		        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //构造位图生成的参数，必须为565。类名+enum  
		        BitmapFactoryOptionsbfo.inPurgeable = true;
		        BitmapFactoryOptionsbfo.inInputShareable = true;
		        mImage = BitmapFactory.decodeFile(imagePath, BitmapFactoryOptionsbfo); //此时返回mImage为空
		        
//		        mImage = BitmapFactory.decodeResource(getResources(), R.drawable.baby2, BitmapFactoryOptionsbfo); //导入图片
//		        mImage = BitmapFactory.decodeResource(getResources(), R.drawable.meinv3, BitmapFactoryOptionsbfo); //导入图片
		        Log.d(TAG, "Bitmap: " + mImage);
    			/*图片太大，Imageview不显示*/
			        DisplayMetrics dm = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(dm);
			        int screenWidth=dm.widthPixels;
			        if(mImage.getWidth() <= screenWidth){
			        	((ImageView) findViewById(R.id.static_img)).setImageBitmap(mImage);
			        }else{
//			        	Bitmap bmp = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight()*screenWidth/mImage.getWidth(), true);
//			        	((ImageView) findViewById(R.id.static_img)).setImageBitmap(bmp);
			        	mImage = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight()*screenWidth/mImage.getWidth(), true);
			        	((ImageView) findViewById(R.id.static_img)).setImageBitmap(mImage);
			        }
    			
			} catch (Exception e) {  
			    // TODO Auto-generated catch block  
			    e.printStackTrace();  
			}  
            
		}
//		else if (requestCode == FaceUtil.REQUEST_CAMERA_IMAGE) {
//			if (null == mPictureFile) {
//				showInfo("拍照失败，请重试");
//				return;
//			}
//			
//			fileSrc = mPictureFile.getAbsolutePath();
//			updateGallery(fileSrc);
//			// 跳转到图片裁剪页面
//			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
//		} 
//		else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {   //Image怎么找到
//			Log.v("TAG", "REQUEST_CROP_IMAGE is open!");
//		}
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
