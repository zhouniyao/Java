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
 * ���ݴ���������+���Լ��ٶ���ת   ���� 
 *
 */
public class Car extends MyActivity {
	private float mPreviousY;//�ϴεĴ���λ��Y����
	private float mPreviousX;//�ϴεĴ���λ��X����
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String packPath = getPackageName();
		//��Ⱦ������
		final MyRenderer4 renderer = new MyRenderer4(Car.this,  packPath + ":raw/camaro_obj");
		renderer.setTYPE(Constant.TRIANGLES);
//		renderer.setScale(0.06f, 0.06f, 0.06f);//����
//		renderer.setTranslate(0.0f, -3f, 0.0f);//����Ļ���ƶ�
		renderer.setTranslate(0.0f, 0.0f, 0.0f);//����Ļ���ƶ�
		/*�����������ת��ʽ����ͬʱѡ��*/
//		renderer.isTouch = true;//���津������ת
		renderer.isRotate = false;//��3D������ٶȣ���ת����
		
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // ��������OpenGL ES 2.0��������setRendererǰ
		mGLSurfaceView.setRenderer(renderer);
		
		/*�����¼�*/
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
			            float dy = y - mPreviousY;//���㴥�ر�Yλ��
			            float dx = x - mPreviousX;//���㴥�ر�Xλ�� 
			            if(renderer.isTouch){
			            	/*��Ӷ������㣬�����������ٶȣ����ֿ�������*/
				            renderer.yAngle += dx * TOUCH_SCALE_FACTOR;//���������Բ��y����ת�ĽǶ�
				            renderer.xAngle += dy * TOUCH_SCALE_FACTOR;//���������Բ��x����ת�ĽǶ�
			            }
			            if(renderer.isRotate){
			            	renderer.handleTouchDrag(dx, dy);////��3D������ٶȣ���ת����
			            }
			        }
			        mPreviousY = y;//��¼���ر�λ��
			        mPreviousX = x;//��¼���ر�λ��
					return true;
				}else {
					return false;
				}
				
			}//End onTouch
		});
		
		
	    ll.addView(mGLSurfaceView);   
	}
}
