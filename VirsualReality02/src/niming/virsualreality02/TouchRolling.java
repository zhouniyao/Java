package niming.virsualreality02;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import niming.core.MyActivity;
import niming.core.MyRenderer;
import niming.core.MySurfaceView;


public class TouchRolling extends MyActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//渲染器对象
		final MyRenderer renderer = new MyRenderer(TouchRolling.this,  "niming.virsualreality02:raw/camaro_obj", R.drawable.camaro);
		//设置纹理图形
//		renderer.setTexture(R.drawable.camaro); //纹理不能再当前context下生成（UI线程不能调用OpenGL API）
//		renderer.setTYPE(renderer.POINTS);
		
//		mGLSurfaceView = new MySurfaceView(this, renderer);	//触碰没反应 （UI线程不能调用OpenGL API）
		
		
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // 这行设置OpenGL ES 2.0，必须在setRenderer前
		mGLSurfaceView.setRenderer(renderer);
	    ll.addView(mGLSurfaceView);   
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
        
        //GLSurfaceView设置触碰监听【也是触碰没有反应】
        mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
        	@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event != null){
					//Convert touch coordinates into normalized device coordinates,
					//keeping in mind that Android's Y coordinates are inverted.
					//视图左上角映射为(0,0)，右下角映射到的坐标为视图的大小
					final float normalizedX =
							(event.getX() / (float) v.getWidth()) * 2 - 1;
					final float normalizedY =
							-((event.getY() / (float) v.getHeight()) * 2 - 1);
					/*
					 * 检查一下这个事件按压(press)还是拖拽(drag)
					 * 【重要提示】Android的UI运行在主线程，而OpenGL的GLSurfaceView运行在一个单独的线程中，
					 * 因此我们需要使用线程安全的技术在两个线程间通讯。
					 * 我们使用queueEvent()给OpenGL线程分发调用，对于按压事件，调用airHockeyRenderer.handleTouchPress()。
					 * 对于拖拽事件，调用airHockeyRender.handleTouchDrag()。 
					 */
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mGLSurfaceView.queueEvent(new Runnable(){
							@Override
							public void run() {
								renderer.handleTouchPress(
										normalizedX, normalizedY);
							}
						});
					}else if(event.getAction() == MotionEvent.ACTION_MOVE){
						mGLSurfaceView.queueEvent(new Runnable() {
							
							@Override
							public void run() {
								renderer.handleTouchDrag(
										normalizedX, normalizedY);
							}
						});
					} 
//					Toast.makeText(AirHockeyActivity.this, "X =" + normalizedX + "Y =" + normalizedY, Toast.LENGTH_SHORT ).show();
					return true;
				}else{
					return false;
				}
			}
		});
      
	}
	/**
	 * 设置菜单不可用
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
