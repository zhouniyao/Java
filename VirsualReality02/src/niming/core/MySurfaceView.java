package niming.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * �Զ���GLSurfaceView
 * 
 */
public class MySurfaceView extends GLSurfaceView implements OnTouchListener{
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
			 * ����ʹ��queueEvent()��OpenGL�̷ַ߳����ã����ڰ�ѹ�¼�������Renderer.handleTouchPress()��
			 * ������ק�¼�������Renderer.handleTouchDrag()�� 
			 */
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				queueEvent(new Runnable(){
					@Override
					public void run() {
						renderer.handleTouchPress(
								normalizedX, normalizedY);
					}
				});
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				queueEvent(new Runnable() {
					
					@Override
					public void run() {
						renderer.handleTouchDrag(
								normalizedX, normalizedY);
					}
				});
			}
//			Toast.makeText(AirHockeyActivity.this, "X =" + normalizedX + "Y =" + normalizedY, Toast.LENGTH_SHORT ).show();
			return true;
		}else{
			return false;
		}
	}
}

//public class MySurfaceView extends GLSurfaceView {
//
//	private SceneRenderer mRenderer;//������Ⱦ��
//	private float _preX = 0.0f; 
//	private float _preY = 0.0f; 
//	private final float TOUCH_SCALE_FACOTOR = 180.0f / 320; 
//	
//	public MySurfaceView(Context context) {
//		super(context);
//        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
//        mRenderer = new SceneRenderer();	//����������Ⱦ��
//        setRenderer(mRenderer);				//������Ⱦ��		        
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ  
//	}
////	public MySurfaceView(Context context) 
////	{ 
////	super(context); 
////	// TODO Auto-generated constructor stub 
////	setEGLContextClientVersion(2); 
////	this.setRenderer(new SceneRenderer()); 
////	setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); 
////	} 
//	public MySurfaceView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		float x = event.getX();
//		float y = event.getY();
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_MOVE:
//			float dx = x - _preX;
//			float dy = y - _preY;
//			mRenderer.zrot = dx * TOUCH_SCALE_FACOTOR;
//			mRenderer.xrot = dy * TOUCH_SCALE_FACOTOR;
//			this.requestRender();
//			break;
//		default:
//			break;
//		}
//		_preX = x;
//		_preY = y;
//		return true;
//	}
//
//	private class SceneRenderer implements GLSurfaceView.Renderer {
//
//		public float xrot, yrot, zrot; 
//		private static final String TAG = "niming"; 
//		/*�������任����*/
//		private float[] modelMatrix = new float[16];
//		private float[] viewMatrix = new float[16];
//		private float[] projectionMatrix = new float[16];
//	    private float[] MVMatrix = new float[16];//�������˵��м���
//	    private float[] MVPMatrix = new float[16];//���յľ���
//	    
//	  //���廷���� 
//	    private FloatBuffer lightAmbient = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 
//	    0.5f, 1.0f}); 
//	    //������ɢ�� 
//	    private FloatBuffer lightDiffuse = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 
//	    1.0f, 1.0f}); 
//	    //��Դ��λ�� 
//	    private FloatBuffer lightPosition = FloatBuffer.wrap(new float[]{0.0f, 
//	    0.0f, 2.0f, 1.0f}); 
//	    
//	    
//	    private SceneRenderer(){} //˽�й��������������⣬�������޷����������
//		@Override
//		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//			
//		}
//
//		@Override
//		public void onSurfaceChanged(GL10 gl, int width, int height) {
//			//��Ļ��߱�
//			float ratio = (float) width / height; 
//			//����OPENGL�ӿ� 
//			GLES20.glViewport(0, 0, width, height); 
//			//���þ���ͶӰ���� 
//			Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7); 
//		}
//
//		@Override
//		public void onDrawFrame(GL10 gl) {
//			//�����Ļ����Ȼ��� 
//			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | 
//			GLES20.GL_DEPTH_BUFFER_BIT); 
//			Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 
//			0.0f); 
//			
//		}
//		
//	}
//	
//	
//}
