package niming.VR2;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;
import static niming.util.Constant.LINES;
import static niming.util.Constant.POINTS;
import static niming.util.Constant.TRIANGLES;
import static niming.util.Constant.TRIANGLE_FAN;
import static niming.util.Constant.TRIANGLE_STRIP;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.example.vrndk01.R;
import com.geometric.Ball;
import com.geometric.BezierSurface;
import com.geometric.BezierSurface2;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View.OnCreateContextMenuListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import niming.core.MyActivity;
import niming.util.Constant;
import niming.util.ShaderHelper;
import niming.util.TextResourceReader;
import niming.util.TextureHelper;

/**
 * 根据触碰任意旋+可以加速度旋转   汽车 【失败】
 *
 */
public class BezierSurfaceDemo extends MyActivity {
	private float mPreviousY;//上次的触控位置Y坐标
	private float mPreviousX;//上次的触控位置X坐标
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new GLSurfaceView(this);
        // Request an OpenGL ES 2.0 compatible context.
       mGLSurfaceView.setEGLContextClientVersion(2);            
        
        // Assign our renderer.
//        glSurfaceView.setRenderer(new AirHockeyRenderer(this));
        mGLSurfaceView.setRenderer(new SceneRenderer(this));
		// 切换到主界面
		setContentView(R.layout.activity_seekbar);	
		LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
		ll.addView(mGLSurfaceView);
        //普通拖拉条被拉动的处理代码
        SeekBar sb=(SeekBar)this.findViewById(R.id.SeekBar01);
        sb.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener()
            {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
//					mGLSurfaceView.setLightOffset((seekBar.getMax()/2.0f-progress)/(seekBar.getMax()/2.0f)*-4);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {	}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) { }            	
            }
        );

	}
}
class SceneRenderer implements GLSurfaceView.Renderer 
{
	
	private final Context context;
	Ball ball;
	BezierSurface2 bezier;
	/*定义各类变换矩阵*/
	private float[] modelMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] projectionMatrix = new float[16];
    private float[] MVMatrix = new float[16];//矩阵连乘的中间结果
    private float[] MVPMatrix = new float[16];//最终的矩阵
    /*顶点数据准备*/
    /* Store our model data in a float buffer. */
    private FloatBuffer objPositions;
    private FloatBuffer objNormalsss;
    private FloatBuffer objTextureCoodinates;
    /*openGL绑定的着色器程序ID*/
    private int program;
    /*定义常量区*/
    private static final int BYTES_PER_FLOAT = 4;
	private static final int POSITION_COMPONENT_COUNT = 2;
	/*private static final int POSITION_COMPONENT_COUNT = 4;*/
	private static final int COLOR_COMPONENT_COUNT = 3;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String A_TEXCOORD = "a_TexCoordinate";
    private static final String U_MVPMATRIX = "u_MVPMatrix";
	private static final String obj = null;
    
 
    
    /*管线装配时，各着色器变量在OpenGL内的Location*/
    private int aPositionLocation;
    private int aColorLocation;
    private int aTextureCoordLocation;
    private int uTextureUnitLoaction;
    private int uMVPMatrixLocation;
    
    /*整个屏幕的宽高px值*/
    private int screenWidth;
    private int screenHeight;
    float aspectRatio;//屏幕宽高比，宽/高 > 1
    /* 设置触控图形*/
    float touchX;
    float touchY;
    /* 设置缩放比例*/
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float scaleZ = 1f;
    /* 设置平移*/
    private float translateX = 0f;
    private float translateY = 0f;
    private float translateZ = 0f;
    /* 设置旋转 */
    private float rotateX = 0f;
    private float rotateY = 0f;//若要物体旋转，旋转矩阵必须有个参数不为0
    private float rotateZ = 0f;
    private float speed = 0f; //旋转速度
    
	public float yAngle = 0;// 绕y轴旋转的角度
	public float xAngle = 0;// 绕x轴旋转的角度
	public float zAngle = 0;// 绕z轴旋转的角度
    
    public boolean isRotate = false;//旋转起来
    public boolean isTouch = false;//跟随触碰任意转
	float rotate = 0;//物体旋转的位置，如果有速度，每次都增加速度
    /*保存触屏位置*/
    float oldX = 0f;
    float oldY = 0f;
    /*纹理ID*/
    private int texResourceId;
//  int texture1;
    int texture2;
    /**构造器中的3个初始化量*/
    int TYPE = Constant.TRIANGLES; //以什么形式绘制图形，默认是三角面绘制
    
    /**
     * 设置图形旋转
     */
    public void setRotate(float speed, float x, float y, float z) {
    	this.isRotate = true;
		this.rotateX  = x;
		this.rotateY  = y;
		this.rotateZ  = z;
		this.speed = speed;
	}
    /**
     * 设置图形整体平移
     * @param x y z
     */
    public void setTranslate(float x, float y, float z) {
        translateX += x;
        translateY += y;
        translateZ += z;
	}
    /**
     * 设置图形的缩放比例
     * @param x、y、z的缩放比例
     */
    public void setScale(float x, float y, float z) {
    	if(x <= 0 || y <= 0 || z <= 0){
    		Toast.makeText(context, "输入的比例数据有误，请重新输入",Toast.LENGTH_SHORT).show();
    		return;
    	}
        scaleX = x;
        scaleY = y;
        scaleZ = z;
	}
    
    /**
     * 设置图形输出类型
     * @param type
     */
    public void setTYPE(int type) {
    	switch (type) {
    	case 0:
			TYPE = POINTS;
			break;
		case 1:
			TYPE = LINES;
			break;
		case 2:
			type = TRIANGLE_STRIP;
			break;
		case 3:
			break;
		case 4:
			TYPE = TRIANGLES;
			break;
		case 5:
			TYPE = TRIANGLE_FAN;
			break;
		default:
			Toast.makeText(context, "输出类型错误，请选择以下类型：Constant.POINTS、Constant.LINES、FACES",Toast.LENGTH_SHORT).show();
			return;
		}
	}
	/**
	 * 渲染构造器
	 * @param 当前活动的activity
	 * @param OBJ文件objResource
	 * @param 纹理resourceID
	 */
    public SceneRenderer(MyActivity activity) {
    	this.context = activity;
	}
	/**
	 * 是否触摸到mallet's bounding sphere
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY){
		oldX = normalizedX;
		oldY = normalizedY;
//				isTouch = true;
	}
	/**
	 * 停止触摸，停止旋转
	 */
	public void handleTouchUp(float normalizedX, float normalizedY) {
//				isTouch = false;
	}
	/**
	 * 划屏时, 图形以中心为基点，旋转到需要展示的面。
	 * 触碰结束，图形静止。
	 */
	public void handleTouchDrag(float x, float y){
		speed = 0.2f;

		float tmp = x/y;
		if((int)tmp > 0){
			if(x>0){//都是正数
				if(tmp>10){rotateX = 1.0f;return;}
				if(x == y){rotateZ = 1.0f;return;}
				if(tmp<0.1f){rotateY = 1.0f;return;}
				rotateX = rotateY = 1.0f;return;
			}else{//都是负数
				speed *= -1;
			}
		}else{//一正一负
//					if(x>0){//x是正数，y是负数
//						
//					}else{//
//						
//					}
			if(tmp<-10){rotateX = 1.0f;return;}
			if(x == -y){rotateZ = 1.0f;return;}
			if(tmp>-0.1f){rotateY = 1.0f;return;}
			rotateX = rotateY = 1.0f;return;
		}
		
	}
	/**
	 * 加载纹理对象
	 */
	public void initTexture() {
		/*纹理*/
//				texture2 = TextureHelper.loadTexture(context, R.drawable.camaro);//汽车
		//加载纹理
		texture2 = TextureHelper.loadTexture(context, R.drawable.camaro);
	}
	/**
	 * 生成着色器程序，应用程序与着色器变量进行【管线装配】
	 */
	public void initShader(int vertex_shaderID, int fragment_shaderID) {
		/**
		 * 【第一步】将资源文件（着色器源代码）读取至 String
		 */
        String vertexShaderSource   = TextResourceReader
				.readTextFileFromResource(context, vertex_shaderID);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, fragment_shaderID);
		/**
		 * 【第二步】编译着色器源代码，返回OpenGL生成的ID
		 */
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		/**
		 * 【第三步】一个OpenGL程序就是把一个顶点着色器和一个片段着色器链接在一起变成单个对象，顶点着色器和片段着色器总是一起工作的。 
		 */
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		glUseProgram(program);
		/*attribute变量定位*/
		aPositionLocation = glGetAttribLocation(program, "a_Position");
		aColorLocation = glGetAttribLocation(program, "a_Color");
		aTextureCoordLocation = glGetAttribLocation(program, "a_TexCoordinate");
		/*uniform常量定位*/
		uMVPMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
		uTextureUnitLoaction = glGetUniformLocation(program, "u_TextureUnit");
		
		glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, false, 0, objPositions);
		glEnableVertexAttribArray(aPositionLocation);
		//u_Color不使用
//					glVertexAttribPointer(aColorLocation, 4, GL_FLOAT, false, 0, );
//					glEnableVertexAttribArray(aColorLocation);
		glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, objTextureCoodinates);
		glEnableVertexAttribArray(aTextureCoordLocation);
	}

	@Override
	public void onSurfaceCreated(GL10 gl,
			javax.microedition.khronos.egl.EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//黑色	//清屏用的颜色
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.0 Enable texture mapping
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		
		ball = new Ball(context);//球
		bezier = new BezierSurface2(context);
//		initShader(R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);//使用纹理着色器
		initTexture();//加载纹理对象
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
//				Log.i(TAG, "$宽3："+ width + "    " + "$高3：" + height);
		screenWidth = width;
		screenHeight= height;
		/**screenWidth/2 < screenHeight */ 
		//屏幕高-宽比(两个半屏),即aspecRatio > 1
		aspectRatio = (width/2) > height ?
				(float)(width/2)/(float)height : (float)height/(float)(width/2);

	}
	
	/**时间和旋转角度*/
	Long time;
	Long startTime = SystemClock.uptimeMillis();
	
	float angleInDegrees;
	float binocularDistance = 0.066f;//双目距离

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清屏所有颜色，用之前的glClearColor填充颜色
        // Do a complete rotation every 10 seconds.
        time = SystemClock.uptimeMillis() % 10000L;
        angleInDegrees = (360.0f / 10000.0f) * ((long) time);
        // 因为屏幕分成左右两半
        glViewport(0, 0, screenWidth/2, screenHeight);
    	// 透视投影
//					MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        // 视点位置设置
        Matrix.setLookAtM(viewMatrix, 0, -1 * binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
//			        Matrix.setLookAtM(viewMatrix, 0, -1f, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		setIdentityM(modelMatrix, 0);// 归一化
//				translateM(modelMatrix, 0, 0f, 0f, -2f);// 沿z轴平移-3
//				scaleM(modelMatrix, 0, 1f, 1f, 1f);
//				rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);//绕x轴旋转angleInDegrees
		translateM(modelMatrix, 0, translateX, translateY, translateZ - 2f);//沿z轴平移-3
		scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
		
		if(isRotate){  //旋转速度不为0时
			rotateM(modelMatrix, 0, rotate, rotateX, rotateY, rotateZ);// 旋转矩阵必须有个参数不为0
			rotate += speed;
//					MatrixStack.pushMatrix(modelMatrix);
		}else{
//					MatrixStack.popMatrix(modelMatrix);
		}
		
			
		if (isTouch) {
			rotateM(modelMatrix, 0, xAngle, 1, 0, 0);// 绕x轴转一下xAngle大小的角度 
			rotateM(modelMatrix, 0, yAngle, 0, 1, 0);// 
			rotateM(modelMatrix, 0, zAngle, 0, 0, 1);// 
		}
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	// 给图像添加纹理
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	ball.drawSelf(MVPMatrix);
    	bezier.drawSelf(MVPMatrix);
        
 /**************************************第二个物体【右边】************************************************/      
        glViewport(screenWidth/2, 0, screenWidth/2, screenHeight);
    	//透视投影
//					MatrixHelper.perspectiveM(projectionMatrix, 45, aspectRatio, 1f, 10f);	
		Matrix.frustumM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 1f, 100f);
        //重设变换矩阵
        Matrix.setLookAtM(viewMatrix, 0, binocularDistance, 0f, 2.2f, 0f, 0f, -5f, 0f, 1f, 0f);
		
		Matrix.multiplyMM(MVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
    	//给图像添加纹理
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture2);
    	glUniform1i(uTextureUnitLoaction, 0);
    	
//    	bezier.drawSelf(MVPMatrix);
    	ball.drawSelf(MVPMatrix);
    	
//		    	GLES20.glDrawElements(GL_Constant.TRIANGLES, obj.facesNum * 3, GL_UNSIGNED_Integer, objVerticesSeq);//三角面
        
//		    	speed += -0.001;// 速度的阻尼系数
    	
        //打印双目距离-->textView
//		        Integer endTime = SystemClock.uptimeMillis();
//		        if((endTime - startTime) > 1000){//每秒输出一次
//		        	DecimalFormat dFormat = new DecimalFormat("##0.000");
//		        	String msg = dFormat.format(binocularDistance);
//					Bundle bd = new Bundle();//创建Bundle对象
//					bd.putString("msg", msg);//向Bundle中添加信息，putString(String key, String value)键值对形式
//					Message tempMessage = new Message();//创建Message对象
//					tempMessage.setData(bd);//向Message中添加数据
//					tempMessage.what = 0;
//					hd.sendMessage(tempMessage);//【重】调用主控制类中的Handler对象发送消息
//		        	startTime = endTime;
//		        	binocularDistance += 0.001f;
//		        	if(binocularDistance > 2)
//		        		binocularDistance = 0.0f;
//		        }
	}//End OnDrawFrame


}



	
