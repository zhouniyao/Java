package com.niming;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.niming.util.FaceRect;
import com.niming.util.SoundUtil;
import com.niming.zfacedetect.R;


import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.FaceDetector;
import android.media.MediaScannerConnection;
import android.media.FaceDetector.Face;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Menu2 extends Activity {
	private boolean isMore = false;// menu�˵���ҳ����
    AlertDialog menuDialog;// menu�˵�Dialog
    GridView menuGrid;
    View menuView;
    
    private final int ITEM_SEARCH = 0;// ����
    private final int ITEM_FILE_MANAGER = 1;// �ļ�����
    private final int ITEM_DOWN_MANAGER = 2;// ���ع���
    private final int ITEM_FULLSCREEN = 3;// ȫ��
    private final int ITEM_MORE = 11;// �˵�

    
    /** �˵�ͼƬ **/
    int[] menu_image_array = { 
    		R.drawable.rect,
            R.drawable.hat3, R.drawable.princess3,
            R.drawable.zhenhuan3, R.drawable.keai3,
    		R.drawable.m1,
            R.drawable.m2, R.drawable.m3,
            R.drawable.m4, R.drawable.m5,
            R.drawable.m6, 
            android.R.drawable.ic_menu_more };
    /** �˵����� **/
    String[] menu_name_array = { "ʶ���", "��ñ", "����", "���", "���", "����",
            "����2", "����", "ҳ��", "ҹ��ģʽ", "ˢ��", "����" };
    
    /** �˵�ͼƬ2 **/
    int[] menu_image_array2 = { R.drawable.m12,
            R.drawable.m2, R.drawable.m13,
            R.drawable.m14, R.drawable.m15,
            R.drawable.m16, R.drawable.m17,
            R.drawable.m18, R.drawable.m19,
            R.drawable.m20, R.drawable.m11,
            android.R.drawable.ic_menu_more };
    /** �˵�����2 **/
    String[] menu_name_array2 = { "�Զ�����", "��ѡģʽ", "�Ķ�ģʽ", "���ģʽ", "��ݷ�ҳ",
            "������", "�������", "��ʱˢ��", "����", "����", "����", "����" };
    
    
    /******************************************��������õı����ͳ���*******************************************************/
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
	private Vibrator vibrator; //��
	private SoundPool soundPool; //����
	int scrId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);    
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);//����1������
		scrId = soundPool.load(Menu2.this, R.raw.self_camera, 1);
		
        setContentView(R.layout.activity_menucamera);
        //��ImageView���ͼƬ
        ImageView imageView = (ImageView) findViewById(R.id.menu_img);
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
        
        menuView = View.inflate(this, R.layout.gridview, null);
        // ����AlertDialog
        menuDialog = new AlertDialog.Builder(this).create();
        menuDialog.setView(menuView);
        menuDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// ��������
                    dialog.dismiss();
                return false;
            }
        });

        menuGrid = (GridView) menuView.findViewById(R.id.gridview1);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        /** ����menuѡ�� **/
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                case 0:// ����
	   				 tietu = BitmapFactory.decodeResource(getResources(), R.drawable.rect) ;  
				     vibrate();distance = (int)(tietu.getWidth()/2);
				     menuDialog.cancel();
				     detectDraw();
                   break;
               case 1:// ��ñ
               	tietu = BitmapFactory.decodeResource(getResources(), R.drawable.hat) ;  
    		        vibrate();distance = (int)(tietu.getWidth()/2);
    		        menuDialog.cancel();
    		        detectDraw();
                   break;
               case 2:// ����
               	tietu = BitmapFactory.decodeResource(getResources(), R.drawable.princess);
   				vibrate();distance = (int)(tietu.getWidth()/2);
   				menuDialog.cancel();
   				detectDraw();
                   break;
               case 3:// ���
   				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.zhenhuan2) ;  
   				vibrate();distance = (int)(tietu.getWidth()/2);
   				menuDialog.cancel();
   				detectDraw();
                   break;
               case 4://�ɰ�
   				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.keai2) ;  
   				vibrate();distance = (int)(tietu.getWidth()/2);
   				menuDialog.cancel();
   				detectDraw();
               	break;
               case 5://����
               case 6://����
               case 7://����
               case 8://����
               case 9://����
               case 10://����
               case 12://����
               case 13://����
               case 14://����
               case 15://����
            	    vibrate();
            	    menuDialog.cancel();
	               	showInfo("��ѡ��δʹ��");
	               	break;
               case 11:// ��ҳ
            	   vibrate();
                   if (isMore) {
                       menuGrid.setAdapter(getMenuAdapter(menu_name_array2,
                               menu_image_array2));
                       isMore = false;
                   } else {// ��ҳ
                       menuGrid.setAdapter(getMenuAdapter(menu_name_array,
                               menu_image_array));
                       isMore = true;
                   }
                   menuGrid.invalidate();// ����menu
                   menuGrid.setSelection(11);
                   break;
               }
                
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// ���봴��һ��
        return super.onCreateOptionsMenu(menu);
    }
    
    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
            int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.menu_stye, new String[] { "itemImage", "itemText" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
        } else {
            menuDialog.show();
        }
        return false;// ����Ϊtrue ����ʾϵͳmenu
    }
    
    /**
     * Ϊ����׼��ѡ��˵���ÿ��ѡ��˵���ʾǰ������ø÷���������ͨ�������������ĳЩ�˵�����û��߲����ã�
     * ͬʱҲ�����޸Ĳ˵�������ݣ���д�÷���ʱ����true������ѡ��˵�������ʾ
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // �������false���û��޷����menu�Ķ�����menu��ʧ��onCreateOptionsMenu���������ᱻ����
        return true;
    }
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	// TODO Auto-generated method stub
    	super.onOptionsMenuClosed(menu);
    }

	private void showInfo(final String str) {
		mToast.setText(str);
		mToast.show();
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
			        	((ImageView) findViewById(R.id.menu_img)).setImageBitmap(mImage);
			        }else{
//			        	Bitmap bmp = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight()*screenWidth/mImage.getWidth(), true);
//			        	((ImageView) findViewById(R.id.static_img)).setImageBitmap(bmp);
			        	mImage = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight()*screenWidth/mImage.getWidth(), true);
			        	((ImageView) findViewById(R.id.menu_img)).setImageBitmap(mImage);
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
	/**
	 * ��������Ȼ�����
	 */
	void detectDraw(){
		if(mImage == null){
			soundPool.play(scrId, 1, 1, 0, 0, 1);
			//		     Menu2.this.finish();//���ص���ҳ
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
			if( tietu == null){
				return;  
			}
			canvas.save();//���뵱ǰcanvas����
			float ratio = 0.0125f * myEyesDistance; //0.0125Ϊ����ֵ
			canvas.scale(ratio, ratio, myMidPoint.x, myMidPoint.y);//��ͼ���ű�����������������
			
			
			canvas.drawBitmap(tietu, (int)(myMidPoint.x - distance), (int)(myMidPoint.y - distance),null);  //��ͼ����ʼ���꣨0�� 0�������������������
			canvas.restore();//�ָ�֮ǰcanvas����
		}
		
		((ImageView) findViewById(R.id.menu_img)).setImageBitmap(bitmap);//��ͼ����ʾ�ڵ�ǰ��ImageView�ؼ���
	}
}
