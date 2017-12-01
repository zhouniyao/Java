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
 * ��Ƭ��ͼ
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
	//FaceDetector ���󣬼���������ʶ��
	private FaceDetector mFaceDetector;
	//����ʶ����
	private FaceRect[] mFaces;
	private FaceDetector.Face[] myFaces; //�洢�����������������  
    float myEyesDistance;           //����֮��ľ���  
    int numberOfFaceDetected;       //ʵ�ʼ�⵽��������  
	public Vibrator vibrator;
	private SoundPool soundPool; //����
	int scrId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_static);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);//����1������
		scrId = soundPool.load(StaticDetect.this, R.raw.nopicture, 1);
		
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		ImageView imageView = (ImageView) findViewById(R.id.static_img);//չʾͼƬ
		Button attachButton = (Button) findViewById(R.id.static_detect);//������ͼ��ť
		
		imageView.setOnClickListener (StaticDetect.this);
		attachButton.setOnClickListener(StaticDetect.this);
		
		findViewById(R.id.rect).setOnClickListener(StaticDetect.this);//ѡ��
		findViewById(R.id.hat).setOnClickListener(StaticDetect.this);//��ñ
		findViewById(R.id.pricess).setOnClickListener(StaticDetect.this);//����
		findViewById(R.id.zhenhuan).setOnClickListener(StaticDetect.this);//���
		findViewById(R.id.zhichang).setOnClickListener(StaticDetect.this);//�Ʒ�
		
	}

	void vibrate(){
//		vibrator.vibrate(new long[]{0,1000}, -1);//������Ƶ�ʣ�ʱ��1��
		vibrator.vibrate(VIBRATE_TIME);
	}

	@Override
	public void onClick(View v) {
//		if(mFaceDetector == null){
//			Log.v(TAG, "����FaceDetectorʧ��");
//			return;
//		}
		switch (v.getId()) {
		case R.id.static_img:
			/*������תͼ��*/
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(intent, 1);
			break;
		case R.id.rect:
	        tietu = BitmapFactory.decodeResource(getResources(), R.drawable.rect) ;  
	        vibrate();distance = (int)(tietu.getWidth()/2);
			break;
		case R.id.hat://ѡ��hat
			 //��hatͼ��ת��ΪBitmap����  
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
		case R.id.pricess://ѡ��dog
			tietu = BitmapFactory.decodeResource(getResources(), R.drawable.princess);
			vibrate();distance = (int)(tietu.getWidth()/2);
//			// ����������պ���Ƭ����·��
//			mPictureFile = new File(Environment.getExternalStorageDirectory(), 
//					"picture" + System.currentTimeMillis()/1000 + ".jpg");
//			// ��������,�����浽��ʱ�ļ�
//			Intent mIntent = new Intent();
//			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
//			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//			startActivityForResult(mIntent, FaceUtil.REQUEST_CAMERA_IMAGE);
			break;
		case R.id.static_detect:
			vibrate();//��ʾ����
			if(mImage == null){
//			     Menu2.this.finish();//���ص���ҳ
				soundPool.play(scrId, 1, 1, 0, 0, 1);
				return;
			}
			/*����FaceDetector*/
		    int imageWidth = mImage.getWidth();   
			int imageHeight = mImage.getHeight();   
			myFaces = new FaceDetector.Face[maxFaces];       //������������ռ�  
			mFaceDetector = new FaceDetector(imageWidth, imageHeight, maxFaces);   
			numberOfFaceDetected = mFaceDetector.findFaces(mImage, myFaces);    //FaceDetector ����ʵ������������  
			if(numberOfFaceDetected == 0){
				showInfo("�Ҳ�������");
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
		//�½�Bitmap
		Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
				mImage.getHeight(), Config.ARGB_8888);
		//����canvas����
		Canvas canvas = new Canvas(bitmap);//�趨��������С��С
		canvas.drawBitmap(mImage, new Matrix(), null);//�Ȼ�����ͼ
		paint.setColor(Color.RED);
		paint.setStrokeWidth(20);//���û��ʴ�ϸ
		
		for (int i=0; i < numberOfFaceDetected; i++) {
            
			
			FaceDetector.Face face = myFaces[i];
			  PointF myMidPoint = new PointF();   //һ��������ά����(x, y)����
	            face.getMidPoint(myMidPoint);   
	            myEyesDistance = face.eyesDistance();   //�õ��������ĵ���ۼ�������������ÿ���������л���  
	            
//	            Log.v(TAG, "�Ӿࣺ" + myEyesDistance);
//	            Log.v(TAG, "��������"+ myMidPoint.x +"  " + myMidPoint.y);
	            /*����ñ*/
	           // ����һ�����򣬵�ǰ�Ĳ�������ΪRect�ü�������  
	            //Rect(x,y, w,h)
//	            Rect rect = new Rect ((int)(myMidPoint.x - myEyesDistance),
//	            		(int)(myMidPoint.y - myEyesDistance),
//	            		(int)(myMidPoint.x + myEyesDistance),
//	            		(int)(myMidPoint.y + myEyesDistance)) ;  
//	            Rect rect = new Rect(0,0, mImage.getWidth(), mImage.getHeight()/2);// ���þ��ο�
	            //��ǰ�Ļ�ͼ����ΪRect�ü������򣬶���������֮ǰ��ֵ��bitmap  
//	            canvas.clipRect(rect)  ; 
	            if( tietu == null){
	            	showInfo("����ʲôҲû��");
	                return;  
	            }
	            /*���ƾ���*/
//	            canvas.drawRect(            //���þ��ο��λ�ò���  
//	                    (int)(myMidPoint.x - myEyesDistance),   
//	                    (int)(myMidPoint.y - myEyesDistance),   
//	                    (int)(myMidPoint.x + myEyesDistance),   
//	                    (int)(myMidPoint.y + myEyesDistance*1.25),   
//	                    paint);  
//	            canvas.drawRect(rect, paint);
//	            canvas.drawColor(Color.YELLOW); //����������yellow
//	            paint.setTextSize((float) 30.0); //���������С
//	            canvas.drawText("ƥ������", myMidPoint.x - myEyesDistance, myMidPoint.y - myEyesDistance, paint) ;  //ÿ��������д����ʾ
	            canvas.save();//���뵱ǰcanvas����
	            float ratio = 0.0125f * myEyesDistance; //0.0125Ϊ����ֵ
	            canvas.scale(ratio, ratio, myMidPoint.x, myMidPoint.y);//��ͼ���ű�����������������
	            
//	            canvas.drawPoint(myMidPoint.x, myMidPoint.y, paint); //���е�
//	            paint.reset();//�������û���
//	            paint.setColor(Color.BLUE);
//	            paint.setStrokeWidth(10);
//	            //�������е��һ������Ϊ2myEyesDistance�� == �������۵��ڲ���Ե����
//	            canvas.drawLine(myMidPoint.x + myEyesDistance, myMidPoint.y, myMidPoint.x - myEyesDistance, myMidPoint.y, paint);
	            
	            canvas.drawBitmap(tietu, (int)(myMidPoint.x - distance), (int)(myMidPoint.y - distance),null);  //��ͼ����ʼ���꣨0�� 0�������������������
	            canvas.restore();//�ָ�֮ǰcanvas����
		}
		
		((ImageView) findViewById(R.id.static_img)).setImageBitmap(bitmap);//��ͼ����ʾ�ڵ�ǰ��ImageView�ؼ���
	}
//	/*����ͼ*/
//	private void drawHat(Context context, int resourceId, Canvas canvas, Paint paint, PointF myMidPoint ) {
//		/*����λͼ���ݲ��������*/
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		
//		options.inScaled = false;//����Android������Ҫԭʼ��ͼ�����ݣ����������ͼ������Ű汾��
//		
//		final Bitmap bitmap = BitmapFactory.decodeResource(
//				context.getResources(), resourceId, options);//ʵ�ʵĽ��빤����������û�ѽ�����ͼ�����OpenGL
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
//		/**�����ο���*/
////        canvas.drawRect(            //���þ��ο��λ�ò���  
////        (int)(myMidPoint.x - myEyesDistance),   
////        (int)(myMidPoint.y - myEyesDistance),   
////        (int)(myMidPoint.x + myEyesDistance),   
////        (int)(myMidPoint.y + myEyesDistance*1.25),   
////        paint);  
//	}



	/**
	 *�����ͼƬ�ɹ��������޷����
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
		//���Ǹ��ص�����
		if (resultCode != RESULT_OK) {						//��һ��if �ж��Ƿ񷵻سɹ�
			return;
		}
		
		String fileSrc = null;
		if (requestCode == 1) {//�ڶ���if  �ж�requestCode������ startActivity(),��Ĳ�����ͬ,�ͻ�ȡͼƬ
		  	 Uri uri = data.getData();  //��ȡͼƬPath
	         Log.d(TAG, "URI:"+ uri.getPath());//����·��
	         ContentResolver cr = this.getContentResolver();  
	         String[] proj = {MediaStore.Images.Media.DATA};
	         @SuppressWarnings("deprecation")
			 Cursor cursor = managedQuery(uri, proj, null, null, null);
	         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
	         cursor.moveToFirst();//�����������ͷ ���������Ҫ����С�ĺ���������Խ��
	         //����������ֵ��ȡͼƬ·��
	         String imagePath = cursor.getString(column_index);

	         Log.d(TAG, "path��  "+ imagePath);//·����ȷ
//	         copyFile(imagePath, newpath);//����û����
	         Toast.makeText(getApplicationContext(), "ͼƬ����ɹ�", Toast.LENGTH_SHORT).show();
            try {  
				
            	/*��ô��ͼ�� ���� bitmap*/
			    BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
		        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //����λͼ���ɵĲ���������Ϊ565������+enum  
		        BitmapFactoryOptionsbfo.inPurgeable = true;
		        BitmapFactoryOptionsbfo.inInputShareable = true;
		        mImage = BitmapFactory.decodeFile(imagePath, BitmapFactoryOptionsbfo); //��ʱ����mImageΪ��
		        
//		        mImage = BitmapFactory.decodeResource(getResources(), R.drawable.baby2, BitmapFactoryOptionsbfo); //����ͼƬ
//		        mImage = BitmapFactory.decodeResource(getResources(), R.drawable.meinv3, BitmapFactoryOptionsbfo); //����ͼƬ
		        Log.d(TAG, "Bitmap: " + mImage);
    			/*ͼƬ̫��Imageview����ʾ*/
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
//				showInfo("����ʧ�ܣ�������");
//				return;
//			}
//			
//			fileSrc = mPictureFile.getAbsolutePath();
//			updateGallery(fileSrc);
//			// ��ת��ͼƬ�ü�ҳ��
//			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
//		} 
//		else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {   //Image��ô�ҵ�
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
	           if (!oldfile.exists()) { //�ļ�������ʱ   
	               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�   
	               FileOutputStream fs = new FileOutputStream(newPath);   
	               byte[] buffer = new byte[1024];   
	               int length;   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   bytesum += byteread; //�ֽ��� �ļ���С   
	                   System.out.println(bytesum);   
	                   fs.write(buffer, 0, byteread);   
	               }   
	               inStream.close();   
	           }   
	       }   
	       catch (Exception e) {   
	           System.out.println("���Ƶ����ļ���������");   
	           e.printStackTrace();   

	       }   

	   } 


}
