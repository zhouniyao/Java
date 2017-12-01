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
		//��Ⱦ������
		final MyRenderer renderer = new MyRenderer(TouchRolling.this,  "niming.virsualreality02:raw/camaro_obj", R.drawable.camaro);
		//��������ͼ��
//		renderer.setTexture(R.drawable.camaro); //�������ٵ�ǰcontext�����ɣ�UI�̲߳��ܵ���OpenGL API��
//		renderer.setTYPE(renderer.POINTS);
		
//		mGLSurfaceView = new MySurfaceView(this, renderer);	//����û��Ӧ ��UI�̲߳��ܵ���OpenGL API��
		
		
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // ��������OpenGL ES 2.0��������setRendererǰ
		mGLSurfaceView.setRenderer(renderer);
	    ll.addView(mGLSurfaceView);   
        mGLSurfaceView.requestFocus();//��ȡ����
        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
        
        //GLSurfaceView���ô���������Ҳ�Ǵ���û�з�Ӧ��
        mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
        	@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event != null){
					//Convert touch coordinates into normalized device coordinates,
					//keeping in mind that Android's Y coordinates are inverted.
					//��ͼ���Ͻ�ӳ��Ϊ(0,0)�����½�ӳ�䵽������Ϊ��ͼ�Ĵ�С
					final float normalizedX =
							(event.getX() / (float) v.getWidth()) * 2 - 1;
					final float normalizedY =
							-((event.getY() / (float) v.getHeight()) * 2 - 1);
					/*
					 * ���һ������¼���ѹ(press)������ק(drag)
					 * ����Ҫ��ʾ��Android��UI���������̣߳���OpenGL��GLSurfaceView������һ���������߳��У�
					 * ���������Ҫʹ���̰߳�ȫ�ļ����������̼߳�ͨѶ��
					 * ����ʹ��queueEvent()��OpenGL�̷ַ߳����ã����ڰ�ѹ�¼�������airHockeyRenderer.handleTouchPress()��
					 * ������ק�¼�������airHockeyRender.handleTouchDrag()�� 
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
	 * ���ò˵�������
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
