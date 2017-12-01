package com.niming;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.niming.util.FaceRect;
import com.niming.util.FaceUtil;
import com.niming.util.ParseResult;
import com.niming.util.PressPicture;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.facedemo.R;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 给照片贴图
 */
public class StaticDetect extends Activity implements OnClickListener{
	private static final String TAG = "niming";
	
	private final int maxFaces = 5;
	private Bitmap mImage = null;
	private Toast mToast;
	private File mPictureFile;
	private FaceDetector mFaceDetector;
	private FaceRect[] mFaces;
    float myEyesDistance;           
    int numberOfFaceDetected;       
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_static);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		ImageView imageView = (ImageView) findViewById(R.id.static_img);
		Button pickButton   = (Button) findViewById(R.id.static_pick);  
		Button cameraButton = (Button) findViewById(R.id.static_camera);
		Button attachButton = (Button) findViewById(R.id.static_detect);
		
		pickButton.setOnClickListener(StaticDetect.this);
		cameraButton.setOnClickListener(StaticDetect.this);
		attachButton.setOnClickListener(StaticDetect.this);
		
		mFaceDetector = FaceDetector.createDetector(this, null);
	}


	@Override
	public void onClick(View v) {
		if(mFaceDetector == null){
			Log.w(TAG, "FaceDetector对象未生成");
			return;
		}
		
		switch (v.getId()) {
		case R.id.static_pick:
			
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(intent, FaceUtil.REQUEST_PICTURE_CHOOSE);
			break;
		case R.id.static_camera:
			mPictureFile = new File(Environment.getExternalStorageDirectory(), 
					"picture" + System.currentTimeMillis()/1000 + ".jpg");
			Intent mIntent = new Intent();
			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			startActivityForResult(mIntent, FaceUtil.REQUEST_CAMERA_IMAGE);
			break;
		case R.id.static_detect:
			
			if (null != mImage) {				
				// 启动图片人脸检测
				String result = mFaceDetector.detectARGB(mImage);
				Log.d(TAG, "result:"+result);
				// 解析人脸结果
				mFaces = ParseResult.parseResult(result);
				if (null != mFaces && mFaces.length > 0) {
					drawFaceRects(mFaces);
				} else {
					// 在无人脸的情况下，判断结果信息
					int errorCode = 0;
					JSONObject object;
					try {
						object = new JSONObject(result);
						errorCode = object.getInt("ret");
					} catch (JSONException e) {
					}
					// errorCode!=0，表示人脸发生错误，请根据错误处理
					if(ErrorCode.SUCCESS == errorCode) {
						showInfo("没有检测到人脸");
					} else {
						showInfo("检测发生错误，错误码："+errorCode);
					}
				}
			} else {
				showInfo("请选择图片后再检测");
			}
			break;
		default:
			break;
		}
	}
	
	private void drawFaceRects(FaceRect[] faces) {
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
	
		
	public static final Bitmap getBitmap(ContentResolver cr, Uri uri)
	        throws FileNotFoundException, IOException {
	    InputStream input = cr.openInputStream(uri);
	    Bitmap bitmap = BitmapFactory.decodeStream(input);
	    input.close();
	    return bitmap;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode != RESULT_OK) {						
			return;
		}
		
		String fileSrc = null;
		if (requestCode ==  FaceUtil.REQUEST_PICTURE_CHOOSE) {
		  	 Uri uri = data.getData();  
	         Log.d(TAG, "URI:"+ uri.getPath());
	         ContentResolver cr = this.getContentResolver();  
	             
            try {  
				
            	mImage = PressPicture.getBitmapFormUri(cr, uri);
            	Log.d(TAG, "Bitmap: " + mImage);
//			    /*test*/
//			    BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();   
//		        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;  
//		        mImage = BitmapFactory.decodeResource(getResources(), R.drawable.baby2, BitmapFactoryOptionsbfo); 
////		    mImage = BitmapFactory.decodeResource(getResources(), R.drawable.meinv3, BitmapFactoryOptionsbfo); 
//			    
    	
		        
    			((ImageView) findViewById(R.id.static_img)).setImageBitmap(mImage);
    			
			} catch (Exception e) {  
			    // TODO Auto-generated catch block  
			    e.printStackTrace();  
			}  
            
		} else if (requestCode == FaceUtil.REQUEST_CAMERA_IMAGE) {
			if (null == mPictureFile) {
				showInfo("����ʧ�ܣ�������");
				return;
			}
			
			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			
			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
		} 
		else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {  
			Log.v("TAG", "REQUEST_CROP_IMAGE is open!");
		}
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
}
