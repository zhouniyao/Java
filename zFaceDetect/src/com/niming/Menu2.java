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
	private boolean isMore = false;// menu菜单翻页控制
    AlertDialog menuDialog;// menu菜单Dialog
    GridView menuGrid;
    View menuView;
    
    private final int ITEM_SEARCH = 0;// 搜索
    private final int ITEM_FILE_MANAGER = 1;// 文件管理
    private final int ITEM_DOWN_MANAGER = 2;// 下载管理
    private final int ITEM_FULLSCREEN = 3;// 全屏
    private final int ITEM_MORE = 11;// 菜单

    
    /** 菜单图片 **/
    int[] menu_image_array = { 
    		R.drawable.rect,
            R.drawable.hat3, R.drawable.princess3,
            R.drawable.zhenhuan3, R.drawable.keai3,
    		R.drawable.m1,
            R.drawable.m2, R.drawable.m3,
            R.drawable.m4, R.drawable.m5,
            R.drawable.m6, 
            android.R.drawable.ic_menu_more };
    /** 菜单文字 **/
    String[] menu_name_array = { "识别框", "官帽", "公主", "格格", "简笔", "其他",
            "其他2", "分享", "页面", "夜间模式", "刷新", "更多" };
    
    /** 菜单图片2 **/
    int[] menu_image_array2 = { R.drawable.m12,
            R.drawable.m2, R.drawable.m13,
            R.drawable.m14, R.drawable.m15,
            R.drawable.m16, R.drawable.m17,
            R.drawable.m18, R.drawable.m19,
            R.drawable.m20, R.drawable.m11,
            android.R.drawable.ic_menu_more };
    /** 菜单文字2 **/
    String[] menu_name_array2 = { "自动横屏", "笔选模式", "阅读模式", "浏览模式", "快捷翻页",
            "检查更新", "检查网络", "定时刷新", "设置", "帮助", "关于", "返回" };
    
    
    /******************************************人脸检测用的变量和常量*******************************************************/
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
	private Vibrator vibrator; //震动
	private SoundPool soundPool; //声音
	int scrId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);    
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);//容纳1个声音
		scrId = soundPool.load(Menu2.this, R.raw.self_camera, 1);
		
        setContentView(R.layout.activity_menucamera);
        //给ImageView添加图片
        ImageView imageView = (ImageView) findViewById(R.id.menu_img);
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
        
        menuView = View.inflate(this, R.layout.gridview, null);
        // 创建AlertDialog
        menuDialog = new AlertDialog.Builder(this).create();
        menuDialog.setView(menuView);
        menuDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
                    dialog.dismiss();
                return false;
            }
        });

        menuGrid = (GridView) menuView.findViewById(R.id.gridview1);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        /** 监听menu选项 **/
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                case 0:// 方框
	   				 tietu = BitmapFactory.decodeResource(getResources(), R.drawable.rect) ;  
				     vibrate();distance = (int)(tietu.getWidth()/2);
				     menuDialog.cancel();
				     detectDraw();
                   break;
               case 1:// 官帽
               	tietu = BitmapFactory.decodeResource(getResources(), R.drawable.hat) ;  
    		        vibrate();distance = (int)(tietu.getWidth()/2);
    		        menuDialog.cancel();
    		        detectDraw();
                   break;
               case 2:// 公主
               	tietu = BitmapFactory.decodeResource(getResources(), R.drawable.princess);
   				vibrate();distance = (int)(tietu.getWidth()/2);
   				menuDialog.cancel();
   				detectDraw();
                   break;
               case 3:// 甄嬛
   				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.zhenhuan2) ;  
   				vibrate();distance = (int)(tietu.getWidth()/2);
   				menuDialog.cancel();
   				detectDraw();
                   break;
               case 4://可爱
   				tietu = BitmapFactory.decodeResource(getResources(), R.drawable.keai2) ;  
   				vibrate();distance = (int)(tietu.getWidth()/2);
   				menuDialog.cancel();
   				detectDraw();
               	break;
               case 5://其他
               case 6://其他
               case 7://其他
               case 8://其他
               case 9://其他
               case 10://其他
               case 12://其他
               case 13://其他
               case 14://其他
               case 15://其他
            	    vibrate();
            	    menuDialog.cancel();
	               	showInfo("该选择还未使用");
	               	break;
               case 11:// 翻页
            	   vibrate();
                   if (isMore) {
                       menuGrid.setAdapter(getMenuAdapter(menu_name_array2,
                               menu_image_array2));
                       isMore = false;
                   } else {// 首页
                       menuGrid.setAdapter(getMenuAdapter(menu_name_array,
                               menu_image_array));
                       isMore = true;
                   }
                   menuGrid.invalidate();// 更新menu
                   menuGrid.setSelection(11);
                   break;
               }
                
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// 必须创建一项
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
        return false;// 返回为true 则显示系统menu
    }
    
    /**
     * 为程序准备选项菜单，每次选项菜单显示前都会调用该方法，可以通过这个方法设置某些菜单项可用或者不可用，
     * 同时也可以修改菜单项的内容，重写该方法时返回true，否则选项菜单不会显示
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // 如果返回false，用户无法点击menu的动作，menu消失，onCreateOptionsMenu方法将不会被调用
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
	 * 找人脸，然后绘制
	 */
	void detectDraw(){
		if(mImage == null){
			soundPool.play(scrId, 1, 1, 0, 0, 1);
			//		     Menu2.this.finish();//返回到主页
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
			if( tietu == null){
				return;  
			}
			canvas.save();//隔离当前canvas配置
			float ratio = 0.0125f * myEyesDistance; //0.0125为经验值
			canvas.scale(ratio, ratio, myMidPoint.x, myMidPoint.y);//贴图缩放比例，按轴中心缩放
			
			
			canvas.drawBitmap(tietu, (int)(myMidPoint.x - distance), (int)(myMidPoint.y - distance),null);  //贴图，初始坐标（0， 0）相对于整个画布而言
			canvas.restore();//恢复之前canvas配置
		}
		
		((ImageView) findViewById(R.id.menu_img)).setImageBitmap(bitmap);//将图像显示在当前的ImageView控件中
	}
}
