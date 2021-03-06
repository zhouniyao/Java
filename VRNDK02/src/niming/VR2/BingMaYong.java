package niming.VR2;

import com.example.vrndk01.R;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import niming.core.MyActivity;
import niming.core.MyGLSurfaceView;
import niming.core.MyRenderer2;
import niming.util.Constant;

public class BingMaYong extends MyActivity {
	
	private float mPreviousY;//上次的触控位置Y坐标
	private float mPreviousX;//上次的触控位置X坐标
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String packPath = getPackageName();
		/*【重要问题】类的多态，子类对象[实例]赋给父类引用变量，（编译类型和运行时类型不一致，后期绑定）不能使用子类独有的方法,注：属性不具有多态性*/
		MyGLSurfaceView mGLSurfaceView2 = new MyGLSurfaceView(this, packPath + ":raw/bingmayong_obj");
		MyGLSurfaceView.SceneRenderer renderer ;
		renderer = mGLSurfaceView2.getRenderer();
		
		renderer.setTYPE(Constant.TRIANGLES);//绘制三角面
		renderer.setScale(0.06f, 0.06f, 0.06f);//兵马俑要缩小到0.06倍
		renderer.setTranslate(0.0f, -3f, 0.0f);//向屏幕下移动
//		renderer.setScale(0.6f, 0.6f, 0.6f);//缩放
//		renderer.setTranslate(0.0f, -1f, 0.0f);//向屏幕下移动
//		renderer.setTranslate(0.0f, 0.0f, 0.0f);//向屏幕下移动
		/*下面的两种旋转方式只能选择一种*/
		renderer.isTouch = true;//跟随触碰任意转
//		renderer.isRotate = true;//给3D物体加速度，旋转起来
		
		
	    ll.addView(mGLSurfaceView2);   
	}
	
	
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		String packPath = getPackageName();
//		//渲染器对象
//		final MyRenderer2 renderer = new MyRenderer2(BingMaYong.this,  packPath + ":raw/bingmayong_obj",  R.drawable.bingmayong);
//		renderer.setTYPE(Constant.TRIANGLES);
//		renderer.setScale(0.06f, 0.06f, 0.06f);
//		renderer.setTranslate(0.0f, -3f, 0.0f);//向屏幕下移动
//		
//		mGLSurfaceView = new GLSurfaceView(this);
//		// Request an OpenGL ES 2.0 compatible context.
//		mGLSurfaceView.setEGLContextClientVersion(2);  // 这行设置OpenGL ES 2.0，必须在setRenderer前
//		mGLSurfaceView.setRenderer(renderer);
//		/*触碰事件*/
//		mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event != null){
//					final float normalizedX =
//							(event.getX() / (float) v.getWidth()) * 2 - 1;
//					final float normalizedY =
//							-((event.getY() / (float) v.getHeight()) * 2 - 1);
//					/*
//					 * 检查一下这个事件按压(press)还是拖拽(drag)
//					 * 【重要提示】Android的UI运行在主线程，而OpenGL的GLSurfaceView运行在一个单独的线程中，
//					 * 因此我们需要使用线程安全的技术在两个线程间通讯。
//					 * 我们使用queueEvent()给OpenGL线程分发调用，对于按压事件，调用Renderer.handleTouchPress()。
//					 * 对于拖拽事件，调用Renderer.handleTouchDrag()。 
//					 */
//					if (event.getAction() == MotionEvent.ACTION_DOWN) {
//						mGLSurfaceView.queueEvent(new Runnable(){
//							@Override
//							public void run() {
//								renderer.handleTouchPress(
//										normalizedX, normalizedY);
//							}
//						});
//					}else if(event.getAction() == MotionEvent.ACTION_MOVE){
//						mGLSurfaceView.queueEvent(new Runnable() {
//							@Override
//							public void run() {
//								renderer.handleTouchDrag(
//										normalizedX, normalizedY);
//							}
//						});
//					}else if(event.getAction() == MotionEvent.ACTION_UP){
//						mGLSurfaceView.queueEvent(new Runnable() {
//							
//							@Override
//							public void run() {
//								renderer.handleTouchUp(
//										normalizedX, normalizedY);
//							}
//						});
//					}
//					return true;
//				}else {
//					return false;
//				}
//				
//			}//End onTouch
//		});
//		
//		
//	    ll.addView(mGLSurfaceView);   
//	}
}
