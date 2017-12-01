package com.example.vrndk01;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import niming.core.MyActivity;
import niming.core.MyRenderer;

public class CubeObj extends MyActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��Ⱦ������
		final MyRenderer renderer = new MyRenderer(CubeObj.this,  "com.example.vrndk01:raw/cube_obj", R.drawable.taiji);
//		renderer.setRotate(0.1f, 0.0f, 1.0f, 0.0f);//��y����ת����
//		renderer.setScale(1.5f, 1.5f, 1.5f);//�������ű���
//		renderer.setTranslate(x, y, z);
		//�ӵ㡢ͶӰ������MyRenderer�������ã�һ�����ã����겻��
		
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // ��������OpenGL ES 2.0��������setRendererǰ
		mGLSurfaceView.setRenderer(renderer);
//		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
		
		/*�����¼�*/
		mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event != null){
					final float normalizedX =
							(event.getX() / (float) v.getWidth()) * 2 - 1;
					final float normalizedY =
							-((event.getY() / (float) v.getHeight()) * 2 - 1);
					/*
					 * ���һ������¼���ѹ(press)������ק(drag)
					 * ����Ҫ��ʾ��Android��UI���������̣߳���OpenGL��GLSurfaceView������һ���������߳��У�
					 * ���������Ҫʹ���̰߳�ȫ�ļ����������̼߳�ͨѶ��
					 * ����ʹ��queueEvent()��OpenGL�̷ַ߳����ã����ڰ�ѹ�¼�������Renderer.handleTouchPress()��
					 * ������ק�¼�������Renderer.handleTouchDrag()�� 
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
					}else if(event.getAction() == MotionEvent.ACTION_UP){
						mGLSurfaceView.queueEvent(new Runnable() {
							
							@Override
							public void run() {
								renderer.handleTouchUp(
										normalizedX, normalizedY);
							}
						});
					}
					return true;
				}else {
					return false;
				}
				
			}//End onTouch
		});
		
		
	    ll.addView(mGLSurfaceView);   
//        mGLSurfaceView.requestFocus();//��ȡ����
//        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
	
	}
}
