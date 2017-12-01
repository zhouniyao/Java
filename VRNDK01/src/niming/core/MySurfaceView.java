package niming.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 自定义GLSurfaceView【暂时用上】
 * 
 */
public class MySurfaceView extends GLSurfaceView {
	MyRenderer renderer;
	/**
	 * 构造器需要一个renderer对象
	 * @param context
	 * @param renderer
	 */
	public MySurfaceView(Context context, GLSurfaceView.Renderer renderer) {
		super(context);
		this.renderer = (MyRenderer) renderer;
	}

//	private float mPreviousY;//上次的触控位置Y坐标
//    private float mPreviousX;//上次的触控位置X坐标
//	//触摸事件回调方法
//    @Override 
//    public boolean onTouchEvent(MotionEvent event) {
//        final float y = event.getY();
//        final float x = event.getX();
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			queueEvent(new Runnable(){
//				@Override
//				public void run() {
//					renderer.handleTouchPress(
//							x, y);
//				}
//			});
//		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
//			queueEvent(new Runnable() {
//				
//				@Override
//				public void run() {
//					renderer.handleTouchDrag(
//							x, y);
//				}
//			});
//		}
////        switch (event.getAction()) {
////        case MotionEvent.ACTION_MOVE:
////            float dy = y - mPreviousY;//计算触控笔Y位移
////            float dx = x - mPreviousX;//计算触控笔X位移
////            renderer.handleTouchDrag(x, y);
////        }
//        mPreviousY = y;//记录触控笔位置
//        mPreviousX = x;//记录触控笔位置
//        return true;
//    }
}
