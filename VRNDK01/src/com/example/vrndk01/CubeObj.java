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
		//渲染器对象
		final MyRenderer renderer = new MyRenderer(CubeObj.this,  "com.example.vrndk01:raw/cube_obj", R.drawable.taiji);
//		renderer.setRotate(0.1f, 0.0f, 1.0f, 0.0f);//绕y轴旋转物体
//		renderer.setScale(1.5f, 1.5f, 1.5f);//设置缩放比例
//		renderer.setTranslate(x, y, z);
		//视点、投影矩阵，在MyRenderer类中设置，一次设置，万年不变
		
		mGLSurfaceView = new GLSurfaceView(this);
		// Request an OpenGL ES 2.0 compatible context.
		mGLSurfaceView.setEGLContextClientVersion(2);  // 这行设置OpenGL ES 2.0，必须在setRenderer前
		mGLSurfaceView.setRenderer(renderer);
//		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
		
		/*触碰事件*/
		mGLSurfaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event != null){
					final float normalizedX =
							(event.getX() / (float) v.getWidth()) * 2 - 1;
					final float normalizedY =
							-((event.getY() / (float) v.getHeight()) * 2 - 1);
					/*
					 * 检查一下这个事件按压(press)还是拖拽(drag)
					 * 【重要提示】Android的UI运行在主线程，而OpenGL的GLSurfaceView运行在一个单独的线程中，
					 * 因此我们需要使用线程安全的技术在两个线程间通讯。
					 * 我们使用queueEvent()给OpenGL线程分发调用，对于按压事件，调用Renderer.handleTouchPress()。
					 * 对于拖拽事件，调用Renderer.handleTouchDrag()。 
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
//        mGLSurfaceView.requestFocus();//获取焦点
//        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
	
	}
}
