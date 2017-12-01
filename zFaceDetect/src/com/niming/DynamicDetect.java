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
	//FaceDetector ���󣬼���������ʶ��
	private FaceDetector mFaceDetector;
	//����ʶ����
	private FaceRect[] mFaces;
	private FaceDetector.Face[] myFaces; //�洢�����������������  
    float myEyesDistance;           //����֮��ľ���  
    int numberOfFaceDetected;       //ʵ�ʼ�⵽��������  
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		ImageView imageView = (ImageView) findViewById(R.id.dynamic_img);//չʾͼƬ
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����������պ���Ƭ����·��
				mPictureFile = new File(Environment.getExternalStorageDirectory(), 
						"picture" + System.currentTimeMillis()/1000 + ".jpg");
				// ��������,�����浽��ʱ�ļ�
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
		});//ѡ��
		findViewById(R.id.hat2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        tietu = BitmapFactory.decodeResource(getResources(), R.drawable.hat) ;  
		        vibrate();distance = (int)(tietu.getWidth()/2);
		        detectDraw();
			}
		});//��ñ
		findViewById(R.id.pricess2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.princess);
				vibrate();distance = (int)(tietu.getWidth()/2);
				detectDraw();
			}
		});//����
		findViewById(R.id.zhenhuan2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.zhenhuan2) ;  
				vibrate();distance = (int)(tietu.getWidth()/2);
				detectDraw();
			}
		});//���
		findViewById(R.id.keai2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.keai2) ;  
				vibrate();distance = (int)(tietu.getWidth()/2);
				detectDraw();
			}
		});//�ɰ�
		
	}
	/**
	 * ��������Ȼ�����
	 */
	void detectDraw(){
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
	}
	/**
	 * ������
	 */
	void vibrate(){
//		vibrator.vibrate(new long[]{0,1000}, -1);//������Ƶ�ʣ�ʱ��1��
		vibrator.vibrate(VIBRATE_TIME);
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
	            
	            Log.v(TAG, "�Ӿࣺ" + myEyesDistance);
	            Log.v(TAG, "��������"+ myMidPoint.x +"  " + myMidPoint.y);
	            if( tietu == null){
	                return;  
	            }
	            canvas.save();//���뵱ǰcanvas����
	            float ratio = 0.0125f * myEyesDistance; //0.0125Ϊ����ֵ
	            canvas.scale(ratio, ratio, myMidPoint.x, myMidPoint.y);//��ͼ���ű�����������������
	            
	            
	            canvas.drawBitmap(tietu, (int)(myMidPoint.x - distance), (int)(myMidPoint.y - distance),null);  //��ͼ����ʼ���꣨0�� 0�������������������
	            canvas.restore();//�ָ�֮ǰcanvas����
		}
		
		((ImageView) findViewById(R.id.dynamic_img)).setImageBitmap(bitmap);//��ͼ����ʾ�ڵ�ǰ��ImageView�ؼ���
	}



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
		/*����ص�*/
		if (requestCode == 3) {//�ڶ���if  �ж�requestCode������ startActivity(),��Ĳ�����ͬ,�ͻ�ȡͼƬ
			
			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			
			
//		  	 Uri uri = data.getData();  //��ȡͼƬPath
//	         Log.d(TAG, "URI:"+ uri.getPath());//����·��
//	         ContentResolver cr = this.getContentResolver();  
//	         String[] proj = {MediaStore.Images.Media.DATA};
//	         @SuppressWarnings("deprecation")
//			 Cursor cursor = managedQuery(uri, proj, null, null, null);
//	         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
//	         cursor.moveToFirst();//�����������ͷ ���������Ҫ����С�ĺ���������Խ��
//	         //����������ֵ��ȡͼƬ·��
//	         String imagePath = cursor.getString(column_index);
			 String imagePath = fileSrc;

	         Log.d(TAG, "path��  "+ imagePath);//·����ȷ
	         Toast.makeText(getApplicationContext(), "ͼƬ����ɹ�", Toast.LENGTH_SHORT).show();
            try {  
				
            	/*��ô��ͼ�� ���� bitmap*/
			    BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
		        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  //����λͼ���ɵĲ���������Ϊ565������+enum  
		        BitmapFactoryOptionsbfo.inPurgeable = true;
		        BitmapFactoryOptionsbfo.inInputShareable = true;
		        mImage = BitmapFactory.decodeFile(imagePath, BitmapFactoryOptionsbfo); //��ʱ����mImageΪ��
		        
		        Log.d(TAG, "Bitmap: " + mImage);
    			/*ͼƬ̫��Imageview����ʾ*/
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
