package niming.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * �Զ���GLSurfaceView����ʱ���ϡ�
 * 
 */
public class MySurfaceView extends GLSurfaceView {
	MyRenderer renderer;
	/**
	 * ��������Ҫһ��renderer����
	 * @param context
	 * @param renderer
	 */
	public MySurfaceView(Context context, GLSurfaceView.Renderer renderer) {
		super(context);
		this.renderer = (MyRenderer) renderer;
	}

//	private float mPreviousY;//�ϴεĴ���λ��Y����
//    private float mPreviousX;//�ϴεĴ���λ��X����
//	//�����¼��ص�����
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
////            float dy = y - mPreviousY;//���㴥�ر�Yλ��
////            float dx = x - mPreviousX;//���㴥�ر�Xλ��
////            renderer.handleTouchDrag(x, y);
////        }
//        mPreviousY = y;//��¼���ر�λ��
//        mPreviousX = x;//��¼���ر�λ��
//        return true;
//    }
}
