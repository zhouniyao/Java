package niming.VR2;

import com.example.vrndk01.R;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import niming.core.MyRenderer4;
import niming.core.MyActivity;
import niming.core.MyRenderer2;
import niming.util.Constant;
/**
 * 根据触碰任意旋+可以加速度旋转   汽车 
 *
 */
public class Car extends MyActivity {
	private float mPreviousY;//上次的触控位置Y坐标
	private float mPreviousX;//上次的触控位置X坐标
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String packPath = getPackageName();
		//渲染器对象
		final MyRenderer4 renderer = new MyRenderer4(Car.this,  packPath + ":raw/camaro_obj");
		renderer.setTYPE(Constant.TRIANGLES);
//		renderer.setScale(0.06f, 0.06f, 0.06f);//缩放
//		renderer.setTranslate(0.0f, -3f, 0.0f);//向屏幕下移动
		renderer.setTranslate(0.0f, 0.0f, 0.0f);//向屏幕下移动
		/*下面的两种旋转方式可以同时选择*/
//		renderer.isTouch = true;//跟随触碰任意转
		renderer.isRotate = false;//给3D物体加速度，旋转起来
		
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // 这行设置OpenGL ES 2.0，必须在setRenderer前
		mGLSurfaceView.setRenderer(renderer);
		
		/*触碰事件*/
		mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e != null){
					float y = e.getY();
			        float x = e.getX();
			        switch (e.getAction()) {
			        case MotionEvent.ACTION_DOWN:
			        	mPreviousX = x;
			        	mPreviousY = y;
			        	break;
			        case MotionEvent.ACTION_MOVE:
			            float dy = y - mPreviousY;//计算触控笔Y位移
			            float dx = x - mPreviousX;//计算触控笔X位移 
			            if(renderer.isTouch){
			            	/*添加额外运算，将减缓交互速度，出现卡顿现象*/
				            renderer.yAngle += dx * TOUCH_SCALE_FACTOR;//设置填充椭圆绕y轴旋转的角度
				            renderer.xAngle += dy * TOUCH_SCALE_FACTOR;//设置填充椭圆绕x轴旋转的角度
			            }
			            if(renderer.isRotate){
			            	renderer.handleTouchDrag(dx, dy);////给3D物体加速度，旋转起来
			            }
			        }
			        mPreviousY = y;//记录触控笔位置
			        mPreviousX = x;//记录触控笔位置
					return true;
				}else {
					return false;
				}
				
			}//End onTouch
		});
		
		
	    ll.addView(mGLSurfaceView);   
	}
}
