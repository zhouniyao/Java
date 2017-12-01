package com.example.vrndk01;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import niming.core.MyActivity;
import niming.core.MyRenderer2;
import niming.util.Constant;

public class SheepObj extends MyActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��Ⱦ������
		final MyRenderer2 renderer = new MyRenderer2(SheepObj.this,  "com.example.vrndk01:raw/ram_obj",  R.drawable.char_ram_nor);
		renderer.setTYPE(Constant.TRIANGLES);
		renderer.setScale(3f, 3f, 3f);
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // ��������OpenGL ES 2.0��������setRendererǰ
		mGLSurfaceView.setRenderer(renderer);
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
	}
}
