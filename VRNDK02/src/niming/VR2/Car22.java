package niming.VR2;

import com.example.vrndk01.R;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import niming.core.MyActivity;
import niming.core.MyGLSurfaceView;
import niming.core.MyRenderer2;
import niming.util.Constant;
/**
 * 根据触碰任意旋+可以加速度旋转   汽车 【失败】
 *
 */
public class Car22 extends MyActivity {
	private float mPreviousY;//上次的触控位置Y坐标
	private float mPreviousX;//上次的触控位置X坐标
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String packPath = getPackageName();
		/*【重要问题】类的多态，子类对象[实例]赋给父类引用变量，（编译类型和运行时类型不一致，后期绑定）不能使用子类独有的方法,注：属性不具有多态性*/
		MyGLSurfaceView mGLSurfaceView2 = new MyGLSurfaceView(this, packPath + ":raw/camaro_obj");
		MyGLSurfaceView.SceneRenderer renderer ;
		renderer = mGLSurfaceView2.getRenderer();
		
		renderer.setTYPE(Constant.TRIANGLES);//绘制三角面
//		renderer.setScale(0.6f, 0.6f, 0.6f);//缩放
//		renderer.setTranslate(0.0f, -1f, 0.0f);//向屏幕下移动
//		renderer.setTranslate(0.0f, 0.0f, 0.0f);//向屏幕下移动
		/*下面的两种旋转方式只能选择一种*/
		renderer.isTouch = true;//跟随触碰任意转
//		renderer.isRotate = true;//给3D物体加速度，旋转起来
		
		
	    ll.addView(mGLSurfaceView2);   
	}
}
