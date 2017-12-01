package niming.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 自定义GLSurfaceView
 * 
 */
public class MySurfaceView extends GLSurfaceView implements OnTouchListener{
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event != null){
			//Convert touch coordinates into normalized device coordinates,
			//keeping in mind that Android's Y coordinates are inverted.
			//视图左上角映射为(0,0)，右下角映射到的坐标为视图的大小
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
//	private SceneRenderer mRenderer;//场景渲染器
//	private float _preX = 0.0f; 
//	private float _preY = 0.0f; 
//	private final float TOUCH_SCALE_FACOTOR = 180.0f / 320; 
//	
//	public MySurfaceView(Context context) {
//		super(context);
//        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
//        mRenderer = new SceneRenderer();	//创建场景渲染器
//        setRenderer(mRenderer);				//设置渲染器		        
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染  
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
//		/*定义各类变换矩阵*/
//		private float[] modelMatrix = new float[16];
//		private float[] viewMatrix = new float[16];
//		private float[] projectionMatrix = new float[16];
//	    private float[] MVMatrix = new float[16];//矩阵连乘的中间结果
//	    private float[] MVPMatrix = new float[16];//最终的矩阵
//	    
//	  //定义环境光 
//	    private FloatBuffer lightAmbient = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 
//	    0.5f, 1.0f}); 
//	    //定义漫散射 
//	    private FloatBuffer lightDiffuse = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 
//	    1.0f, 1.0f}); 
//	    //光源的位置 
//	    private FloatBuffer lightPosition = FloatBuffer.wrap(new float[]{0.0f, 
//	    0.0f, 2.0f, 1.0f}); 
//	    
//	    
//	    private SceneRenderer(){} //私有构造器，除本类外，其他类无法定义其对象
//		@Override
//		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//			
//		}
//
//		@Override
//		public void onSurfaceChanged(GL10 gl, int width, int height) {
//			//屏幕宽高比
//			float ratio = (float) width / height; 
//			//设置OPENGL视口 
//			GLES20.glViewport(0, 0, width, height); 
//			//设置矩阵投影参数 
//			Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7); 
//		}
//
//		@Override
//		public void onDrawFrame(GL10 gl) {
//			//清楚屏幕和深度缓存 
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
