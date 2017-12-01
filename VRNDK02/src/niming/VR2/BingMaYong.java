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
	
	private float mPreviousY;//�ϴεĴ���λ��Y����
	private float mPreviousX;//�ϴεĴ���λ��X����
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String packPath = getPackageName();
		/*����Ҫ���⡿��Ķ�̬���������[ʵ��]�����������ñ��������������ͺ�����ʱ���Ͳ�һ�£����ڰ󶨣�����ʹ��������еķ���,ע�����Բ����ж�̬��*/
		MyGLSurfaceView mGLSurfaceView2 = new MyGLSurfaceView(this, packPath + ":raw/bingmayong_obj");
		MyGLSurfaceView.SceneRenderer renderer ;
		renderer = mGLSurfaceView2.getRenderer();
		
		renderer.setTYPE(Constant.TRIANGLES);//����������
		renderer.setScale(0.06f, 0.06f, 0.06f);//����ٸҪ��С��0.06��
		renderer.setTranslate(0.0f, -3f, 0.0f);//����Ļ���ƶ�
//		renderer.setScale(0.6f, 0.6f, 0.6f);//����
//		renderer.setTranslate(0.0f, -1f, 0.0f);//����Ļ���ƶ�
//		renderer.setTranslate(0.0f, 0.0f, 0.0f);//����Ļ���ƶ�
		/*�����������ת��ʽֻ��ѡ��һ��*/
		renderer.isTouch = true;//���津������ת
//		renderer.isRotate = true;//��3D������ٶȣ���ת����
		
		
	    ll.addView(mGLSurfaceView2);   
	}
	
	
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		String packPath = getPackageName();
//		//��Ⱦ������
//		final MyRenderer2 renderer = new MyRenderer2(BingMaYong.this,  packPath + ":raw/bingmayong_obj",  R.drawable.bingmayong);
//		renderer.setTYPE(Constant.TRIANGLES);
//		renderer.setScale(0.06f, 0.06f, 0.06f);
//		renderer.setTranslate(0.0f, -3f, 0.0f);//����Ļ���ƶ�
//		
//		mGLSurfaceView = new GLSurfaceView(this);
//		// Request an OpenGL ES 2.0 compatible context.
//		mGLSurfaceView.setEGLContextClientVersion(2);  // ��������OpenGL ES 2.0��������setRendererǰ
//		mGLSurfaceView.setRenderer(renderer);
//		/*�����¼�*/
//		mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event != null){
//					final float normalizedX =
//							(event.getX() / (float) v.getWidth()) * 2 - 1;
//					final float normalizedY =
//							-((event.getY() / (float) v.getHeight()) * 2 - 1);
//					/*
//					 * ���һ������¼���ѹ(press)������ק(drag)
//					 * ����Ҫ��ʾ��Android��UI���������̣߳���OpenGL��GLSurfaceView������һ���������߳��У�
//					 * ���������Ҫʹ���̰߳�ȫ�ļ����������̼߳�ͨѶ��
//					 * ����ʹ��queueEvent()��OpenGL�̷ַ߳����ã����ڰ�ѹ�¼�������Renderer.handleTouchPress()��
//					 * ������ק�¼�������Renderer.handleTouchDrag()�� 
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