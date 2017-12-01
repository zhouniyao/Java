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
 * ���ݴ���������+���Լ��ٶ���ת   ���� ��ʧ�ܡ�
 *
 */
public class Car22 extends MyActivity {
	private float mPreviousY;//�ϴεĴ���λ��Y����
	private float mPreviousX;//�ϴεĴ���λ��X����
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String packPath = getPackageName();
		/*����Ҫ���⡿��Ķ�̬���������[ʵ��]�����������ñ��������������ͺ�����ʱ���Ͳ�һ�£����ڰ󶨣�����ʹ��������еķ���,ע�����Բ����ж�̬��*/
		MyGLSurfaceView mGLSurfaceView2 = new MyGLSurfaceView(this, packPath + ":raw/camaro_obj");
		MyGLSurfaceView.SceneRenderer renderer ;
		renderer = mGLSurfaceView2.getRenderer();
		
		renderer.setTYPE(Constant.TRIANGLES);//����������
//		renderer.setScale(0.6f, 0.6f, 0.6f);//����
//		renderer.setTranslate(0.0f, -1f, 0.0f);//����Ļ���ƶ�
//		renderer.setTranslate(0.0f, 0.0f, 0.0f);//����Ļ���ƶ�
		/*�����������ת��ʽֻ��ѡ��һ��*/
		renderer.isTouch = true;//���津������ת
//		renderer.isRotate = true;//��3D������ٶȣ���ת����
		
		
	    ll.addView(mGLSurfaceView2);   
	}
}